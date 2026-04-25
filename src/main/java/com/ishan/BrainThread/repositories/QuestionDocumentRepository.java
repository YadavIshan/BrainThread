package com.ishan.BrainThread.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ishan.BrainThread.models.QuestionElasticDocument;
import java.util.List;

public interface QuestionDocumentRepository extends ElasticsearchRepository<QuestionElasticDocument, String> {
    List<QuestionElasticDocument> findByTitleContainingOrContentContaining(String title , String content);
}
