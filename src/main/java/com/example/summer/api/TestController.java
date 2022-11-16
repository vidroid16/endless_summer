package com.example.summer.api;

import com.amazonaws.auth.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.example.summer.database.EntityManager;
import com.example.summer.dto.SlideToAddDto;
import com.example.summer.dto.TransitionDto;
import com.example.summer.model.Novel;
import com.example.summer.model.Slide;
import com.example.summer.model.Transition;
import com.example.summer.service.NovelService;
import com.example.summer.service.SlideService;
import com.example.summer.util.ThrowingConsumer;
import com.yandex.ydb.table.query.Params;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
public class TestController {
    @Autowired
    NovelService novelService;
    @Autowired
    SlideService slideService;

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
    @PostMapping("/graph")
    public ResponseEntity testGraph() {

        DirectedGraph<String, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        directedGraph.addVertex("v1");
        directedGraph.addVertex("v2");
        directedGraph.addVertex("v3");
        directedGraph.addVertex("v4");
        directedGraph.addVertex("v5");
        directedGraph.addVertex("v6");
        directedGraph.addVertex("v7");
        directedGraph.addVertex("v8");

        directedGraph.addEdge("v1", "v2");
        directedGraph.addEdge("v1", "v3");
        directedGraph.addEdge("v1", "v4");
        directedGraph.addEdge("v1", "v4");
        directedGraph.addEdge("v2", "v5");
        directedGraph.addEdge("v3", "v6");
        directedGraph.addEdge("v5", "v7");

        directedGraph.removeVertex("v3");

        return new ResponseEntity(directedGraph.toString(), HttpStatus.OK);
    }
    @PostMapping("/novel")
    public ResponseEntity<String> rarara(){
        Novel novel =new Novel("ydbbucket");
        novelService.createNovel(novel);
        return new ResponseEntity(HttpStatus.OK);
    }
    @PostMapping(path = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity ima(@ModelAttribute SlideToAddDto dto){
        InputStream url = slideService.createSlide(dto);
        return new ResponseEntity(url,HttpStatus.OK);
    }
    @PostMapping("/edge/{idParent}/{idChild}")
    public ResponseEntity setEdge(@PathVariable("idParent") String idParent, @PathVariable("idChild") String idChild){
        slideService.setParent(idChild,idParent);
        return ResponseEntity.ok().build();
    }
    @GetMapping(path = "/slide")
    public ResponseEntity getSlideWithChilds(){
        return null;
    }
    @PostMapping("/transition")
    public ResponseEntity setTransition(@RequestBody TransitionDto dto){
        slideService.setTransition(dto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/transition/{id}")
    public ResponseEntity getTransitionsById(@PathVariable("id") String slideId){
        List<Transition> transitions = slideService.getTransitionsOfSlide(slideId);
        return new ResponseEntity(transitions,HttpStatus.OK);
    }
    @GetMapping("/slides/next/{idTransition}")
    public ResponseEntity getNextSlide(@PathVariable("idTransition") String transitionId){
        Slide slide = slideService.getNextSlide(transitionId);
        return new ResponseEntity(slide,HttpStatus.OK);
    }
}
