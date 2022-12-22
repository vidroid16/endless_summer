package com.example.summer.mapper;

import com.example.summer.dto.TransitionDto;
import com.example.summer.model.Transition;

public class TransitionMapper {
    public static TransitionDto transitionToTransitionDto(Transition transition){
        TransitionDto dto = new TransitionDto();
        dto.setEdgeId(transition.getEdgeId());
        dto.setDescription(transition.getDescription());
        return dto;
    }
}
