package com.example.summer.service;

import com.amazonaws.services.accessanalyzer.model.AclGrantee;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3control.model.BucketCannedACL;
import com.example.summer.DAO.NovelDao;
import com.example.summer.model.Novel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void createNovel(Novel novel){
        novelDao.save(novel);
        s3.createBucket(novel.getName());
        s3.getBucketAcl(novel.getName()).grantPermission(GroupGrantee.AllUsers, Permission.Read);
    }
}
