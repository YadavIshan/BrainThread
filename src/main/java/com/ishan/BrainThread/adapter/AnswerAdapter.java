package com.ishan.BrainThread.adapter;

import com.ishan.BrainThread.dto.AnswerResponseDTO;
import com.ishan.BrainThread.models.Answer;

public class AnswerAdapter {
    public static AnswerResponseDTO toAnswerResponseDTO(Answer answer) {
        return AnswerResponseDTO.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .questionId(answer.getQuestionId())
                .userId(answer.getUserId())
                .build();
    }
}
