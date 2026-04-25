package com.ishan.BrainThread.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.ishan.BrainThread.models.Answer;

import reactor.core.publisher.Flux;
import org.springframework.data.domain.Pageable;

@Repository
public interface AnswerRepository extends ReactiveMongoRepository<Answer, String> {
    Flux<Answer> findByQuestionIdOrderByCreatedAtDesc(String questionId, Pageable pageable);
}
