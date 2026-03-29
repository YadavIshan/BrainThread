package com.ishan.BrainThread.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewCountEvent {
    private String targetId;
    private String targetType;
    private LocalDateTime timestamp;
}
