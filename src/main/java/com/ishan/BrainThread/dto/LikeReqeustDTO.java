package com.ishan.BrainThread.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeReqeustDTO {
    @NotBlank(message = "Target type is required")
    private String targetType;
    @NotBlank(message = "Target ID is required")
    private String targetId;
    @NotNull(message = "Like status is required")
    private boolean isLike;
    @NotBlank(message = "User ID is required")
    private String userId;
}
