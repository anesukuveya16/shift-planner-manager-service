package com.project.anesu.shiftplanner.managerservice.integrationTests;

import static com.project.anesu.shiftplanner.managerservice.controller.ManagerServiceRestEndpoints.*;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagerServiceScheduleTest {

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Test
  void shouldCreateScheduleWithoutShiftsOrVacations() {
    String scheduleRequestBody =
        """
                {
                  "employeeId": 1,
                  "startDate": "2025-04-15T08:00:00",
                  "endDate": "2025-04-15T16:00:00",
                  "rejectionReason": null,
                  "shifts": [],
                  "vacations": []
                }
            """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(scheduleRequestBody)
        .when()
        .post(LANDING_PAGE + CREATE_SCHEDULE)
        .then()
        .statusCode(200)
        .body("employeeId", equalTo(1))
        .body("shifts.size()", equalTo(0))
        .body("vacations.size()", equalTo(0));
  }

  @Test
  void shouldUpdateEmployeeScheduleSuccessfully() {
    String existingScheduleRequestBody =
        """
                {
                  "employeeId": 1,
                  "startDate": "2025-04-15T08:00:00",
                  "endDate": "2025-04-15T16:00:00",
                  "rejectionReason": null,
                  "shifts": [],
                  "vacations": []
                }
            """;

    Integer scheduleId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(existingScheduleRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SCHEDULE)
            .then()
            .statusCode(200)
            .body("employeeId", equalTo(1))
            .body("shifts.size()", equalTo(0))
            .body("vacations.size()", equalTo(0))
            .extract()
            .path("id");

    String updateScheduleRequestBody =
        """
               {
                 "employeeId": 1,
                 "startDate": "2025-04-15T10:00:00",
                 "endDate": "2025-12-20T18:00:00",
                 "rejectionReason": null,
                 "shifts": [],
                 "vacations": []
               }
           """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(updateScheduleRequestBody)
        .when()
        .put(LANDING_PAGE + UPDATE_SCHEDULE, scheduleId)
        .then()
        .statusCode(200)
        .body("shifts.size()", equalTo(0))
        .body("vacations.size()", equalTo(0));
  }

  @Test
  void shouldRetrieveEmployeeScheduleByGivenScheduleId() {
    String existingScheduleRequestBody =
        """
                    {
                      "employeeId": 1,
                      "startDate": "2025-04-15T08:00:00",
                      "endDate": "2025-04-15T16:00:00",
                      "rejectionReason": null,
                      "shifts": [],
                      "vacations": []
                    }
                """;

    Integer scheduleId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(existingScheduleRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SCHEDULE)
            .then()
            .statusCode(200)
            .body("employeeId", equalTo(1))
            .body("shifts.size()", equalTo(0))
            .body("vacations.size()", equalTo(0))
            .extract()
            .path("id");

    String retrieveSchedule =
        """
               {
                 "employeeId": 1,
                 "startDate": "2025-04-15T10:00:00",
                 "endDate": "2025-04-15T18:00:00",
                 "rejectionReason": null,
                 "shifts": [],
                 "vacations": []
               }
           """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(retrieveSchedule)
        .when()
        .get(LANDING_PAGE + GET_SCHEDULE_BY_ID, scheduleId)
        .then()
        .statusCode(200)
        .body("employeeId", equalTo(1))
        .body("shifts.size()", equalTo(0))
        .body("vacations.size()", equalTo(0));
  }

  @Test
  void shouldDeleteEmployeeScheduleSuccessfully() {

    String existingScheduleRequestBody =
        """
                {
                  "employeeId": 250,
                  "startDate": "2025-04-15T10:00:00",
                  "endDate": "2025-04-15T18:00:00",
                  "rejectionReason": null,
                  "totalWorkingHours": 10,
                  "shifts": [],
                  "vacations": []
                }
            """;

    Integer scheduleId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(existingScheduleRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SCHEDULE)
            .then()
            .statusCode(200)
            .body("employeeId", equalTo(250))
            .body("shifts.size()", equalTo(0))
            .body("vacations.size()", equalTo(0))
            .extract()
            .path("id");

    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .delete(LANDING_PAGE + DELETE_SCHEDULE, scheduleId)
        .then()
        .statusCode(200);
  }
}
