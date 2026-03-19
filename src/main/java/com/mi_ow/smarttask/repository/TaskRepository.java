package com.mi_ow.smarttask.repository;

import com.mi_ow.smarttask.entity.Task;
import com.mi_ow.smarttask.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
}