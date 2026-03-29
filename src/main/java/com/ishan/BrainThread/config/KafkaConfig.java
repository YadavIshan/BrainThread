package com.ishan.BrainThread.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:view-count-consumer}")
    private String groupId;

    public static final String TOPIC_NAME = "view-count-topic";

    /***
     * This function is going to create a a producer factory 
     * @return
     */
    @Bean
    public ProducerFactory<String , Object> producerFactory() {
        Map<String , Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * This function is going to create a a consumer factory 
     * @return
     */
    @Bean
    public ConsumerFactory<String , Object> consumerFactory() {
        Map<String , Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * This function is going to create a a listener container factory 
     * @return
     */
    @Bean
    public KafkaTemplate<String , Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * This function is going to create a a listener container factory 
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String , Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String , Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        return factory;
    }
}
