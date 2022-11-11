package com.example.summer.api;

import com.amazonaws.auth.*;
import com.amazonaws.services.codepipeline.model.AWSSessionCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.example.summer.database.EntityManager;
import com.example.summer.model.Slide;
import com.example.summer.model.User;
import com.example.summer.service.SlideService;
import com.example.summer.util.ThrowingConsumer;
import com.google.common.collect.ImmutableList;
import com.yandex.ydb.auth.iam.CloudAuthProvider;
import com.yandex.ydb.core.grpc.GrpcTransport;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.description.TableDescription;
import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import com.yandex.ydb.table.transaction.TxControl;
import com.yandex.ydb.table.values.PrimitiveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;
import yandex.cloud.sdk.auth.provider.ApiKeyCredentialProvider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
public class TestController {

    private final SlideService slideService;

    @Autowired
    public TestController(SlideService slideService) {
        this.slideService = slideService;
    }

    @PostMapping("/test1")
    public ResponseEntity<String> doTest() {
        return new ResponseEntity("TEST", HttpStatus.OK);
    }

    @PostMapping("/s3")
    public ResponseEntity testS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("YCAJEv9mFuSu6PDYR9k0WyZ_E", "YCOIoFuTunsdFjuTCbvb7wiQOktqWjSU71IRuPKq");
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
        Bucket bucket = s3.createBucket("spring");
        return new ResponseEntity<>(bucket.toString(), HttpStatus.OK);
    }

    @PostMapping("/bd")
    public ResponseEntity testBd() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("YCAJEv9mFuSu6PDYR9k0WyZ_E", "YCOIoFuTunsdFjuTCbvb7wiQOktqWjSU71IRuPKq");
        AmazonDynamoDB amazonDynamoDB
                = new AmazonDynamoDBClient(awsCreds);
        amazonDynamoDB.setEndpoint("https://docapi.serverless.yandexcloud.net/ru-central1/b1g2r9itt925gce1e2je/etnrflq3ol8cgs7rqdbk");
        DynamoDB db = new DynamoDB(amazonDynamoDB);
        TableCollection<ListTablesResult> tables = db.listTables();
        Iterator<Table> iterator = tables.iterator();

        while (iterator.hasNext()) {
            Table table = iterator.next();
            System.out.println(table.getTableName());
        }
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @PostMapping("/slide")
    public ResponseEntity testSlide() {
        Slide slide = new Slide();
        User user = new User();
        user.setUsername("shalya");
        user.setPassword("password");
        slide.setUser(user);
        slide.setLink("http://lalala.com");
        slideService.saveSlide(slide);
        return new ResponseEntity(slide, HttpStatus.OK);
    }

    @PostMapping("/ydb")
    public ResponseEntity testYdb() {

        EntityManager entityManager = new EntityManager();

        var tasks = new ArrayList<Integer>();
        entityManager.execute("select * from Users", Params.empty(), ThrowingConsumer.unchecked(result -> {
            var resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                tasks.add(resultSet.getColumnCount());
            }
        }));
        return new ResponseEntity(tasks, HttpStatus.OK);
    }
}
