package com.ishan.BrainThread.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Question;

@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {
    Flux<Question> findByUserId(String userId);

    Mono<Long> countByUserId(String userId);
}
