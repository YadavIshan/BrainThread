package com.ishan.BrainThread.controllers;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.dto.AnswerRequestDTO;
import com.ishan.BrainThread.dto.AnswerResponseDTO;
import com.ishan.BrainThread.service.IAnswerService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final IAnswerService answerService;

    @PostMapping
    public Mono<AnswerResponseDTO> createAnswer(@Valid @RequestBody AnswerRequestDTO answerDTO) {
        return answerService.createAnswer(answerDTO);
    }

    @GetMapping("/{id}")
    public Mono<AnswerResponseDTO> getAnswerById(@PathVariable String id) {
        return answerService.getAnswerById(id);
    }

    @GetMapping("/question/{questionId}")
    public Flux<AnswerResponseDTO> getAnswersForQuestion(
            @PathVariable String questionId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return answerService.getAnswersForQuestion(questionId, cursor, limit);
    }
}
