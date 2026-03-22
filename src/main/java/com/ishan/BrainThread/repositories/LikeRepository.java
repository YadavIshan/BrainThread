package com.ishan.BrainThread.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.ishan.BrainThread.models.Like;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<Like, String> {
    
}
