package com.ishan.BrainThread.producer;

import org.springframework.stereotype.Service;

import com.ishan.BrainThread.events.ViewCountEvent;
import com.ishan.BrainThread.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    
    private final KafkaTemplate<String , Object> kafkaTemplate;

    public void publishViewCountEvent(ViewCountEvent event) {
        kafkaTemplate.send(KafkaConfig.TOPIC_NAME , event.getTargetId() , event)
        .whenComplete((result , ex) -> {
            if(ex != null) {
                System.out.println("Error sending message: " + ex.getMessage());
            }else{
                System.out.println("Message sent successfully: " + result);
            }
        });
    }
}
