package com.ishan.BrainThread.adapter;

import com.ishan.BrainThread.dto.QuestionResponseDTO;
import com.ishan.BrainThread.models.Question;
import com.ishan.BrainThread.models.QuestionElasticDocument;

public class QuestionAdapter {

    public static QuestionResponseDTO toQuestionResponseDTO(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .userId(question.getUserId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public static QuestionResponseDTO toQuestionResponseDTO(QuestionElasticDocument question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .build();
    }
}
