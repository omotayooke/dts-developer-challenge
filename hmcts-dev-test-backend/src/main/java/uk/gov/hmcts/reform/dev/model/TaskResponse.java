package uk.gov.hmcts.reform.dev.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskResponse {

    private long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime dueDate;
}
