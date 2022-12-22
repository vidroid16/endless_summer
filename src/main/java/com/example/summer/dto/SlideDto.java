package com.example.summer.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SlideDto {
    String slideId;
    String description;
    String photo;
    ArrayList<EdgeDto> edges;

    public SlideDto(String slideId, String description, String photo, ArrayList<EdgeDto> edges) {
        this.slideId = slideId;
        this.description = description;
        this.photo = photo;
        this.edges = edges;
    }

    public SlideDto() {
    }
}
