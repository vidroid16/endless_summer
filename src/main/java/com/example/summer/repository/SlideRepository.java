package com.example.summer.repository;

import com.example.summer.model.Slide;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableScan
public interface SlideRepository extends CrudRepository<Slide, Long> {
}
