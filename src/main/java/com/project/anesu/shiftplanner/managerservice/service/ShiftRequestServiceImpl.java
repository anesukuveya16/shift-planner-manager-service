package com.project.anesu.shiftplanner.managerservice.service;

import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.ScheduleService;
import com.project.anesu.shiftplanner.managerservice.model.ShiftRequestService;
import com.project.anesu.shiftplanner.managerservice.model.repository.ShiftRequestRepository;
import com.project.anesu.shiftplanner.managerservice.service.exception.ShiftRequestNotFoundException;
import com.project.anesu.shiftplanner.managerservice.service.util.ShiftRequestValidator;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShiftRequestServiceImpl implements ShiftRequestService {

  private final ShiftRequestRepository shiftRequestRepository;
  private final ShiftRequestValidator shiftRequestValidator;
  private final ScheduleService scheduleService;

  @Override
  public ShiftRequest sendShiftRequestToEmployee(Long employeeId, ShiftRequest shiftRequest) {

    shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);

    shiftRequest.setEmployeeId(employeeId);
    shiftRequest.setStatus(ShiftRequestStatus.PENDING);

    return shiftRequestRepository.save(shiftRequest);
  }

  @Override
  public ShiftRequest approveShiftRequest(Long employeeId, Long shiftRequestId) {

    ShiftRequest shiftRequest =
        getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);

    shiftRequestValidator.validateShiftRequest(shiftRequest, shiftRequestRepository);

    shiftRequest.setStatus(ShiftRequestStatus.APPROVED);
    ShiftRequest approvedShiftRequest = shiftRequestRepository.save(shiftRequest);

    scheduleService.addShiftToSchedule(employeeId, approvedShiftRequest);

    return approvedShiftRequest;
  }

  @Override
  public ShiftRequest declineShiftRequest(Long shiftRequestId, String rejectionReason) {

    ShiftRequest shiftRequest =
        getShiftRequestByIdAndStatus(shiftRequestId, ShiftRequestStatus.PENDING);

    shiftRequest.setStatus(ShiftRequestStatus.REJECTED);
    shiftRequest.setRejectionReason(rejectionReason);

    return shiftRequestRepository.save(shiftRequest);
  }

  @Override
  public List<ShiftRequest> getShiftRequestByEmployeeId(Long employeeId) {

    return shiftRequestRepository.findByEmployeeId(employeeId);
  }

  @Override
  public ShiftRequest getShiftRequestByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status)
      throws ShiftRequestNotFoundException {

    return shiftRequestRepository
        .findByIdAndStatus(shiftRequestId, status)
        .orElseThrow(
            () ->
                new ShiftRequestNotFoundException(
                    "Could not find shift with status []"
                        + status
                        + " and ID [] "
                        + shiftRequestId
                        + "to approve."));
  }

  @Override
  public List<ShiftRequest> getShiftRequestByDateRange(
      LocalDateTime startDate, LocalDateTime endDate) {

    return shiftRequestRepository.findByShiftDateBetween(startDate, endDate);
  }
}
