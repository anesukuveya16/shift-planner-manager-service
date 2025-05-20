package com.project.anesu.shiftplanner.managerservice.unitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.ScheduleService;
import com.project.anesu.shiftplanner.managerservice.model.repository.VacationRequestRepository;
import com.project.anesu.shiftplanner.managerservice.service.VacationRequestServiceImpl;
import com.project.anesu.shiftplanner.managerservice.service.exception.VacationRequestNotFoundException;
import com.project.anesu.shiftplanner.managerservice.service.util.VacationRequestValidator;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VacationRequestServiceImplTest {

  @Mock private VacationRequestRepository vacationRequestRepositoryMock;
  @Mock private VacationRequestValidator vacationRequestValidatorMock;
  @Mock private ScheduleService scheduleServiceMock;

  private VacationRequestServiceImpl cut;

  @BeforeEach
  void setUp() {
    cut =
        new VacationRequestServiceImpl(
            vacationRequestRepositoryMock, vacationRequestValidatorMock, scheduleServiceMock);
  }

  @Test
  void approveVacationRequest_AndChangeStatusToApproved_AfterValidationHasPassed() {

    // Given

    VacationRequest vacationRequest = givenVacationRequest();

    when(vacationRequestRepositoryMock.findByIdAndStatus(
            vacationRequest.getId(), VacationRequestStatus.PENDING))
        .thenReturn(Optional.of(vacationRequest));
    when(vacationRequestRepositoryMock.save(any(VacationRequest.class)))
        .thenReturn(vacationRequest);

    // When
    VacationRequest approvedVacationRequest =
        cut.approveVacationRequest(vacationRequest.getId(), VacationRequestStatus.APPROVED);

    // Then
    verify(vacationRequestRepositoryMock, times(1)).save(vacationRequest);
    verify(scheduleServiceMock)
        .addApprovedVacationRequestToSchedule(
            vacationRequest.getEmployeeId(), approvedVacationRequest);
  }

  @Test
  void declineVacationRequest_ChangeStatusToDeclined() {

    // Given
    Long vacationRequestId = 10L;
    String rejectionReason = "Required";

    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setId(vacationRequestId);
    vacationRequest.setStatus(VacationRequestStatus.PENDING);

    when(vacationRequestRepositoryMock.findByIdAndStatus(
            vacationRequestId, vacationRequest.getStatus()))
        .thenReturn(Optional.of(vacationRequest));
    when(vacationRequestRepositoryMock.save(any(VacationRequest.class)))
        .thenReturn(vacationRequest);

    // When
    VacationRequest rejectedVacationRequest =
        cut.declineVacationRequest(vacationRequestId, rejectionReason);

    // Then
    assertEquals(VacationRequestStatus.REJECTED, rejectedVacationRequest.getStatus());

    verify(vacationRequestRepositoryMock, times(1)).save(vacationRequest);
  }

  @Test
  void approveVacationRequest_ShouldThrowExceptionWhenVacationRequestIdIsNotFound() {

    // Given
    Long vacationRequestId = 10L;
    VacationRequestStatus status = VacationRequestStatus.PENDING;

    when(vacationRequestRepositoryMock.findByIdAndStatus(vacationRequestId, status))
        .thenReturn(Optional.empty());

    // When & Then
    assertThrows(
        VacationRequestNotFoundException.class,
        () -> cut.approveVacationRequest(vacationRequestId, status));

    verify(vacationRequestRepositoryMock, times(1)).findByIdAndStatus(vacationRequestId, status);
    verifyNoMoreInteractions(vacationRequestRepositoryMock);
  }

  @Test
  void shouldRetrieveListOfVacationRequestsThroughTheEmployeeId() {
    // Given

    Long employeeId = 1L;
    VacationRequest vacationRequest = new VacationRequest();

    when(vacationRequestRepositoryMock.findByEmployeeId(employeeId))
        .thenReturn(List.of(vacationRequest));

    // When
    List<VacationRequest> retrievedVacationRequest =
        cut.getVacationRequestsByEmployeeId(employeeId);

    // Then
    assertNotNull(retrievedVacationRequest);
  }

  @Test
  void shouldThrowExceptionWhenVacationRequestIsNotFoundByIdAndStatus() {

    // Given

    VacationRequest vacationRequest = givenVacationRequest();

    when(vacationRequestRepositoryMock.findByIdAndStatus(
            vacationRequest.getId(), VacationRequestStatus.PENDING))
        .thenReturn(Optional.empty());

    // When
    assertThrows(
        VacationRequestNotFoundException.class,
        () ->
            cut.getVacationRequestByIdAndStatus(
                vacationRequest.getId(), VacationRequestStatus.PENDING));

    // Then
    verify(vacationRequestRepositoryMock, times(1))
        .findByIdAndStatus(vacationRequest.getId(), VacationRequestStatus.PENDING);
    verifyNoMoreInteractions(vacationRequestRepositoryMock);
  }

  @Test
  void shouldSuccessfullyRetrieveGivenEmployeeVacationRequests_ByIdAndDateRange() {

    // Given

    VacationRequest vacationRequest = givenVacationRequest();

    when(vacationRequestRepositoryMock.findByEmployeeIdAndDateRange(
            vacationRequest.getEmployeeId(),
            vacationRequest.getStartDate(),
            vacationRequest.getEndDate()))
        .thenReturn(List.of(vacationRequest));

    // When
    List<VacationRequest> vacationRequests =
        cut.getVacationByIdAndDateRange(
            vacationRequest.getEmployeeId(),
            vacationRequest.getStartDate(),
            vacationRequest.getEndDate());

    // Then
    assertNotNull(vacationRequests);
    assertEquals(1, vacationRequests.size());

    verify(vacationRequestRepositoryMock, times(1))
        .findByEmployeeIdAndDateRange(
            vacationRequest.getEmployeeId(),
            vacationRequest.getStartDate(),
            vacationRequest.getEndDate());
  }

  @Test
  void shouldSuccessfullyRetrieve_TeamVacationRequests() {
    // Given

    List<VacationRequest> vacationRequests = approvedVacationRequests();

    VacationRequest submittedVacationRequest = givenVacationRequest();
    when(vacationRequestRepositoryMock.findByOfficeLocationIdAndStatusAndDateRange(
            submittedVacationRequest.getOfficeLocationId(),
            List.of(VacationRequestStatus.PENDING, VacationRequestStatus.APPROVED),
            submittedVacationRequest.getStartDate(),
            submittedVacationRequest.getEndDate()))
        .thenReturn(List.of(submittedVacationRequest));

    // When
    List<VacationRequest> currentTeamCalendar =
        cut.getTeamCalendar(
            submittedVacationRequest.getOfficeLocationId(),
            submittedVacationRequest.getStartDate(),
            submittedVacationRequest.getEndDate());

    // Then
    assertNotNull(currentTeamCalendar);
    assertEquals(1, currentTeamCalendar.size());

    verify(vacationRequestRepositoryMock, times(1))
        .findByOfficeLocationIdAndStatusAndDateRange(
            submittedVacationRequest.getOfficeLocationId(),
            List.of(VacationRequestStatus.PENDING, VacationRequestStatus.APPROVED),
            submittedVacationRequest.getStartDate(),
            submittedVacationRequest.getEndDate());
  }

  private VacationRequest givenVacationRequest() {
    VacationRequest vacationRequest = new VacationRequest();
    vacationRequest.setId(1L);
    vacationRequest.setEmployeeId(25L);
    vacationRequest.setOfficeLocationId(250L);
    vacationRequest.setStartDate(LocalDateTime.now());
    vacationRequest.setEndDate(LocalDateTime.now().plusDays(8));
    vacationRequest.setStatus(VacationRequestStatus.PENDING);
    return vacationRequest;
  }

  private ArrayList<VacationRequest> approvedVacationRequests() {
    return new ArrayList<>(
        Arrays.asList(
            new VacationRequest() {
              {
                setEmployeeId(3L);
                setOfficeLocationId(250L);
                setStartDate(LocalDateTime.now());
                setEndDate(LocalDateTime.now().plusDays(8));
                setStatus(VacationRequestStatus.APPROVED);
              }
            },
            new VacationRequest() {
              {
                setEmployeeId(2L);
                setOfficeLocationId(250L);
                setStartDate(LocalDateTime.now());
                setEndDate(LocalDateTime.now().plusDays(10));
                setStatus(VacationRequestStatus.APPROVED);
              }
            }));
  }
}
