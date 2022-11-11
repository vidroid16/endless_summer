package com.example.summer.service;

import com.example.summer.model.Slide;
import com.example.summer.repository.SlideRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlideService {

    @Autowired
    SlideRepository slideRepository;

    public void saveSlide(Slide slide){
        slideRepository.save(slide);
    }
}
