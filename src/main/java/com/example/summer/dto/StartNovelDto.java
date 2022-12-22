package com.example.summer.dto;

import com.example.summer.model.Transition;
import lombok.Data;

import java.util.List;

@Data
public class StartNovelDto {
    String id;
    String novelId;
    String description;
    List<Transition> transitions;
}
