package com.example.summer.mapper;

import com.example.summer.dto.EdgeDto;
import com.example.summer.dto.TransitionDto;
import com.example.summer.model.Edge;
import com.example.summer.model.Transition;

public class EdgeMapper {
    public static EdgeDto edgeToEdgeDto(Edge edge){
        EdgeDto dto = new EdgeDto();
        dto.setId(edge.getId());
        dto.setIdChild(edge.getIdChild());
        return dto;
    }
}
