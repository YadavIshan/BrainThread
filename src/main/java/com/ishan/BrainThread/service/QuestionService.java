package com.ishan.BrainThread.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Question;
import com.ishan.BrainThread.repositories.QuestionRepository;
import com.ishan.BrainThread.repositories.QuestionDocumentRepository;
import lombok.RequiredArgsConstructor;

import com.ishan.BrainThread.adapter.QuestionAdapter;
import com.ishan.BrainThread.dto.QuestionResponseDTO;
import com.ishan.BrainThread.events.ViewCountEvent;
import com.ishan.BrainThread.dto.QuestionRequestDTO;
import com.ishan.BrainThread.utils.CursorUtils;
import java.util.Date;
import java.time.LocalDateTime;
import com.ishan.BrainThread.producer.KafkaEventProducer;


@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final IQuestionIndexService questionIndexService;
    private final QuestionDocumentRepository questionDocumentRepository;

    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionDTO) {
        Question newQuestion = Question.builder()
                .title(questionDTO.getTitle())
                .content(questionDTO.getContent())
                .userId(questionDTO.getUserId())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        return questionRepository.save(newQuestion)
                .map(savedQuestion -> {
                    questionIndexService.createQuestionIndex(savedQuestion);
                    return QuestionAdapter.toQuestionResponseDTO(savedQuestion);
                })
                .doOnSuccess(q -> {
                    System.out.println("Question created successfully: " + q);
                })
                .doOnError(e -> {
                    System.out.println("Error creating question: " + e.getMessage());
                });
    }

    @Override
    public Flux<QuestionResponseDTO> getQuestionsByAuthorId(String authorId) {
        return questionRepository.findByUserId(authorId)
                .map(QuestionAdapter::toQuestionResponseDTO);
    }

    @Override
    public Flux<QuestionResponseDTO> searchQuestions(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.findByTitleOrContentContainingIgnoreCase(query, pageable)
                .map(QuestionAdapter::toQuestionResponseDTO)
                .doOnError(e -> {
                    System.out.println("Error searching questions: " + e.getMessage());
                })
                .doOnComplete(() -> {
                    System.out.println("Questions searched successfully");
                });
    }

    @Override
    public Flux<QuestionResponseDTO> getQuestionByTag(String tag, int page, int size) {
        return questionRepository.findByTag(tag)
                .map(QuestionAdapter::toQuestionResponseDTO);
    }

    @Override
    public Flux<QuestionResponseDTO> getAllQuestions(String cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if(!CursorUtils.isValidCursor(cursor)) {
            return questionRepository.findTop10ByOrderByCreatedAtDesc()
                .take(limit)
                .map(QuestionAdapter::toQuestionResponseDTO)
                .doOnComplete(() -> {
                    System.out.println("Questions retrieved successfully");
                })
                .doOnError(e -> {
                    System.out.println("Error retrieving questions: " + e.getMessage());
                });
        }else{
            LocalDateTime cursorDate = CursorUtils.encodeCursor(cursor);
            
            //Filtering out records older than the cursor
            return questionRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(cursorDate, pageable)
                .map(QuestionAdapter::toQuestionResponseDTO)
                .doOnComplete(() -> {
                    System.out.println("Questions retrieved successfully");
                })
                .doOnError(e -> {
                    System.out.println("Error retrieving questions: " + e.getMessage());
                });
        }
    }

    @Override
    public Mono<QuestionResponseDTO> getQuestionById(String id) {
        return questionRepository.findById(id)
                .map(QuestionAdapter::toQuestionResponseDTO)
                .doOnSuccess(q -> {
                    System.out.println("Question retrieved successfully: " + q);
                    ViewCountEvent event = new ViewCountEvent(id , "question" , LocalDateTime.now());
                    kafkaEventProducer.publishViewCountEvent(event);
                })
                .doOnError(e -> {
                    System.out.println("Error retrieving question: " + e.getMessage());
                });
    }

    @Override
    public Flux<QuestionResponseDTO> searchQuestionsElastic(String query) {
        return Flux.fromIterable(questionDocumentRepository.findByTitleContainingOrContentContaining(query, query))
                .map(QuestionAdapter::toQuestionResponseDTO)
                .doOnError(e -> {
                    System.out.println("Error searching questions: " + e.getMessage());
                })
                .doOnComplete(() -> {
                    System.out.println("Questions searched successfully");
                });
    }
}
