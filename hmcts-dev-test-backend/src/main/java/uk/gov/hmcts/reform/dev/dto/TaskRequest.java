package uk.gov.hmcts.reform.dev.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private String status;

    @NotNull
    private LocalDateTime dueDate;
}
