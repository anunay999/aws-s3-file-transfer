package com.sfdc.cloud;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.sfdc.cloud.client.Transaction;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Options options = new Options();

    static {
        options.addRequiredOption("b", "bucket", true, "S3 Bucket Name");
        options.addOption("d", "dir", true, "Source directory to upload");
        options.addOption("p", "prefix", true, "S3 Directory Prefix");
        options.addRequiredOption("m", "mode", true, "Upload/Download");
        options.addOption("c", "config", true, "S3 Client Configurations");
        options.addOption("r", "region", true, "S3 Region");

    }

    public static void main(String[] args){
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */

        CommandLine command;
        String bucketName;
        String sourceDir;
        String directoryPrefix;
        String s3Region;
        String mode;
        Path configPath;

        try {
            command = new DefaultParser().parse(options, args);
            bucketName = command.getOptionValue('b');
            sourceDir = command.getOptionValue('d');
            directoryPrefix = command.getOptionValue('p');
            mode = command.getOptionValue('m');
            s3Region = command.getOptionValue('r') != null ? command.getOptionValue('r') : "us-east-2";
            configPath = Paths.get(command.getOptionValue('c'));

            logger.info(String.format("Bucket Name: %s\nSource Directory: %s\nDirectory Prefix: %s",bucketName,sourceDir,directoryPrefix));

            Transaction transaction = new Transaction(s3Region, bucketName, sourceDir, directoryPrefix, configPath);
            transaction.run(mode);

        } catch (Exception e) {
            new HelpFormatter().printHelp("Command Line Arguments", options);
            logger.error("Invalid arguments passed.");
            System.exit(1);
        }

    }

}
