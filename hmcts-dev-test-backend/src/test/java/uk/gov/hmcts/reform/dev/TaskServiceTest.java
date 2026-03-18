package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.exception.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.mapper.TaskMapper;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    TaskRepository taskRepository;

    TaskService taskService;
    TaskMapper mapper = new TaskMapper();

    @BeforeEach
    void setup() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTaskTest() {
        LocalDateTime now =  LocalDateTime.now().plusMinutes(20);
        Task task = new Task(1L, "New Task", "Do something", Status.TODO, now.plusMinutes(20), now);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskRequest taskRequest = new TaskRequest("New Task", "Do something", "TODO", now.plusMinutes(20));
        Task taskMapped = mapper.toEntity(taskRequest);

        var result = taskService.create(taskMapped);

        assertThat(result).isEqualTo(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTaskBadDateThrowsException() {
        LocalDateTime now =  LocalDateTime.now();

        Task task = new Task(1L, "New Task", "Do something", Status.TODO, now.minusDays(1), now);

        assertThatException().isThrownBy(() -> taskService.create(task))
            .isInstanceOf(IllegalArgumentException.class)
            .withMessage("The due date should be in the future");
    }

    @Test
    void getTaskTest() {
        LocalDateTime now =  LocalDateTime.now();
        Task task = new Task(1L,"New Task", "Do something", Status.TODO, now.plusHours(10), now);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        var result = taskService.getById(1L);

        assertThat(result).isEqualTo(task);
        verify(taskRepository).findById(1L);
    }

    @Test
    void taskNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatException().isThrownBy(() -> taskService.getById(1L))
            .isInstanceOf(TaskNotFoundException.class)
            .withMessage("[%s] not found", 1L);
    }

    @Test
    void updatesTaskTest() {
        LocalDateTime now =  LocalDateTime.now();
        Task task = new Task(1L,"New Task", "Do something", Status.TODO, now.plusHours(10), now);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task updatedTask = new Task(1L,"New Task", "Task Ongoing", Status.TODO, now.plusHours(10), now);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        var result = taskService.updateStatus(1L, "IN_PROGRESS");

        assertThat(result.getStatus()).isEqualTo(Status.IN_PROGRESS);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTasksTest() {
        LocalDateTime now =  LocalDateTime.now();
        Task task1 = new Task(1L,"New Task", "Do something", Status.TODO, now.plusHours(10), now);
        Task task2 = new Task(2L,"New Task", "Do something after", Status.TODO, now.plusHours(10), now);
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        var result = taskService.getAll(Pageable.unpaged());

        assertThat(result).contains(task1, task2);
        verify(taskRepository).findAll();
    }

    @Test
    void deleteTaskTest() {
        LocalDateTime now =  LocalDateTime.now();
        Task task = new Task(1L,"New Task", "Do something", Status.TODO, now.plusHours(10), now);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        assertThatNoException().isThrownBy(() -> taskService.delete(1L));

        verify(taskRepository).delete(task);
        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
    }
}
