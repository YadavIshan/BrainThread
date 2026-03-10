package com.ishan.BrainThread.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Question;

@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question, String> {

    Flux<Question> findByUserId(String userId);

    Mono<Long> countByUserId(String userId);

    @Query("{ $or: [ { title: { $regex: ?0, $options: 'i' } }, { content: { $regex: ?0, $options: 'i' } } ] }")
    Flux<Question> findByTitleOrContentContainingIgnoreCase(String searchTerm);

    @Query("{ $or: [ { title: { $regex: ?0, $options: 'i' } }, { content: { $regex: ?0, $options: 'i' } } ] }")
    Flux<Question> findByTitleOrContentContainingIgnoreCase(String searchTerm, Pageable pageable);

    @Query("{ tags: ?0 }")
    Flux<Question> findByTag(String tag);

    
    Flux<Question> findByCreatedAtGreaterThanOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);
}
