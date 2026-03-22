package com.ishan.BrainThread.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ishan.BrainThread.models.Question;
import com.ishan.BrainThread.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;

import com.ishan.BrainThread.adapter.QuestionAdapter;
import com.ishan.BrainThread.dto.QuestionResponseDTO;
import com.ishan.BrainThread.dto.QuestionRequestDTO;
import com.ishan.BrainThread.utils.CursorUtils;
import java.util.Date;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

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
                .map(QuestionAdapter::toQuestionResponseDTO)
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
}
