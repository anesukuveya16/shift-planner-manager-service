package com.project.anesu.shiftplanner.managerservice.integrationTests;

import static com.project.anesu.shiftplanner.managerservice.controller.ManagerServiceRestEndpoints.*;
import static org.hamcrest.Matchers.*;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.repository.VacationRequestRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagerServiceVacationRequestTest {

  @LocalServerPort private int port;

  private Long vacationRequestId;

  private static final Long EMPLOYEE_ID = 1L;
  private static final Long OFFICE_LOCATION_ID = 200L;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    vacationRequestRepository.deleteAll();

    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setEmployeeId(EMPLOYEE_ID);
    vacationRequest.setOfficeLocationId(OFFICE_LOCATION_ID);
    vacationRequest.setStatus(VacationRequestStatus.PENDING);
    vacationRequest.setRejectionReason("Valid rejection reason");
    vacationRequest.setStartDate(LocalDateTime.of(2025, 1, 5, 0, 0));
    vacationRequest.setEndDate(LocalDateTime.of(2025, 1, 20, 0, 0));

    VacationRequest savedVacationRequest = vacationRequestRepository.save(vacationRequest);
    vacationRequestId = savedVacationRequest.getId();
  }

  @Autowired private VacationRequestRepository vacationRequestRepository;

  @Test
  void shouldApproveExistingVacationRequest() {

    RestAssured.given()
        .contentType(ContentType.JSON)
        .queryParam("status", "APPROVED")
        .when()
        .put(LANDING_PAGE + APPROVE_VACATION_REQUEST, vacationRequestId)
        .then()
        .statusCode(200)
        .body("status", equalTo("APPROVED"));
  }

  @Test
  void shouldAllowManagerToDeclineVacationRequestWithValidReason() {

    String vacationRequestBody =
        """
              {
                "rejectionReason": "Valid rejection reason."
              }
              """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(vacationRequestBody)
        .queryParam("status", "REJECTED")
        .when()
        .put(LANDING_PAGE + REJECT_VACATION_REQUEST, vacationRequestId)
        .then()
        .statusCode(200)
        .body("status", equalTo("REJECTED"));
  }

  @Test
  void getAllExistingVacationRequestsByGivenEmployeeId() {

    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + GET_VACATIONS_BY_EMPLOYEE_ID, EMPLOYEE_ID)
        .then()
        .statusCode(200)
        .body("[0].employeeId", equalTo(EMPLOYEE_ID.intValue()));
  }

  @Test
  void getRequestedVacationRequestsWithin_GivenDateRange() {

    String startDate = "2025-01-01T00:00:00";
    String endDate = "2025-03-31T23:59:59";

    RestAssured.given()
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + GET_EMPLOYEE_VACATIONS_IN_RANGE, EMPLOYEE_ID)
        .then()
        .statusCode(200)
        .body("$", hasSize(1))
        .body("[0].employeeId", equalTo(EMPLOYEE_ID.intValue()));
  }

  @Test
  void getTeamCalendar_WithinGivenDateRange() {

    String startDate = "2025-01-01T00:00:00";
    String endDate = "2025-12-31T23:59:59";

    RestAssured.given()
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + GET_TEAM_CALENDAR, OFFICE_LOCATION_ID)
        .then()
        .statusCode(200)
        .body("[0].officeLocationId", equalTo(OFFICE_LOCATION_ID.intValue()));
  }
}
