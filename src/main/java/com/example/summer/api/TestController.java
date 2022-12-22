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
import com.example.summer.dto.StartNovelDto;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TestController {
    @Autowired
    NovelService novelService;
    @Autowired
    SlideService slideService;

    @PostMapping("/novel/{name}")
    public ResponseEntity<String> rarara(@PathVariable("name") String name){
        Novel novel =new Novel(name);
        String id = novelService.createNovel(novel);
        return new ResponseEntity(id, HttpStatus.OK);
    }
    @PostMapping(path = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity ima(@ModelAttribute SlideToAddDto dto){
        InputStream url = slideService.createSlide(dto);
        return new ResponseEntity(HttpStatus.OK);
    }
    @PostMapping("/edge/{idParent}/{idChild}")
    public ResponseEntity setEdge(@PathVariable("idParent") String idParent, @PathVariable("idChild") String idChild){
        slideService.setParent(idParent,idChild);
        return ResponseEntity.ok().build();
    }
    @GetMapping(path = "/novel/{id}")
    public ResponseEntity getAllNovelSlides(@PathVariable("id") String id){
        return new ResponseEntity(slideService.getAllSlidesByNovelId(id),HttpStatus.OK);
    }
    @GetMapping(path = "/novels")
    public ResponseEntity getAllNovels(){
        return new ResponseEntity(novelService.getAllNovels(),HttpStatus.OK);
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
    @PostMapping("/novels/{novelId}/start/{start-slide-id}")
    public ResponseEntity setStartSlide(@PathVariable("novelId") String novelId, @PathVariable("start-slide-id") String startSlideId){
        novelService.setStartSlide(novelId,startSlideId);
        return new ResponseEntity(HttpStatus.OK);
    }
    @GetMapping("/novels/{novelId}/start")
    public ResponseEntity getStartSlide(@PathVariable("novelId") String novelId){
        String startSlideId = novelService.getStartSlideId(novelId);
        Slide slide = slideService.getSlideById(startSlideId);
        List<Transition> transitionDtos = slideService.getTransitionsOfSlide(startSlideId);
        StartNovelDto startNovelDto = new StartNovelDto();
        startNovelDto.setNovelId(novelId);
        startNovelDto.setId(startSlideId);
        startNovelDto.setDescription(slide.getDescription());
        startNovelDto.setTransitions(transitionDtos);
        return new ResponseEntity(slide, HttpStatus.OK);
    }
    @GetMapping("/slides/next/{idTransition}")
    public ResponseEntity getNextSlide(@PathVariable("idTransition") String transitionId){
        Slide slide = slideService.getNextSlide(transitionId);
        return new ResponseEntity(slide,HttpStatus.OK);
    }
    @GetMapping(value = "/slides/{slideId}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getPhoto(@PathVariable("slideId") String slideId){
        //return new ResponseEntity(slideService.getPhoto(slideId),HttpStatus.OK);
        byte[] bytes = slideService.getPhoto(slideId);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentLength(bytes.length)
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
