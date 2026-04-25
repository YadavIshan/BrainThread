package com.ishan.BrainThread.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDTO {
    private String id;
    private String targetType;
    private String targetId;
    private String userId;
    private boolean isLike;
    private LocalDateTime createdAt;
}
