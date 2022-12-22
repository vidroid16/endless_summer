package com.example.summer.service;

import com.amazonaws.services.accessanalyzer.model.AclGrantee;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3control.model.BucketCannedACL;
import com.example.summer.DAO.NovelDao;
import com.example.summer.dto.NovelDto;
import com.example.summer.model.Novel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NovelService {

    NovelDao novelDao;

    @Autowired
    private AmazonS3 s3;

    @Autowired
    private SlideService slideService;

    @Autowired
    public NovelService(){
        this.novelDao = new NovelDao();
    }

    public String createNovel(Novel novel){
        novelDao.save(novel);
        s3.createBucket(novel.getId());
        s3.getBucketAcl(novel.getId()).grantPermission(GroupGrantee.AllUsers, Permission.Read);
        return novel.getId();
    }
    public ArrayList<NovelDto> getAllNovels(){
        List<Novel> novels = novelDao.getAllNovels();
        ArrayList<NovelDto> novelDtos = new ArrayList<>();
        novels.forEach(n->{
            novelDtos.add(new NovelDto(n.getId(),n.getName(),n.getStartSlideId()));
        });
        return novelDtos;
    }

    public void setStartSlide(String novelId, String startSlideId){
        novelDao.setStartSlide(novelId,startSlideId);
    }
    public String getStartSlideId(String novelId){
       Novel novel = novelDao.getById(novelId);
       return novel.getStartSlideId();
    }

    public String getInfo(){
        return "";
    }
}
