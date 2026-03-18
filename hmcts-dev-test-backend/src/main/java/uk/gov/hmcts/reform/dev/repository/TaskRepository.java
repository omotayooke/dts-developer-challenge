package uk.gov.hmcts.reform.dev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(Status status, Pageable pageable);
}
