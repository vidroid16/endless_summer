package com.example.summer.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.example.summer.DAO.EdgeDao;
import com.example.summer.DAO.SlideDao;
import com.example.summer.dto.*;
import com.example.summer.mapper.EdgeMapper;
import com.example.summer.mapper.TransitionMapper;
import com.example.summer.model.Edge;
import com.example.summer.model.Slide;
import com.example.summer.model.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
            var req = new PutObjectRequest(slideToAddDto.getNovelId(), slide.getId(),inputStream,meta);
            req.setCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(req);

            InputStream photIs = s3.getObject(new GetObjectRequest(slideToAddDto.getNovelId(),slide.getId())).getObjectContent();
            slideDao.save(slide);
            return photIs;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setParent(String parentId, String childId){
        edgeDao.save(parentId,childId);
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
    public Slide getSlideById(String slideId){
        Slide slide = slideDao.getById(slideId);
        return slide;
    }
    public byte[] getPhoto(String slideId) {
        Slide slide = getSlideById(slideId);
        S3Object object = s3.getObject(new GetObjectRequest(slide.getNovelId(), slide.getId()));
        String url = object.getObjectContent().getHttpRequest().getURI().toString();
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

        return imageBytes;
    }
    public AllSlidesDto getAllSlidesByNovelId(String novelId){
        List<Slide> slides = slideDao.getAllSlideByNovelId(novelId);
        ArrayList<SlideDto> slideDtos = new ArrayList<>();
        slides.forEach(p->{
            ArrayList<EdgeDto> edgeDtos = new ArrayList<>();
            List<Edge> edges = edgeDao.getAllEdgesBySlideId(p.getId());
            edges.forEach(e->{
                edgeDtos.add(EdgeMapper.edgeToEdgeDto(e));
            });
            edgeDtos.forEach(t->{
                ArrayList<TransitionDto> transitionDtos = new ArrayList<>();
                slideDao.getTransitionsByEdgeId(t.getId()).forEach(k ->{
                    transitionDtos.add(TransitionMapper.transitionToTransitionDto(k));
                });
                t.setTransitions(transitionDtos);
            });
            slideDtos.add(new SlideDto(p.getId(), p.getDescription(),
                    p.getPhoto(), edgeDtos));
        });
        AllSlidesDto allSlidesDto = new AllSlidesDto();
        allSlidesDto.setSlides(slideDtos);
        return allSlidesDto;
    }
}
