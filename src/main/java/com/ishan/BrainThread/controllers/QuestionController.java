package com.ishan.BrainThread.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ishan.BrainThread.models.Question;
import com.ishan.BrainThread.repositories.QuestionRepository;

import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/author/{authorId}")
    public Flux<Question> getQuestionsByAuthorId(@PathVariable String authorId) {
        return questionRepository.findByUserId(authorId);
    }
}
