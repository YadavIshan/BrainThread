package com.ishan.BrainThread.servicce;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.dto.QuestionRequestDTO;

public interface IQuestionService {

    public Mono<QuestionRequestDTO> createQuestion(QuestionRequestDTO question);

    public Flux<QuestionRequestDTO> getQuestionsByAuthorId(String authorId);
}
