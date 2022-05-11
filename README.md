# AWS S3 File Transfer Utility

## Overview

AWS Utility JAR which can upload or download a specified directory from/to desired S3 bucket.

## Build
Before you compile , you will need few things
- A java 8 compatible JDK (Java Development Kit)
- [Maven](https://maven.apache.org/install.html) Installation

## Quick Setup Guide
Clone this repository and the modules using
```
mvn clean package
```

## Execution

* Make sure the AWS environment variables are set.
    * `AWS_ACCESS_KEY_ID`
    * `AWS_SECRET_ACCESS_KEY`
    * `AWS_SESSION_TOKEN`
    * One other option is if you are using Unix based OS then
        * Update or add a credential profile in the credentials file located at (`~/.aws/credentials`).

## Usage
```
java -jar aws-s3-file-transfer.jar -r <s3_region> -d <source_dir> -p <dir_prefix> -b <bucket_name> -m <mode> -c <config.yaml> 
```

### Accepted arguments

```
 -b,--bucket <arg>   S3 Bucket Name
 -c,--config <arg>   S3 Client Configurations
 -d,--dir <arg>      Source directory to upload/download
 -m,--mode <arg>     upload / download
 -p,--prefix <arg>   S3 Directory Prefix
 -r,--region <arg>   S3 Region

```

### Sample Client Configuration
#### config.yaml
```yaml
multipartUploadThresholdSizeBytes: 1073741824 # the size threshold in bytes for when to use multipart uploads
multipartUploadMinSizeBytes: 0 # the minimum part size for upload parts
disableParallelUpload: false # the option to disable parallel downloads
maxTransferThreadCount: 10 # maximum number of transfer threads
maxConnectionRetryCount: 100
maxConnectionTimeoutMs: 10000
```



