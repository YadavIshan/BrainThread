package com.ishan.BrainThread.service;

import reactor.core.publisher.Mono;
import com.ishan.BrainThread.dto.LikeReqeustDTO;
import com.ishan.BrainThread.dto.LikeResponseDTO;

public interface ILikeService {
    public Mono<LikeResponseDTO> createLike(LikeReqeustDTO likeDTO);

    public Mono<LikeResponseDTO> deleteLike(String id);

    public Mono<LikeResponseDTO> countLikesByTargetIdAndTargetType(String targetId, String targetType);

    public Mono<LikeResponseDTO> countDislikesByTargetIdAndTargetType(String targetId, String targetType);

    public Mono<LikeResponseDTO> toggleLike(String targetId, String targetType);
}
