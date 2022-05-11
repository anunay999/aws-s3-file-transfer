package com.sfdc.cloud.loader;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfdc.cloud.model.AWSS3ClientConfig;
import java.io.IOException;
import java.nio.file.Path;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class AWSS3ClientConfigLoader {

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private final AWSS3ClientConfig awss3ClientConfig;

    public AWSS3ClientConfigLoader(Path configPath) throws IOException {
        objectMapper.findAndRegisterModules();
        this.awss3ClientConfig = configPath != null ? objectMapper.readValue(configPath.toFile(),AWSS3ClientConfig.class)
                : new DefaultAWSClientConfig().setDefault();
    }

    public AWSS3ClientConfigLoader(@JsonProperty(required = true, value = "multipartUploadThresholdSizeBytes") long multipartUploadThresholdSize,
                                   @JsonProperty(required = true, value = "multipartUploadMinSizeBytes") long multipartUploadMinSize,
                                   @JsonProperty(required = true, value = "disableParallelUpload") Boolean disableParallelUpload,
                                   @JsonProperty(required = true, value = "maxTransferThreadCount") int maxTransferThreadCount,
                                   @JsonProperty(required = true, value = "maxConnectionRetryCount") int maxConnectionRetryCount,
                                   @JsonProperty(required = true, value = "maxConnectionTimeoutMs") int maxConnectionTimeoutM){
        this.awss3ClientConfig = new AWSS3ClientConfig(multipartUploadThresholdSize
                , multipartUploadMinSize, disableParallelUpload, maxTransferThreadCount,
                maxConnectionRetryCount,maxConnectionTimeoutM);
    }

    public AWSS3ClientConfig getAwss3ClientConfig() {
        return awss3ClientConfig;
    }
}

class DefaultAWSClientConfig extends AWSS3ClientConfig{

    DefaultAWSClientConfig setDefault(){
        this.setDisableParallelUpload(false);
        this.setMaxConnectionRetryCount(100);
        this.setMaxConnectionTimeoutMs(10000);
        this.setMaxTransferThreadCount(10);
        this.setMultipartUploadMinSizeBytes(0);
        this.setMultipartUploadThresholdSizeBytes(1073741824);
        return this;
    }

}
