package com.example.summer.dto;

import com.amazonaws.services.dynamodbv2.xspec.S;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SlideToAddDto {
    private MultipartFile image;
    private String description;
    private String novelId;

}
