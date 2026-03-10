package com.ishan.BrainThread.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ishan.BrainThread.dto.QuestionRequestDTO;
import com.ishan.BrainThread.dto.QuestionResponseDTO;
import com.ishan.BrainThread.service.IQuestionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @GetMapping("/author/{authorId}")
    public Flux<QuestionResponseDTO> getQuestionsByAuthorId(@PathVariable String authorId) {
        return questionService.getQuestionsByAuthorId(authorId);
    }

    @PostMapping
    public Mono<QuestionResponseDTO> createQuestion(@RequestBody QuestionRequestDTO question) {
        return questionService.createQuestion(question);
    }

    @GetMapping("/search")
    public Flux<QuestionResponseDTO> searchQuestions(@RequestParam String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return questionService.searchQuestions(query, page, size);
    }

    @GetMapping("/tag/{tag}")
    public Flux<QuestionResponseDTO> getQuestionByTag(@PathVariable String tag,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return questionService.getQuestionByTag(tag, page, size);
    }

    @GetMapping()
    public Flux<QuestionResponseDTO> getAllQuestions(
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int limit
    ) {
        return questionService.getAllQuestions(cursor, limit);
    }
}
