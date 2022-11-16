package com.example.summer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class SlideWithChilds {
    String slideId;
    String description;
    String photo;
    ArrayList<String> childsId;
}
