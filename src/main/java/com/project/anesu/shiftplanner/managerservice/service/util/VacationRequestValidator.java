package com.project.anesu.shiftplanner.managerservice.service.util;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.repository.VacationRequestRepository;
import com.project.anesu.shiftplanner.managerservice.service.exception.InvalidVacationRequestException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VacationRequestValidator {

  private static final int MAX_VACATION_DAYS_EACH_YEAR = 30;
  private static final String OVERLAPPING_VACATION_REQUEST_ERROR =
      "Request could not be fulfilled because there is already an approved vacation request for this period for employee: ";

  public void validateVacationRequest(
      VacationRequest vacationRequest, VacationRequestRepository repository) {
    validateAnyOverlappingVacationRequests(vacationRequest, repository);
    validateTheRemainingVacationDays(vacationRequest, repository);
  }

  private void validateAnyOverlappingVacationRequests(
      VacationRequest vacationRequest, VacationRequestRepository repository) {
    if (isOverlappingWithExistingApprovedRequest(vacationRequest, repository)) {
      throw new InvalidVacationRequestException(
          OVERLAPPING_VACATION_REQUEST_ERROR + vacationRequest.getEmployeeId());
    }
  }

  private boolean isOverlappingWithExistingApprovedRequest(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    Long employeeId = vacationRequest.getEmployeeId();

    List<VacationRequest> existingRequests =
        repository.findByEmployeeIdAndDateRange(
            employeeId, vacationRequest.getStartDate(), vacationRequest.getEndDate());

    for (VacationRequest existingRequest : existingRequests) {
      if (existingRequest.getStatus().equals(VacationRequestStatus.APPROVED)
              && isOverlapping(existingRequest, vacationRequest)) {
        return true;
      }
    }
    return false;
  }

  private boolean isOverlapping(VacationRequest existing, VacationRequest newRequest) {
    return !(existing.getEndDate().isBefore(newRequest.getStartDate())
        || existing.getStartDate().isAfter(newRequest.getEndDate()));
  }

  private void validateTheRemainingVacationDays(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    List<VacationRequest> allVacationRequestsForCurrentYear =
        getAllVacationRequestsForCurrentYear(vacationRequest, repository);

    long existingUsedVacationDays = 0;

    // Loop through each vacation request found for the employee in the current year.
    for (VacationRequest existingRequest : allVacationRequestsForCurrentYear) {
      // Check if the current request in the loop is 'APPROVED'.
      if (existingRequest.getStatus().equals(VacationRequestStatus.APPROVED)) {
        // If it IS approved, add its days to our running total.
        existingUsedVacationDays += calculateDaysInRange(existingRequest.getStartDate(), existingRequest.getEndDate());
      }
    }

    int newVacationRequestDays = calculateNewRequestedVacationRequest(vacationRequest);

    long totalVacationDays = existingUsedVacationDays + newVacationRequestDays;

    if (totalVacationDays > MAX_VACATION_DAYS_EACH_YEAR) {
      throw new InvalidVacationRequestException(
          "Vacation request exceeds yearly limit. Employee ID: "
              + vacationRequest.getEmployeeId()
              + " already has "
              + existingUsedVacationDays
              + " days. New request adds "
              + newVacationRequestDays
              + " days, exceeding the maximum of "
              + MAX_VACATION_DAYS_EACH_YEAR
              + " days.");
    }
  }

  private List<VacationRequest> getAllVacationRequestsForCurrentYear(
      VacationRequest vacationRequest, VacationRequestRepository repository) {

    int currentYear = LocalDateTime.now().getYear();
    LocalDateTime startOfTheYear = LocalDateTime.of(currentYear, 1, 1, 0, 0);
    LocalDateTime endOfTheYear = LocalDateTime.of(currentYear, 12, 31, 0, 0);

    return repository.findByEmployeeIdAndOverlappingIntoNewYear(
        vacationRequest.getEmployeeId(), startOfTheYear, endOfTheYear);
  }

  private long calculateDaysInRange(LocalDateTime startDate, LocalDateTime endDate) {
    LocalDate firstDayOfYear = LocalDate.now().withDayOfYear(1);
    LocalDate lastDayOfYear = LocalDate.now().withMonth(12).withDayOfMonth(31);

    LocalDate adjustedStartDate =
        startDate.toLocalDate().isBefore(firstDayOfYear) ? firstDayOfYear : startDate.toLocalDate();

    LocalDate adjustedEndDate =
        endDate.toLocalDate().isAfter(lastDayOfYear) ? lastDayOfYear : endDate.toLocalDate();

    return ChronoUnit.DAYS.between(adjustedStartDate, adjustedEndDate) + 1;
  }

  private long calculatedTotalOfUsedVacationDays(List<VacationRequest> vacationRequests) {
    return vacationRequests.stream()
        .mapToLong(vacation -> calculateDaysInRange(vacation.getStartDate(), vacation.getEndDate()))
        .sum();
  }

  private int calculateNewRequestedVacationRequest(VacationRequest vacationRequest) {
    return (int)
            ChronoUnit.DAYS.between(vacationRequest.getStartDate(), vacationRequest.getEndDate())
        + 1;
  }
}
