package com.ishan.BrainThread.models;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "likes")
public class Like {
    @Id
    private String id;

    private String targetType ; //Question , Answers , Stories , etc.
    
    private String targetId;

    private String userId;

    private boolean isLike;

    @CreatedDate
    private Date createdAt;
}
