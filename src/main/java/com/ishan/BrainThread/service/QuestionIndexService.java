package com.ishan.BrainThread.service;

import org.springframework.stereotype.Service;

import com.ishan.BrainThread.models.QuestionElasticDocument;
import com.ishan.BrainThread.models.Question;
import com.ishan.BrainThread.repositories.QuestionDocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionIndexService implements IQuestionIndexService{

    private final QuestionDocumentRepository questionDocumentRepository;

    @Override
    public void createQuestionIndex(Question question) {
            QuestionElasticDocument questionDocument = QuestionElasticDocument.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .build();
        questionDocumentRepository.save(questionDocument);
    }
    
}
