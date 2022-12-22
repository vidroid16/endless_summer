package com.example.summer.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EdgeDto {
    String id;
    String idChild;
    ArrayList<TransitionDto> transitions;
}
