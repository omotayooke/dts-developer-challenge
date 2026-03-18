package uk.gov.hmcts.reform.dev.mapper;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.model.TaskResponse;

import java.time.LocalDateTime;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setStatus(Status.valueOf(req.getStatus()));
        task.setDueDate(req.getDueDate());
        task.setCreatedDate(LocalDateTime.now());
        return task;
    }

    public TaskResponse toResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setStatus(task.getStatus().name());
        res.setDueDate(task.getDueDate());
        return res;
    }
}
