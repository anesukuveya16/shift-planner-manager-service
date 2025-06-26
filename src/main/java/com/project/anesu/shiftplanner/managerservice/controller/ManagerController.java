package com.project.anesu.shiftplanner.managerservice.controller;

import static com.project.anesu.shiftplanner.managerservice.controller.ManagerServiceRestEndpoints.*;
import static com.project.anesu.shiftplanner.managerservice.controller.ManagerServiceRestEndpoints.LANDING_PAGE;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.service.ScheduleServiceImpl;
import com.project.anesu.shiftplanner.managerservice.service.ShiftRequestServiceImpl;
import com.project.anesu.shiftplanner.managerservice.service.VacationRequestServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(LANDING_PAGE)
@AllArgsConstructor
public class ManagerController {

  private final ScheduleServiceImpl scheduleService;
  private final ShiftRequestServiceImpl shiftRequestService;
  private final VacationRequestServiceImpl vacationRequestService;

  @PostMapping(CREATE_SCHEDULE)
  public Schedule createSchedule(@RequestBody Schedule schedule) {
    return scheduleService.createSchedule(schedule);
  }

  @PutMapping(UPDATE_SCHEDULE)
  public ResponseEntity<Schedule> updateEmployeeSchedule(
      @PathVariable Long scheduleId, @RequestBody Schedule updatedSchedule) {
    Schedule updated = scheduleService.updateEmployeeSchedule(scheduleId, updatedSchedule);

    if (updated != null) {
      return ResponseEntity.ok(updated);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @GetMapping(GET_SCHEDULE_BY_ID)
  public Optional<Schedule> getScheduleById(@PathVariable Long scheduleId) {
    return scheduleService.getScheduleById(scheduleId);
  }

  @GetMapping(GET_SCHEDULES_IN_RANGE)
  public Optional<List<Schedule>> getAllEmployeeSchedulesWithinGivenDateRange(
      @PathVariable Long scheduleId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return scheduleService.getAllEmployeeSchedulesWithinGivenDateRange(
        scheduleId, startDate, endDate);
  }

  @DeleteMapping(DELETE_SCHEDULE)
  public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
    scheduleService.deleteSchedule(scheduleId);
    return ResponseEntity.ok("Schedule deleted successfully.");
  }

  @PostMapping(CREATE_SHIFT_REQUEST)
  public ShiftRequest sendShiftRequestToEmployee(
      @PathVariable Long employeeId, @RequestBody ShiftRequest shiftRequest) {
    return shiftRequestService.sendShiftRequestToEmployee(employeeId, shiftRequest);
  }

  @PutMapping(APPROVE_SHIFT_REQUEST)
  public ShiftRequest approveShiftRequest(
      @PathVariable Long employeeId, @PathVariable Long shiftRequestId) {
    return shiftRequestService.approveShiftRequest(employeeId, shiftRequestId);
  }

  @PutMapping(REJECT_SHIFT_REQUEST)
  public ShiftRequest rejectShiftRequest(
      @PathVariable Long shiftRequestId, @RequestBody String rejectionReason) {
    return shiftRequestService.rejectShiftRequest(shiftRequestId, rejectionReason);
  }

  @GetMapping(GET_SHIFT_REQUEST_BY_EMPLOYEE_ID)
  public List<ShiftRequest> getShiftRequestByEmployeeId(@PathVariable Long employeeId) {
    return shiftRequestService.getShiftRequestByEmployeeId(employeeId);
  }

  @GetMapping(GET_SHIFT_REQUESTS_IN_RANGE)
  public List<ShiftRequest> getShiftRequestByDateRange(
      @PathVariable Long employeeId,
      @RequestParam("startDate") LocalDateTime startDate,
      @RequestParam("endDate") LocalDateTime endDate) {
    return shiftRequestService.getShiftRequestByDateRange(employeeId, startDate, endDate);
  }

  @PutMapping(APPROVE_VACATION_REQUEST)
  public VacationRequest approveVacationRequest(
      @PathVariable Long vacationRequestId, @RequestParam VacationRequestStatus status) {
    return vacationRequestService.approveVacationRequest(vacationRequestId, status);
  }

  @PutMapping(REJECT_VACATION_REQUEST)
  public VacationRequest rejectVacationRequest(
      @PathVariable Long vacationRequestId, @RequestBody String rejectionReason) {
    return vacationRequestService.rejectVacationRequest(vacationRequestId, rejectionReason);
  }

  @GetMapping(GET_VACATIONS_BY_EMPLOYEE_ID)
  public List<VacationRequest> getVacationRequestsByEmployeeId(@PathVariable Long employeeId) {
    return vacationRequestService.getVacationRequestsByEmployeeId(employeeId);
  }

  @GetMapping(GET_EMPLOYEE_VACATIONS_IN_RANGE)
  public List<VacationRequest> getVacationByIdAndDateRange(
      @PathVariable Long employeeId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return vacationRequestService.getVacationByIdAndDateRange(employeeId, startDate, endDate);
  }

  @GetMapping(GET_TEAM_CALENDAR)
  public List<VacationRequest> getTeamCalendar(
      @PathVariable Long officeLocationId,
      @RequestParam LocalDateTime startDate,
      @RequestParam LocalDateTime endDate) {
    return vacationRequestService.getTeamCalendar(officeLocationId, startDate, endDate);
  }
}
