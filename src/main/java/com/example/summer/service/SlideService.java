package com.example.summer.service;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.summer.DAO.EdgeDao;
import com.example.summer.DAO.SlideDao;
import com.example.summer.dto.SlideToAddDto;
import com.example.summer.dto.TransitionDto;
import com.example.summer.model.Slide;
import com.example.summer.model.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SlideService {
    SlideDao slideDao;
    EdgeDao edgeDao;

    @Autowired
    private AmazonS3 s3;

    @Autowired
    public SlideService(){
        this.slideDao = new SlideDao();
        this.edgeDao = new EdgeDao();
    }

    public InputStream createSlide(SlideToAddDto slideToAddDto) {
        Slide slide = new Slide(slideToAddDto.getNovelId(), slideToAddDto.getDescription());
        InputStream photoUrl = null;
        try {
            InputStream inputStream = slideToAddDto.getImage().getInputStream();
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("image/jpeg");
            s3.putObject(new PutObjectRequest("spring", slide.getId(),inputStream,meta));

            InputStream photIs = s3.getObject(new GetObjectRequest("spring",slide.getId())).getObjectContent();
            slideDao.save(slide);
            return photIs;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setParent(String slideId, String parentId){
        edgeDao.save(slideId,parentId);
    }
    public void setTransition(TransitionDto dto){
        Transition transition = new Transition(dto.getEdgeId(), dto.getDescription());
        slideDao.saveTransition(transition);
    }
    public List<Transition> getTransitionsOfSlide(String slideId){
        return slideDao.getTransitions(slideId);
    }
    public Slide getNextSlide(String transitionId){
        String id = slideDao.getNextSlideId(transitionId);
        Slide slide = slideDao.getById(id);
        return slide;
    }
}
