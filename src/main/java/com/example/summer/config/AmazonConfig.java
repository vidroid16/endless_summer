package com.example.summer.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;
import yandex.cloud.sdk.auth.provider.ApiKeyCredentialProvider;

import java.time.Duration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.example.summer.repository")
public class AmazonConfig {
    @Bean
    public BasicAWSCredentials getAWSCreds(){
        return new BasicAWSCredentials("YCAJEv9mFuSu6PDYR9k0WyZ_E", "YCOIoFuTunsdFjuTCbvb7wiQOktqWjSU71IRuPKq");
    }
    @Bean(name = "amazonDynamoDB")
    public AmazonDynamoDB getAmazonDB(){
        AmazonDynamoDB amazonDynamoDB
                = new AmazonDynamoDBClient(getAWSCreds());
        amazonDynamoDB.setEndpoint("https://docapi.serverless.yandexcloud.net/ru-central1/b1g2r9itt925gce1e2je/etnrflq3ol8cgs7rqdbk");
        DynamoDB db = new DynamoDB(amazonDynamoDB);
        return amazonDynamoDB;
    }
    @Bean
    public DynamoDB getDB(){
       DynamoDB db = new DynamoDB(getAmazonDB());
       return db;
    }

    @Bean
    public Integer getYDBSession(){
        return 32;
    }
}
