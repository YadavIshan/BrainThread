package com.ishan.BrainThread.service;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import com.ishan.BrainThread.dto.AnswerRequestDTO;
import com.ishan.BrainThread.dto.AnswerResponseDTO;

public interface IAnswerService {
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerDTO);

    public Flux<AnswerResponseDTO> getAnswersForQuestion(String questionId, String cursor, int size);

    public Mono<AnswerResponseDTO> getAnswerById(String id);
}
