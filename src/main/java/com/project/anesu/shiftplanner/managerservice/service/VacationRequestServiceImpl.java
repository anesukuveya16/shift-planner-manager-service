package com.project.anesu.shiftplanner.managerservice.service;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.ScheduleService;
import com.project.anesu.shiftplanner.managerservice.model.VacationRequestService;
import com.project.anesu.shiftplanner.managerservice.model.repository.VacationRequestRepository;
import com.project.anesu.shiftplanner.managerservice.service.exception.VacationRequestNotFoundException;
import com.project.anesu.shiftplanner.managerservice.service.util.VacationRequestValidator;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VacationRequestServiceImpl implements VacationRequestService {

  private final VacationRequestRepository vacationRequestRepository;
  private final VacationRequestValidator vacationRequestValidator;
  private final ScheduleService scheduleService;

  @Override
  public VacationRequest approveVacationRequest(
      Long vacationRequestId, VacationRequestStatus status) {

    VacationRequest vacationRequest =
        getVacationRequestByIdAndStatus(vacationRequestId, VacationRequestStatus.PENDING);
    vacationRequestValidator.validateVacationRequest(vacationRequest, vacationRequestRepository);

    vacationRequest.setStatus(VacationRequestStatus.APPROVED);
    VacationRequest approvedVacationRequest = vacationRequestRepository.save(vacationRequest);

    scheduleService.addApprovedVacationRequestToSchedule(
        vacationRequest.getEmployeeId(), approvedVacationRequest);

    return approvedVacationRequest;
  }

  @Override
  public VacationRequest declineVacationRequest(Long vacationRequestId, String rejectionReason) {

    VacationRequest vacationRequest =
        getVacationRequestByIdAndStatus(vacationRequestId, VacationRequestStatus.PENDING);

    vacationRequest.setStatus(VacationRequestStatus.REJECTED);
    vacationRequest.setRejectionReason(rejectionReason);

    return vacationRequestRepository.save(vacationRequest);
  }

  @Override
  public List<VacationRequest> getVacationRequestsByEmployeeId(Long employeeId) {

    return vacationRequestRepository.findByEmployeeId(employeeId);
  }

  @Override
  public List<VacationRequest> getVacationByIdAndDateRange(
      Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {

    return vacationRequestRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
  }

  @Override
  public List<VacationRequest> getTeamCalendar(
      Long officeLocationId, LocalDateTime startDate, LocalDateTime endDate) {

    return vacationRequestRepository.findByOfficeLocationIdAndStatusAndDateRange(
        officeLocationId,
        List.of(VacationRequestStatus.PENDING, VacationRequestStatus.APPROVED),
        startDate,
        endDate);
  }

  @Override
  public VacationRequest getVacationRequestByIdAndStatus(
      Long vacationRequestId, VacationRequestStatus status)
      throws VacationRequestNotFoundException {

    return vacationRequestRepository
        .findByIdAndStatus(vacationRequestId, status)
        .orElseThrow(
            () ->
                new VacationRequestNotFoundException(
                    "Vacation request with id: " + vacationRequestId + " does not exist."));
  }
}
