package com.ishan.BrainThread.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Answer;
import com.ishan.BrainThread.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import com.ishan.BrainThread.adapter.AnswerAdapter;
import com.ishan.BrainThread.dto.AnswerRequestDTO;
import com.ishan.BrainThread.dto.AnswerResponseDTO;
import java.util.Date;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class AnswerService implements IAnswerService {
    private final AnswerRepository answerRepository;

    @Override
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerDTO) {
        Answer newAnswer = Answer.builder()
                .content(answerDTO.getContent())
                .userId(answerDTO.getUserId())
                .questionId(answerDTO.getQuestionId())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        return answerRepository.save(newAnswer)
                .map(AnswerAdapter::toAnswerResponseDTO);
    }

    @Override
    public Flux<AnswerResponseDTO> getAnswersForQuestion(String questionId, String cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);
        return answerRepository.findByQuestionIdOrderByCreatedAtDesc(questionId, pageable)
                .map(AnswerAdapter::toAnswerResponseDTO);
    }

    @Override
    public Mono<AnswerResponseDTO> getAnswerById(String id) {
        return answerRepository.findById(id)
                .map(AnswerAdapter::toAnswerResponseDTO);
    }
}
