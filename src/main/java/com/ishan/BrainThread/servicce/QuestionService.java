package com.ishan.BrainThread.servicce;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Question;
import com.ishan.BrainThread.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;

import com.ishan.BrainThread.adapter.QuestionAdapter;
import com.ishan.BrainThread.dto.QuestionRequestDTO;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Mono<QuestionRequestDTO> createQuestion(QuestionRequestDTO questionDTO) {
        Question newQuestion = Question.builder()
                .title(questionDTO.getTitle())
                .content(questionDTO.getContent())
                .userId(questionDTO.getUserId())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        return questionRepository.save(newQuestion).map(QuestionAdapter::toQuestionResponseDTO).doOnSuccess(q -> {
            System.out.println("Question created successfully: " + q);
        }).doOnError(e -> {
            System.out.println("Error creating question: " + e.getMessage());
        });
    }

    @Override
    public Flux<QuestionRequestDTO> getQuestionsByAuthorId(String authorId) {
        return questionRepository.findByUserId(authorId).map(QuestionAdapter::toQuestionResponseDTO);
    }
}
