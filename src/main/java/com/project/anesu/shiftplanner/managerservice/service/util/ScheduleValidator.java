package com.project.anesu.shiftplanner.managerservice.service.util;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftEntry;
import com.project.anesu.shiftplanner.managerservice.service.exception.InvalidScheduleException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ScheduleValidator {

  private static final int MAX_WORKING_HOURS_PER_SHIFT = 8;
  private static final int MAX_WORKING_HOURS_PER_WEEK = 40;

  public void validateSchedule(Schedule schedule) {
    validateDates(schedule);
    validateWorkingHours(schedule);
  }

  private void validateDates(Schedule schedule) {
    if (schedule.getStartDate() == null || schedule.getEndDate() == null) {
      throw new InvalidScheduleException("Start date or end date is null");
    }
    if (schedule.getStartDate().isAfter(schedule.getEndDate())) {
      throw new InvalidScheduleException("Start date cannot be after end date");
    }

    if (schedule.getShifts() == null && schedule.getVacations() == null) {
      throw new InvalidScheduleException("Shifts or vacations must be provided.");
    }
  }

  private void validateWorkingHours(Schedule schedule) {
    for (ShiftEntry shift : schedule.getShifts()) {
      if (shift.getWorkingHours() > MAX_WORKING_HOURS_PER_SHIFT) {
        throw new InvalidScheduleException("Shift exceeds maximum working hours.");
      }
    }

    Map<LocalDateTime, Long> dailyWorkingHours =
        schedule.getShifts().stream()
            .collect(
                Collectors.groupingBy(
                    ShiftEntry::getShiftDate, Collectors.summingLong(ShiftEntry::getWorkingHours)));

    for (LocalDateTime date : dailyWorkingHours.keySet()) {
      long weeklyHours = calculateWeeklyHours(dailyWorkingHours, date);
      if (weeklyHours > MAX_WORKING_HOURS_PER_WEEK) {
        throw new InvalidScheduleException("Weekly working hours exceed maximum limit.");
      }
    }
  }

  private long calculateWeeklyHours(
      Map<LocalDateTime, Long> dailyWorkingHours, LocalDateTime date) {
    LocalDateTime weekStart = date.with(DayOfWeek.MONDAY);
    LocalDateTime weekEnd = date.with(DayOfWeek.SUNDAY);

    return dailyWorkingHours.entrySet().stream()
        .filter(entry -> !entry.getKey().isBefore(weekStart) && !entry.getKey().isAfter(weekEnd))
        .mapToLong(Map.Entry::getValue)
        .sum();
  }
}
