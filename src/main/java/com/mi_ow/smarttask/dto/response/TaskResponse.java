package com.mi_ow.smarttask.dto.response;

import com.mi_ow.smarttask.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
