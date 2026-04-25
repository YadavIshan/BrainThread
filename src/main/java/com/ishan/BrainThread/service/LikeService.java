package com.ishan.BrainThread.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Like;
import com.ishan.BrainThread.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import com.ishan.BrainThread.adapter.LikeAdapter;
import com.ishan.BrainThread.dto.LikeReqeustDTO;
import com.ishan.BrainThread.dto.LikeResponseDTO;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService {
    private final LikeRepository likeRepository;

    @Override
    public Mono<LikeResponseDTO> createLike(LikeReqeustDTO likeDTO) {
        Like newLike = Like.builder()
                .targetType(likeDTO.getTargetType())
                .targetId(likeDTO.getTargetId())
                .userId(likeDTO.getUserId())
                .isLike(likeDTO.isLike())
                .createdAt(new Date())
                .build();
        return likeRepository.save(newLike)
                .map(LikeAdapter::toLikeResponseDTO);
    }

    @Override
    public Mono<LikeResponseDTO> deleteLike(String id) {
        return likeRepository.findById(id)
                .flatMap(like -> likeRepository.delete(like).thenReturn(like))
                .map(LikeAdapter::toLikeResponseDTO);
    }

    @Override
    public Mono<Long> countLikesByTargetIdAndTargetType(String targetId, String targetType) {
        return likeRepository.countByTargetIdAndTargetTypeAndIsLikeTrue(targetId, targetType);
    }

    @Override
    public Mono<Long> countDislikesByTargetIdAndTargetType(String targetId, String targetType) {
        return likeRepository.countByTargetIdAndTargetTypeAndIsLikeFalse(targetId, targetType);
    }

    @Override
    public Mono<LikeResponseDTO> toggleLike(String targetId, String targetType, String userId) {
        return likeRepository.findByTargetIdAndTargetTypeAndUserId(targetId, targetType, userId)
                .flatMap(existingLike -> {
                    existingLike.setLike(!existingLike.isLike());
                    return likeRepository.save(existingLike);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Like newLike = Like.builder()
                            .targetType(targetType)
                            .targetId(targetId)
                            .userId(userId)
                            .isLike(true)
                            .createdAt(new Date())
                            .build();
                    return likeRepository.save(newLike);
                }))
                .map(LikeAdapter::toLikeResponseDTO);
    }
}
