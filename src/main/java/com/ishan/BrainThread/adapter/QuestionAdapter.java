package com.ishan.BrainThread.adapter;

import com.ishan.BrainThread.dto.QuestionRequestDTO;
import com.ishan.BrainThread.models.Question;

public class QuestionAdapter {
    public static QuestionRequestDTO toQuestionResponseDTO(Question question) {
        return QuestionRequestDTO.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUserId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
