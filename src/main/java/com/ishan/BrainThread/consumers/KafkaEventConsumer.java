package com.ishan.BrainThread.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.ishan.BrainThread.events.ViewCountEvent;
import com.ishan.BrainThread.repositories.QuestionRepository;
import com.ishan.BrainThread.config.KafkaConfig;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {
    
    private final QuestionRepository questionRepository;

    @KafkaListener(topics = KafkaConfig.TOPIC_NAME, groupId = "view-count-consumer" , containerFactory = "kafkaListenerContainerFactory")
    private void handleViewCountEvent(ViewCountEvent event) {
        //Note : If what you're returning inside the lambda is a Mono or Flux, always use flatMap, not map.
        questionRepository.findById(event.getTargetId())
            .flatMap(question -> {
                question.setViewCount(question.getViewCount() == null ? 1 : question.getViewCount() + 1);
                return questionRepository.save(question);
            })
            .subscribe(updatedQuestion -> {
                System.out.println("Question updated successfully: " + updatedQuestion);
            }, error -> {
                System.out.println("Error updating question: " + error.getMessage());
            }); // because this is lazy loaded
    }
}
