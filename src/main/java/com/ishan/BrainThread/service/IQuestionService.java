package com.ishan.BrainThread.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.dto.QuestionRequestDTO;
import com.ishan.BrainThread.dto.QuestionResponseDTO;

public interface IQuestionService {

    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO question);

    public Flux<QuestionResponseDTO> getQuestionsByAuthorId(String authorId);

    public Flux<QuestionResponseDTO> searchQuestions(String query, int page, int size);

    public Flux<QuestionResponseDTO> getQuestionByTag(String tag, int page, int size);

    public Flux<QuestionResponseDTO> getAllQuestions(String cursor, int limit);

    public Mono<QuestionResponseDTO> getQuestionById(String id);

    public Flux<QuestionResponseDTO> searchQuestionsElastic(String query);
}
