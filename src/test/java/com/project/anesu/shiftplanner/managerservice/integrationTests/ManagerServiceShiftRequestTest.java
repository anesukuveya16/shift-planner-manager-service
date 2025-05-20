package com.project.anesu.shiftplanner.managerservice.integrationTests;

import static com.project.anesu.shiftplanner.managerservice.controller.ManagerServiceRestEndpoints.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagerServiceShiftRequestTest {

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Test
  void shouldSuccessfullySendShiftRequestToEmployee() {
    Long employeeId = 10L;

    String shiftRequestBody =
        """
                  {
                  "shiftDate": "2025-06-20T10:00:00",
                  "shiftLengthInHours": 8,
                  "shiftType": "MORNING_SHIFT"
                }
            """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBody)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST, employeeId)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("shiftType", equalTo("MORNING_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));
  }

  @Test
  void shouldApprovePendingShiftRequestFromEmployee() {
    Long employeeId = 1L;

    String shiftRequestBody =
        """
                      {
                      "shiftDate": "2025-10-20T06:00:00",
                      "shiftLengthInHours": 8,
                      "shiftType": "MORNING_SHIFT"
                    }
                """;

    Integer shiftRequestId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(shiftRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SHIFT_REQUEST, employeeId)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("shiftType", equalTo("MORNING_SHIFT"))
            .body("shiftLengthInHours", equalTo(8))
            .extract()
            .path("id");

    String approvedShiftRequestBody =
        """
                     {
                     "shiftDate": "2025-10-20T06:00:00",
                     "shiftLengthInHours": 8,
                     "shiftType": "MORNING_SHIFT",
                     "shiftRequestStatus": "APPROVED"
                   }
               """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(approvedShiftRequestBody)
        .when()
        .put(LANDING_PAGE + APPROVE_SHIFT_REQUEST, employeeId, shiftRequestId)
        .then()
        .statusCode(200)
        .body("status", equalTo("APPROVED"))
        .body("shiftType", equalTo("MORNING_SHIFT"))
        .body("shiftLengthInHours", equalTo(8));
  }

  @Test
  void shouldRejectPendingShiftRequestFromEmployee() {
    Long employeeId = 105L;

    String shiftRequestBody =
        """
                          {
                          "shiftDate": "2025-10-20T15:00:00",
                          "shiftLengthInHours": 6,
                          "shiftType": "AFTERNOON_SHIFT"
                        }
                    """;

    Integer shiftRequestId =
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(shiftRequestBody)
            .when()
            .post(LANDING_PAGE + CREATE_SHIFT_REQUEST, employeeId)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("shiftType", equalTo("AFTERNOON_SHIFT"))
            .body("shiftLengthInHours", equalTo(6))
            .extract()
            .path("id");

    String declineShiftRequestBody =
        """
                          {
                          "shiftDate": "2025-10-20T15:00:00",
                          "shiftLengthInHours": 6,
                          "shiftType": "AFTERNOON_SHIFT",
                          "shiftRequestStatus": "REJECTED",
                          "rejectionReason": "Too many employees in 1 shift."
                        }
                    """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(declineShiftRequestBody)
        .when()
        .put(LANDING_PAGE + DECLINE_SHIFT_REQUEST, shiftRequestId)
        .then()
        .statusCode(200)
        .body("status", equalTo("REJECTED"));
  }

  @Test
  void shouldSuccessfullyRetrieveShiftRequestByGivenEmployeeId() {
    Long employeeId = 200L;

    String shiftRequestBodyOne =
        """
                          {
                          "shiftDate": "2025-10-20T21:00:00",
                          "shiftLengthInHours": 10,
                          "shiftType": "NIGHT_SHIFT"
                        }
                    """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBodyOne)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST, employeeId)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("shiftType", equalTo("NIGHT_SHIFT"))
        .body("shiftLengthInHours", equalTo(10));

    String shiftRequestBodyTwo =
        """
                              {
                              "shiftDate": "2025-10-24T15:00:00",
                              "shiftLengthInHours": 6,
                              "shiftType": "AFTERNOON_SHIFT"
                            }
                        """;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(shiftRequestBodyTwo)
        .when()
        .post(LANDING_PAGE + CREATE_SHIFT_REQUEST, employeeId)
        .then()
        .statusCode(200)
        .body("status", equalTo("PENDING"))
        .body("shiftType", equalTo("AFTERNOON_SHIFT"))
        .body("shiftLengthInHours", equalTo(6));

    RestAssured.given()
        .contentType(ContentType.JSON)
        .when()
        .get(LANDING_PAGE + GET_SHIFT_REQUEST_BY_EMPLOYEE_ID, employeeId)
        .then()
        .statusCode(200)
        .body("findAll { it.shiftType == 'AFTERNOON_SHIFT' }.size()", greaterThanOrEqualTo(1))
        .body("findAll { it.shiftType == 'NIGHT_SHIFT' }.size()", greaterThanOrEqualTo(1));
  }
}
