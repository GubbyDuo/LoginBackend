package com.gubbyduo.ReactBackend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@PropertySource("file:src/main/resources/.env")
public class AWSConfig {
	
	@Value("${aws_access_key_id}")
	private String keyId;
	@Value("${aws_secret_access_key}")
	private String accessKey;
	
	public AWSCredentials credentials() {		
		AWSCredentials credentials = new BasicAWSCredentials(keyId,accessKey);
		return credentials;
	}
	
	@Bean
	public AmazonS3 amazonS3() {
		AmazonS3 s3Client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials()))
				.withRegion(Regions.AP_SOUTHEAST_2)
				.build();
		return s3Client;
	}
}
