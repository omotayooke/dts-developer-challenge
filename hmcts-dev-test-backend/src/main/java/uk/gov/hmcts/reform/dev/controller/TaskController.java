package uk.gov.hmcts.reform.dev.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.mapper.TaskMapper;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.model.TaskResponse;
import uk.gov.hmcts.reform.dev.service.TaskService;

@RestController
@RequestMapping("/tasks")
@CrossOrigin
public class TaskController {

    private final TaskService service;
    private final TaskMapper mapper;

    public TaskController(TaskService service, TaskMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public Task create(@Valid @RequestBody TaskRequest taskRequest) {
        Task task = mapper.toEntity(taskRequest);
        return service.create(task);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        Task task = service.getById(id);
        return mapper.toResponse(task);
    }

    @GetMapping
    public Page<TaskResponse> getAll(@RequestParam(required = false) String status,
                                     Pageable pageable) {
        Page<Task> page = (status == null)
            ? service.getAll(pageable)
            : service.getByStatus(Status.valueOf(status), pageable);
        return page.map(mapper::toResponse);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        Task task = service.updateStatus(id, status);
        return mapper.toResponse(task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
