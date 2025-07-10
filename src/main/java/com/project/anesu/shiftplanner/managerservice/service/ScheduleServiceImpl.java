package com.project.anesu.shiftplanner.managerservice.service;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftEntry;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationEntry;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.ScheduleService;
import com.project.anesu.shiftplanner.managerservice.model.repository.ScheduleRepository;
import com.project.anesu.shiftplanner.managerservice.service.exception.InvalidScheduleException;
import com.project.anesu.shiftplanner.managerservice.service.exception.ScheduleNotFoundException;
import com.project.anesu.shiftplanner.managerservice.service.util.ScheduleValidator;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final ScheduleValidator scheduleValidator;

  @Override
  public Schedule createSchedule(Schedule schedule) {

    return scheduleRepository.save(schedule);
  }

  @Override
  public Schedule updateEmployeeSchedule(Long scheduleId, Schedule updatedSchedule)
      throws ScheduleNotFoundException {

    Schedule existingEmployeeSchedule =
        scheduleRepository
            .findById(scheduleId)
            .orElseThrow(() -> new ScheduleNotFoundException((scheduleId)));

    Schedule newlyUpdatedSchedule =
        updateExistingEmployeeSchedule(updatedSchedule, existingEmployeeSchedule);

    scheduleValidator.validateSchedule(newlyUpdatedSchedule);

    return scheduleRepository.save(newlyUpdatedSchedule);
  }

  @Override
  public Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest) {

    validateApprovedShiftRequest(approvedShiftRequest);

    Optional<Schedule> scheduleInApprovedShiftCalenderWeek =
        getScheduleForApprovedShiftCalendarWeek(employeeId, approvedShiftRequest);

    Schedule schedule =
        scheduleInApprovedShiftCalenderWeek
            .map(
                existingSchedule ->
                    addNewShiftEntryToExistingSchedule(approvedShiftRequest, existingSchedule))
            .orElseGet(() -> createNewScheduleForApprovedShift(employeeId, approvedShiftRequest));

    return scheduleRepository.save(schedule);
  }

  @Override
  public Schedule addApprovedVacationRequestToSchedule(
      Long employeeId, VacationRequest approvedVacationRequest) {

    validateApprovedVacationRequest(approvedVacationRequest);

    Optional<Schedule> scheduleInApprovedVacationCalenderWeek =
        getScheduleForApprovedVacationCalendarWeek(employeeId, approvedVacationRequest);

    Schedule schedule =
        scheduleInApprovedVacationCalenderWeek
            .map(
                existingSchedule ->
                    addNewVacationEntryToExistingSchedule(
                        approvedVacationRequest, existingSchedule))
            .orElseGet(
                () ->
                    createNewScheduleForApprovedVacationRequest(
                        employeeId, approvedVacationRequest));

    return scheduleRepository.save(schedule);
  }

  @Override
  public Optional<Schedule> getScheduleById(Long scheduleId) {

    return scheduleRepository.findById(scheduleId);
  }

  @Override
  public Optional<List<Schedule>> getAllEmployeeSchedulesWithinGivenDateRange(
      Long scheduleId, LocalDateTime startDate, LocalDateTime endDate) {

    return scheduleRepository.findByEmployeeIdAndDateRange(scheduleId, startDate, endDate);
  }

  @Override
  public Optional<Schedule> getEmployeeScheduleForGivenDate(
      Long employeeId, LocalDateTime shiftDate) {
    return scheduleRepository.findByEmployeeIdAndDateRange(employeeId, shiftDate);
  }

  @Override
  public void deleteSchedule(Long scheduleId) throws ScheduleNotFoundException {

    if (!scheduleRepository.existsById(scheduleId)) {
      throw new ScheduleNotFoundException(scheduleId);
    }
    scheduleRepository.deleteById(scheduleId);
  }

  private Schedule updateExistingEmployeeSchedule(
      Schedule updatedSchedule, Schedule existingSchedule) {

    existingSchedule.setStartDate(updatedSchedule.getStartDate());
    existingSchedule.setEndDate(updatedSchedule.getEndDate());
    existingSchedule.setShifts(updatedSchedule.getShifts());
    existingSchedule.setVacations(updatedSchedule.getVacations());
    existingSchedule.setTotalWorkingHours(updatedSchedule.getTotalWorkingHours());
    return existingSchedule;
  }

  private LocalDateTime determineShiftEndDate(ShiftRequest approvedShiftRequest) {

    return approvedShiftRequest
        .getShiftDate()
        .plusHours(approvedShiftRequest.getShiftLengthInHours());
  }

  private void validateApprovedVacationRequest(VacationRequest approvedVacationRequest) {

    if (!VacationRequestStatus.APPROVED.equals(approvedVacationRequest.getStatus())) {
      throw new InvalidScheduleException(
          "Invalid schedule operation. Only approved vacation requests can be added to the schedule.");
    }
  }

  private Schedule addNewVacationEntryToExistingSchedule(
      VacationRequest approvedVacationRequest, Schedule scheduleInApprovedVacationCalenderWeek) {

    scheduleInApprovedVacationCalenderWeek
        .getVacations()
        .add(VacationEntry.fromApprovedVacationRequest(approvedVacationRequest));

    return scheduleInApprovedVacationCalenderWeek;
  }

  private Schedule addNewShiftEntryToExistingSchedule(
      ShiftRequest approvedShiftRequest, Schedule scheduleInApprovedShiftCalenderWeek) {

    scheduleInApprovedShiftCalenderWeek
        .getShifts()
        .add(ShiftEntry.fromApprovedShiftEntry(approvedShiftRequest));
    return scheduleInApprovedShiftCalenderWeek;
  }

  private Schedule createNewScheduleForApprovedVacationRequest(
      Long employeeId, VacationRequest approvedVacationRequest) {

    List<VacationEntry> vacationEntries = new ArrayList<>();
    vacationEntries.add(VacationEntry.fromApprovedVacationRequest(approvedVacationRequest));

    return Schedule.builder()
        .employeeId(employeeId)
        .startDate(approvedVacationRequest.getStartDate())
        .endDate(approvedVacationRequest.getEndDate())
        .vacations(vacationEntries)
        .build();
  }

  private Optional<Schedule> getScheduleForApprovedVacationCalendarWeek(
      Long employeeId, VacationRequest approvedVacationRequest) {

    LocalDateTime startOfVacationCalendarWeek =
        approvedVacationRequest.getStartDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfVacationCalendarWeek =
        approvedVacationRequest.getEndDate().with(DayOfWeek.SUNDAY);

    return scheduleRepository.findByEmployeeIdAndWeekRange(
        employeeId, startOfVacationCalendarWeek, endOfVacationCalendarWeek);
  }

  private void validateApprovedShiftRequest(ShiftRequest approvedShiftRequest) {

    if (!ShiftRequestStatus.APPROVED.equals(approvedShiftRequest.getStatus())) {
      throw new InvalidScheduleException(
          "Invalid schedule operation. Only approved shifts can be added to the schedule.");
    }
  }

  private Optional<Schedule> getScheduleForApprovedShiftCalendarWeek(
      Long employeeId, ShiftRequest approvedShiftRequest) {

    LocalDateTime startOfShiftCalendarWeek =
        approvedShiftRequest.getShiftDate().with(DayOfWeek.MONDAY);
    LocalDateTime endOfShiftCalendarWeek =
        approvedShiftRequest.getShiftDate().with(DayOfWeek.SUNDAY);

    return scheduleRepository.findByEmployeeIdAndWeekRange(
        employeeId, startOfShiftCalendarWeek, endOfShiftCalendarWeek);
  }

  private Schedule createNewScheduleForApprovedShift(
      Long employeeId, ShiftRequest approvedShiftRequest) {

    List<ShiftEntry> shiftEntries = new ArrayList<>();
    shiftEntries.add(ShiftEntry.fromApprovedShiftEntry(approvedShiftRequest));

    return Schedule.builder()
        .employeeId(employeeId)
        .startDate(approvedShiftRequest.getShiftDate())
        .endDate(determineShiftEndDate(approvedShiftRequest))
        .totalWorkingHours(approvedShiftRequest.getShiftLengthInHours())
        .shifts(shiftEntries)
        .build();
  }
}
