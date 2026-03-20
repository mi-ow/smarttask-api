package com.mi_ow.smarttask.service;

import com.mi_ow.smarttask.dto.request.TaskRequest;
import com.mi_ow.smarttask.dto.response.TaskResponse;
import com.mi_ow.smarttask.entity.Task;
import com.mi_ow.smarttask.entity.TaskStatus;
import com.mi_ow.smarttask.entity.User;
import com.mi_ow.smarttask.exception.ResourceNotFoundException;
import com.mi_ow.smarttask.repository.TaskRepository;
import com.mi_ow.smarttask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;
    private Task mockTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("mi_ow");
        mockUser.setEmail("mi_ow@example.com");
        mockUser.setPassword("encodedPassword");

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Task");
        mockTask.setDescription("Test Description");
        mockTask.setStatus(TaskStatus.TODO);
        mockTask.setUser(mockUser);

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setStatus(TaskStatus.TODO);

        // Mock SecurityContextHolder for every test
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("mi_ow");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("mi_ow")).thenReturn(Optional.of(mockUser));
    }

    // ── Create Task Tests ────────────────────────────────────────

    @Test
    void createTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(TaskStatus.TODO, response.getStatus());
        assertEquals("mi_ow", response.getUsername());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ── Get All Tasks Tests ──────────────────────────────────────

    @Test
    void getAllTasks_Success() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(mockTask));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Task", responses.get(0).getTitle());
    }

    @Test
    void getAllTasks_ReturnsEmptyList_WhenNoTasks() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of());

        List<TaskResponse> responses = taskService.getAllTasks();

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    // ── Get Task By Id Tests ─────────────────────────────────────

    @Test
    void getTaskById_Success() {
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockTask));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
    }

    @Test
    void getTaskById_ThrowsException_WhenTaskNotFound() {
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(999L));

        assertEquals("Task not found with id: 999", exception.getMessage());
    }

    // ── Update Task Tests ────────────────────────────────────────

    @Test
    void updateTask_Success() {
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setUser(mockUser);

        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTask(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Task", response.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
    }

    @Test
    void updateTask_ThrowsException_WhenTaskNotFound() {
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(999L, taskRequest));

        assertEquals("Task not found with id: 999", exception.getMessage());
    }

    // ── Delete Task Tests ────────────────────────────────────────

    @Test
    void deleteTask_Success() {
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockTask));

        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskRepository, times(1)).delete(mockTask);
    }

    @Test
    void deleteTask_ThrowsException_WhenTaskNotFound() {
        when(taskRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.deleteTask(999L));

        assertEquals("Task not found with id: 999", exception.getMessage());
        verify(taskRepository, never()).delete(any(Task.class));
    }
}