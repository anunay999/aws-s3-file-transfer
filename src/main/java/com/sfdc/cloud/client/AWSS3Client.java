package com.sfdc.cloud.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.sfdc.cloud.model.AWSS3ClientConfig;
import com.sfdc.cloud.status.XferMgrProgress;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSS3Client {

    private static final Logger logger = LoggerFactory.getLogger(AWSS3Client.class);

    private final String s3Region;
    private final AmazonS3 s3Client;
    private final AWSS3ClientConfig awss3ClientConfig;
    private TransferManager transferManager;
    private ThreadPoolExecutor threadPoolExecutor;

    public AWSS3Client(AWSS3ClientConfig config, String s3Region){
        this.awss3ClientConfig = config;
        this.s3Region = s3Region;
        this.s3Client = getAWSS3Client();
    }


    /**
     * Uploads the source directory to specified S3 Bucket
     */
    public void uploadDirectory(String bucketName, String virtualDirectoryKeyPath, String sourceDirectory){
        try {
            this.createBucketIfNotExist(bucketName);
            MultipleFileUpload multipleFileUpload = this.getTransferManager().uploadDirectory(bucketName,
                    virtualDirectoryKeyPath, new File(sourceDirectory), true);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(multipleFileUpload);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(multipleFileUpload);
            // or block with Transfer.waitForCompletion()
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public void downloadDirectory(String bucketName, String virtualDirectoryKeyPath, String sourceDirectory){
        try {
            try {
                MultipleFileDownload xfer = this.getTransferManager().downloadDirectory(
                        bucketName, virtualDirectoryKeyPath, new File(sourceDirectory));
                // loop with Transfer.isDone()
                XferMgrProgress.showTransferProgress(xfer);
                // or block with Transfer.waitForCompletion()
                XferMgrProgress.waitForCompletion(xfer);
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

    }

    /**
     *
     * @return TransferManager which helps in uploading files and directories to S3
     */

    private TransferManager getTransferManager() {
        if (this.transferManager != null) {
            return this.transferManager;
        }
        // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/transfer/TransferManagerBuilder.html
        int threadCount = awss3ClientConfig.getMaxTransferThreadCount();
        this.threadPoolExecutor = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("s3transfer-%d").build(), new ThreadPoolExecutor.AbortPolicy());

        // https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/transfer/TransferManagerBuilder.html
        this.transferManager = TransferManagerBuilder.standard()
                .withS3Client(this.s3Client)
                // Uploads over this size will automatically use a multipart upload strategy,
                // while uploads smaller than this threshold will use a single connection to upload the whole object.
                .withMultipartUploadThreshold(awss3ClientConfig.getMultipartUploadThresholdSizeBytes())
                // Decreasing the minimum part size will cause multipart uploads to be split into a larger number of smaller parts
                .withMinimumUploadPartSize(awss3ClientConfig.getMultipartUploadMinSizeBytes())
                .withDisableParallelDownloads(awss3ClientConfig.getDisableParallelUpload())
                .withExecutorFactory(() -> threadPoolExecutor)
                .build();
        return this.transferManager;
    }

    private void createBucketIfNotExist(String bucketName){
        if (s3Client.doesBucketExistV2(bucketName)) {
            logger.info(String.format("Bucket %s already exists.\n", bucketName));
        } else {
            try {
                s3Client.createBucket(bucketName);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
    }

    /**
     *
     * @return Returns S3 client
     */
    private AmazonS3 getAWSS3Client() {
        if(this.s3Client != null){
            return this.s3Client;
        }
        AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion("us-east-2");
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxErrorRetry(awss3ClientConfig.getMaxConnectionRetryCount());
        clientConfiguration.setConnectionTimeout(awss3ClientConfig.getMaxConnectionTimeoutMs());
        clientConfiguration.setProtocol(Protocol.HTTPS);

        // Max connection should be same as number of threads in Transfer thread pool
        // refer https://github.com/aws/aws-sdk-java/issues/1405
        clientConfiguration.withMaxConnections(awss3ClientConfig.getMaxTransferThreadCount());

        return s3ClientBuilder.withClientConfiguration(clientConfiguration)
                .build();
    }

    /**
     * These shutdowns the transfer managers which shuts down the executor service and close the threads
     * NOTE: This should only be called when all the downloads/uploads are complete; else it will interrupt
     * and fail the ongoing downloads/uploads.
     */
    public void shutDown() {
        if (transferManager != null) {
            this.getTransferManager().shutdownNow();
        }
        if (s3Client != null) {
            this.getAWSS3Client().shutdown();
        }
    }

}
