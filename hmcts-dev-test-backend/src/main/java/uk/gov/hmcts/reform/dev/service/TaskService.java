package uk.gov.hmcts.reform.dev.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.exception.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;


@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task create(Task task) {
        if (LocalDateTime.now().isAfter(task.getDueDate())) {
         throw new IllegalArgumentException("The due date should be in the future");
        }
        return repository.save(task);
    }

    public Task getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    public Page<Task> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Task> getByStatus(Status status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    public Task updateStatus(Long id, String status) {
        Task task = getById(id);
        task.setStatus(Enum.valueOf(
            Status.class,
            status.toUpperCase()
        ));
        return repository.save(task);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
