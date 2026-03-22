package com.ishan.BrainThread.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.ishan.BrainThread.models.Answer;

@Repository
public interface AnswerRepository extends ReactiveMongoRepository<Answer, String> {
    
}
