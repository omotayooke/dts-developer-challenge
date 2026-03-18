package uk.gov.hmcts.reform.dev.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.Task;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
public class IntegrationTaskTest {

    @MockitoBean
    TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private transient MockMvc mockMvc;

    @DisplayName("Should create a Task with 201 response code and Location header")
    @Test
    void createTaskEndpoint() throws Exception {

        Task model = new Task(1L, "Task",
                              "Write Report", Status.TODO, LocalDateTime.now().plusWeeks(2), LocalDateTime.now());
        when(taskService.create(model)).thenReturn(model);

        MvcResult response = mockMvc.perform(post("/tasks")
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .content(objectMapper.writeValueAsString(model)))
            .andExpect(status().isCreated()).andReturn();

        var location = response.getResponse().getHeader("Location");
        assertThat(location).startsWith("/tasks/");
    }
}
