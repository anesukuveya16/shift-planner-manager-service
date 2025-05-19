package com.project.anesu.shiftplanner.managerservice.model;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.service.exception.VacationRequestNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing vacation requests. Provides methods for approving, declining, and
 * retrieving vacation requests, as well as fetching vacation schedules for teams.
 */
public interface VacationRequestService {

  /**
   * Approves a vacation request and updates its status.
   *
   * @param vacationRequestId The ID of the vacation request to approve.
   * @param status The new status of the vacation request.
   * @return The updated {@link VacationRequest} after approval.
   */
  VacationRequest approveVacationRequest(Long vacationRequestId, VacationRequestStatus status)
      throws VacationRequestNotFoundException;

  /**
   * Declines a vacation request with a given reason.
   *
   * @param vacationRequestId The ID of the vacation request to decline.
   * @param rejectionReason The reason for rejecting the request.
   * @return The updated {@link VacationRequest} after being declined.
   */
  VacationRequest declineVacationRequest(Long vacationRequestId, String rejectionReason);

  /**
   * Retrieves a list of vacation requests for a specific employee.
   *
   * @param employeeId The ID of the employee.
   * @return A list of {@link VacationRequest} objects for the given employee.
   */
  List<VacationRequest> getVacationRequestsByEmployeeId(Long employeeId);

  /**
   * Retrieves vacation requests for an employee within a specified date range.
   *
   * @param employeeId The ID of the employee.
   * @param startDate The start date of the requested range.
   * @param endDate The end date of the requested range.
   * @return A list of {@link VacationRequest} objects within the given date range.
   */
  List<VacationRequest> getVacationByIdAndDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Retrieves the vacation schedule for a team within a specified date range.
   *
   * @param officeLocationId The ID of the office location.
   * @param startDate The start date of the requested schedule.
   * @param endDate The end date of the requested schedule.
   * @return A list of {@link VacationRequest} objects representing the team's calendar.
   */
  List<VacationRequest> getTeamCalendar(
      Long officeLocationId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Retrieves the vacation schedule for an employee by the id and status of the vacation request.
   *
   * @param vacationRequestId The ID of the vacation request.
   * @param status The current status of the requested vacation.
   * @return A list of {@link VacationRequest} objects representing the team's calendar.
   */
  VacationRequest getVacationRequestByIdAndStatus(
      Long vacationRequestId, VacationRequestStatus status) throws VacationRequestNotFoundException;
}
