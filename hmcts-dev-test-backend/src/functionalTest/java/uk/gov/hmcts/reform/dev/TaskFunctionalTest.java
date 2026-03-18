package uk.gov.hmcts.reform.dev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import uk.gov.hmcts.reform.dev.dto.TaskRequest;
import uk.gov.hmcts.reform.dev.model.Status;
import uk.gov.hmcts.reform.dev.model.TaskResponse;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties =
    {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:public",
        "spring.datasource.username=sa",
        "spring.datasource.password=password"
    })
public class TaskFunctionalTest {

    @Autowired
    ObjectMapper objectMapper;

    @LocalServerPort
    int port;

    private String testUrl;

    @BeforeEach
    public void setUp() {
        testUrl = "http://localhost:" + port;
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void createTaskTest() throws JsonProcessingException {
        TaskRequest model = new TaskRequest("Task", "Hello", "TODO", LocalDateTime.now().plusMinutes(5));

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl + "/tasks")
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
        assertThat(response.getBody().asString()).contains("Hello");
    }

    @Test
    void createTaskTestDateIsUnprocessable() throws JsonProcessingException {
        TaskRequest model = new TaskRequest("Task", "Hello", "TODO", LocalDateTime.now().minusDays(1));

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl + "/tasks")
            .then()
            .extract().response().andReturn();

        Assertions.assertEquals(422, response.statusCode());
        assertThat(response.getBody().asString()).contains("The due date should be in the future");
    }

    @Test
    void updateTaskTest() throws JsonProcessingException {
        TaskRequest model = new TaskRequest("Task", "Hello", "TODO", LocalDateTime.now().plusDays(10));

        Response responseTask = given()
            .contentType(ContentType.JSON)
            .when()
            .body(objectMapper.writeValueAsString(model))
            .post(testUrl + "/tasks")
            .then()
            .extract().response().andReturn();

        TaskResponse task = objectMapper.readValue(responseTask.getBody().asString(), TaskResponse.class);

        Response response = given()
            .contentType(ContentType.JSON)
            .queryParam("status", "DONE")
            .when()
            .patch(testUrl + "/tasks/{id}/status", task.getId())
            .then()
            .extract()
            .response();

        assertThat(response.statusCode()).isEqualTo(200);
        TaskResponse updated = objectMapper.readValue(response.getBody().asString(), TaskResponse.class);
        assertThat(updated.getStatus()).isEqualTo(Status.DONE.name());

    }
}
