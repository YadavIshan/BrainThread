package com.ishan.BrainThread.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(indexName = "questions")
public class QuestionElasticDocument {
    @Id
    private String id;
    private String title;
    private String content;

}
