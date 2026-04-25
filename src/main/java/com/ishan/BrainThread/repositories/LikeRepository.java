package com.ishan.BrainThread.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.ishan.BrainThread.models.Like;

import reactor.core.publisher.Mono;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<Like, String> {
    Mono<Long> countByTargetIdAndTargetTypeAndIsLikeTrue(String targetId, String targetType);
    Mono<Long> countByTargetIdAndTargetTypeAndIsLikeFalse(String targetId, String targetType);
    Mono<Like> findByTargetIdAndTargetTypeAndUserId(String targetId, String targetType, String userId);
}
