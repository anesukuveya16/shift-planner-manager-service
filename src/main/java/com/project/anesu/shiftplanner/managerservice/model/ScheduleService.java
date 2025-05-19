package com.project.anesu.shiftplanner.managerservice.model;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.service.exception.ScheduleNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

  /**
   * Creates a new {@link Schedule} for an employee.
   *
   * @param schedule the {@link Schedule} to be created
   * @return the created {@link Schedule}
   */
  Schedule createSchedule(Schedule schedule);

  /**
   * Updates a specific employee's schedule based on the manager's decision.
   *
   * @param updatedSchedule the updated schedule information
   * @return the updated {@link Schedule}
   */
  Schedule updateEmployeeSchedule(Long scheduleId, Schedule updatedSchedule)
      throws ScheduleNotFoundException;

  /**
   * Adds a new shift to an employee's schedule after approving a {@link ShiftRequest}.
   *
   * @param employeeId the ID of the employee to update
   * @param approvedShiftRequest the approved {@link ShiftRequest}
   * @return the updated {@link Schedule}
   */
  Schedule addShiftToSchedule(Long employeeId, ShiftRequest approvedShiftRequest);

  /**
   * Retrieves a schedule for a specific employee by ID.
   *
   * @param scheduleId the ID of the {@link Schedule} to retrieve
   * @return the found {@link Schedule}, or {@code null} if not found
   */
  Optional<Schedule> getScheduleById(Long scheduleId);

  /**
   * Retrieves a list of schedules for all employees within a date range.
   *
   * @param startDate the start of the {@link LocalDateTime} range
   * @param endDate the end of the {@link LocalDateTime} range
   * @return a list of {@link Schedule}s for all employees within the date range
   */
  Optional<List<Schedule>> getAllEmployeeSchedulesWithinGivenDateRange(
      Long scheduleId, LocalDateTime startDate, LocalDateTime endDate);

  /**
   * Adds approved vacation requests to an employee's schedule after approving a {@link
   * VacationRequest}.
   *
   * @param employeeId the ID of the employee to update
   * @param approvedVacationRequest the approved {@link VacationRequest}
   * @return the updated {@link Schedule}
   */
  Schedule addApprovedVacationRequestToSchedule(
      Long employeeId, VacationRequest approvedVacationRequest);

  /**
   * Deletes a specific employee's schedule by ID (only if applicable to manager's permissions).
   *
   * @param scheduleId the ID of the {@link Schedule} to delete
   */
  void deleteSchedule(Long scheduleId) throws ScheduleNotFoundException;
}
