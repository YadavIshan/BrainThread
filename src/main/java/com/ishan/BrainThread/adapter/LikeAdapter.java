package com.ishan.BrainThread.adapter;

import com.ishan.BrainThread.dto.LikeResponseDTO;
import com.ishan.BrainThread.models.Like;

public class LikeAdapter {
    public static LikeResponseDTO toLikeResponseDTO(Like like) {
        return LikeResponseDTO.builder()
                .id(like.getId())
                .targetType(like.getTargetType())
                .targetId(like.getTargetId())
                .userId(like.getUserId())
                .isLike(like.isLike())
                .build();
    }
}
