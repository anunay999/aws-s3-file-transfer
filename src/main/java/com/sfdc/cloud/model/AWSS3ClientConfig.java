package com.sfdc.cloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AWSS3ClientConfig {

    private long multipartUploadThresholdSizeBytes; //the size threshold in bytes for when to use multipart uploads
    private long multipartUploadMinSizeBytes; //the minimum part size for upload parts
    private Boolean disableParallelUpload; //the option to disable parallel downloads
    private int maxTransferThreadCount; // maximum number of transfer threads
    private int maxConnectionRetryCount;
    private int maxConnectionTimeoutMs;

    public AWSS3ClientConfig() {}

    @SuppressWarnings("unused") // Jackson
    public AWSS3ClientConfig(@JsonProperty(required = true, value = "multipartUploadThresholdSizeBytes") long multipartUploadThresholdSize,
                             @JsonProperty(required = true, value = "multipartUploadMinSizeBytes") long multipartUploadMinSize,
                             @JsonProperty(required = true, value = "disableParallelUpload") Boolean disableParallelUpload,
                             @JsonProperty(required = true, value = "maxTransferThreadCount") int maxTransferThreadCount,
                             @JsonProperty(required = true, value = "maxConnectionRetryCount") int maxConnectionRetryCount,
                             @JsonProperty(required = true, value = "maxConnectionTimeoutMs") int maxConnectionTimeoutMs) {

        this.multipartUploadThresholdSizeBytes = multipartUploadThresholdSize;
        this.multipartUploadMinSizeBytes = multipartUploadMinSize;
        this.disableParallelUpload = disableParallelUpload;
        this.maxTransferThreadCount = maxTransferThreadCount;
        this.maxConnectionRetryCount = maxConnectionRetryCount;
        this.maxConnectionTimeoutMs = maxConnectionTimeoutMs;
    }

    public long getMultipartUploadThresholdSizeBytes() {
        return multipartUploadThresholdSizeBytes;
    }

    public void setMultipartUploadThresholdSizeBytes(long multipartUploadThresholdSizeBytes) {
        this.multipartUploadThresholdSizeBytes = multipartUploadThresholdSizeBytes;
    }

    public long getMultipartUploadMinSizeBytes() {
        return multipartUploadMinSizeBytes;
    }

    public void setMultipartUploadMinSizeBytes(long multipartUploadMinSizeBytes) {
        this.multipartUploadMinSizeBytes = multipartUploadMinSizeBytes;
    }

    public Boolean getDisableParallelUpload() {
        return disableParallelUpload;
    }

    public void setDisableParallelUpload(Boolean disableParallelUpload) {
        this.disableParallelUpload = disableParallelUpload;
    }

    public int getMaxTransferThreadCount() {
        return maxTransferThreadCount;
    }

    public void setMaxTransferThreadCount(int maxTransferThreadCount) {
        this.maxTransferThreadCount = maxTransferThreadCount;
    }

    public int getMaxConnectionRetryCount() {
        return maxConnectionRetryCount;
    }

    public void setMaxConnectionRetryCount(int maxConnectionRetryCount) {
        this.maxConnectionRetryCount = maxConnectionRetryCount;
    }

    public int getMaxConnectionTimeoutMs() {
        return maxConnectionTimeoutMs;
    }

    public void setMaxConnectionTimeoutMs(int maxConnectionTimeoutMs) {
        this.maxConnectionTimeoutMs = maxConnectionTimeoutMs;
    }


    @Override
    public String toString() {
        return "AWSS3ClientConfig{" +
                "multipartUploadThresholdSizeBytes=" + multipartUploadThresholdSizeBytes +
                ", multipartUploadMinSizeBytes=" + multipartUploadMinSizeBytes +
                ", disableParallelUpload=" + disableParallelUpload +
                ", maxTransferThreadCount=" + maxTransferThreadCount +
                ", maxConnectionRetryCount=" + maxConnectionRetryCount +
                ", maxConnectionTimeoutMs=" + maxConnectionTimeoutMs +
                '}';
    }
}
