package com.ishan.BrainThread.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ishan.BrainThread.dto.QuestionRequestDTO;
import com.ishan.BrainThread.servicce.IQuestionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @GetMapping("/author/{authorId}")
    public Flux<QuestionRequestDTO> getQuestionsByAuthorId(@PathVariable String authorId) {
        return questionService.getQuestionsByAuthorId(authorId);
    }

    @PostMapping
    public Mono<QuestionRequestDTO> createQuestion(@RequestBody QuestionRequestDTO question) {
        return questionService.createQuestion(question);
    }
}
