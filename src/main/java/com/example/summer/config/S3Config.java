package com.example.summer.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Bean(name = "s3")
    public AmazonS3 s3(){
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("YCAJEv9mFuSu6PDYR9k0WyZ_E", "YCOIoFuTunsdFjuTCbvb7wiQOktqWjSU71IRuPKq");
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
        return s3;
    }
}
