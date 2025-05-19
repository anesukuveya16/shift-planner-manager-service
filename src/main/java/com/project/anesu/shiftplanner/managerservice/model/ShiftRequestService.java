package com.project.anesu.shiftplanner.managerservice.model;

import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.managerservice.service.exception.ShiftRequestNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface ShiftRequestService {

  /**
   * Sends a shift request submitted by the manager.
   *
   * @param employeeId the ID of the employee who is required to approve or decline the shift
   *     request
   * @param shiftRequest the shift that would then be approved or declined
   * @return the approved {@link ShiftRequest} with updated status
   */
  ShiftRequest sendShiftRequestToEmployee(Long employeeId, ShiftRequest shiftRequest);

  /**
   * Approves a shift request submitted by an employee.
   *
   * @param employeeId the ID of the employee who submitted the shift request
   * @param shiftRequestId the ID of the shift request to approve
   * @return the approved {@link ShiftRequest} with updated status
   */
  ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId);

  /**
   * Declines a shift request submitted by an employee, providing a reason.
   *
   * @param employeeId the ID of the employee who submitted the shift request
   * @param rejectionReason the reason for rejecting the shift request
   * @return the declined {@link ShiftRequest} with updated status and reason
   */
  ShiftRequest declineShiftRequest(Long employeeId, String rejectionReason);

  /**
   * Retrieves a shift request for a specific employee.
   *
   * @param employeeId the ID of the employee whose shift request is being retrieved
   * @return the corresponding {@link ShiftRequest}, or {@code null} if not found
   */
  List<ShiftRequest> getShiftRequestByEmployeeId(Long employeeId);

  /**
   * Retrieves a list of shift requests for an employee based on their status.
   *
   * <p>//* @param employeeId the ID of the employee whose shift requests are being retrieved
   *
   * @param status the {@link ShiftRequestStatus} to filter requests
   * @return a list of {@link ShiftRequest} matching the given status, or an empty list if none
   *     found
   */
  ShiftRequest getShiftRequestByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status)
      throws ShiftRequestNotFoundException;

  /**
   * Retrieves shift requests within a specific date range.
   *
   * @param startDate the start of the date range
   * @param endDate the end of the date range
   * @return a list of {@link ShiftRequest} within the specified date range, or an empty list if
   *     none found
   */
  List<ShiftRequest> getShiftRequestByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
