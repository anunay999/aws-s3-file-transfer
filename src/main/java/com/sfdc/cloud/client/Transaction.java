package com.sfdc.cloud.client;

import com.sfdc.cloud.Main;
import com.sfdc.cloud.loader.AWSS3ClientConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Transaction implements ObjectTransaction{

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final String bucketName;
    private final String sourceDir;
    private final String directoryPrefix;
    private final AWSS3Client awss3Client;
    private Map<String, TransactionHandler> transactionHandlerMap;

    public Transaction(String s3Region, String bucketName, String sourceDir, String directoryPrefix, Path configPath) throws IOException {
        this.bucketName = bucketName;
        this.sourceDir = sourceDir;
        this.directoryPrefix = directoryPrefix;
        this.awss3Client = new AWSS3Client(new AWSS3ClientConfigLoader(configPath).getAwss3ClientConfig(), s3Region);
        this.initiateModes();
    }

    @Override
    public void run(String mode) {
        try{
            transactionHandlerMap.get(mode).handle(this);
            awss3Client.shutDown();
            logger.info("Shutting down aws clients");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initiateModes(){
        this.transactionHandlerMap = new HashMap<>();
        //feel free to factor these out to their own class or
        //if using Java 8 use the new Lambda syntax
        transactionHandlerMap.put("upload", transaction -> {
            logger.info(String.format("Uploading %s/%s to bucket(%s)",directoryPrefix, sourceDir,bucketName));
            transaction.awss3Client.uploadDirectory(transaction.bucketName, transaction.directoryPrefix, transaction.sourceDir);
            logger.info("Completed Successfully!");
        });
        transactionHandlerMap.put("download", transaction -> {
            logger.info(String.format("Downloading %s/%s to source (%s)",bucketName, directoryPrefix,sourceDir));
            transaction.awss3Client.downloadDirectory(transaction.bucketName, transaction.directoryPrefix, transaction.sourceDir);
            logger.info("Completed Successfully!");
        });
    }
}
