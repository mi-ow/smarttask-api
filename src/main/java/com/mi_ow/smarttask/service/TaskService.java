package com.mi_ow.smarttask.service;

import com.mi_ow.smarttask.dto.request.TaskRequest;
import com.mi_ow.smarttask.dto.response.TaskResponse;
import com.mi_ow.smarttask.entity.Task;
import com.mi_ow.smarttask.entity.TaskStatus;
import com.mi_ow.smarttask.entity.User;
import com.mi_ow.smarttask.exception.ResourceNotFoundException;
import com.mi_ow.smarttask.repository.TaskRepository;
import com.mi_ow.smarttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // ── Helper: get logged in user ──────────────────────────────
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ── Helper: map Task entity to TaskResponse DTO ─────────────
    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setUsername(task.getUser().getUsername());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    // ── Create ──────────────────────────────────────────────────
    public TaskResponse createTask(TaskRequest request) {
        User user = getLoggedInUser();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setUser(user);

        return mapToResponse(taskRepository.save(task));
    }

    // ── Get All Tasks for logged in user ────────────────────────
    public List<TaskResponse> getAllTasks() {
        User user = getLoggedInUser();
        return taskRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Get Single Task ─────────────────────────────────────────
    public TaskResponse getTaskById(Long id) {
        User user = getLoggedInUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));
        return mapToResponse(task);
    }

    // ── Update Task ─────────────────────────────────────────────
    public TaskResponse updateTask(Long id, TaskRequest request) {
        User user = getLoggedInUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());

        return mapToResponse(taskRepository.save(task));
    }

    // ── Delete Task ─────────────────────────────────────────────
    public void deleteTask(Long id) {
        User user = getLoggedInUser();
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        User user = getLoggedInUser();
        return taskRepository.findByUserIdAndStatus(user.getId(), status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}