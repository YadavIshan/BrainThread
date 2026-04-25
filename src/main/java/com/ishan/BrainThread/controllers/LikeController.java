package com.ishan.BrainThread.controllers;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.dto.LikeReqeustDTO;
import com.ishan.BrainThread.dto.LikeResponseDTO;
import com.ishan.BrainThread.service.ILikeService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final ILikeService likeService;

    @PostMapping
    public Mono<LikeResponseDTO> createLike(@Valid @RequestBody LikeReqeustDTO likeDTO) {
        return likeService.createLike(likeDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<LikeResponseDTO> deleteLike(@PathVariable String id) {
        return likeService.deleteLike(id);
    }

    @GetMapping("/count/likes")
    public Mono<Long> countLikes(
            @RequestParam String targetId,
            @RequestParam String targetType) {
        return likeService.countLikesByTargetIdAndTargetType(targetId, targetType);
    }

    @GetMapping("/count/dislikes")
    public Mono<Long> countDislikes(
            @RequestParam String targetId,
            @RequestParam String targetType) {
        return likeService.countDislikesByTargetIdAndTargetType(targetId, targetType);
    }

    @PostMapping("/toggle")
    public Mono<LikeResponseDTO> toggleLike(
            @RequestParam String targetId,
            @RequestParam String targetType,
            @RequestParam String userId) {
        return likeService.toggleLike(targetId, targetType, userId);
    }
}
