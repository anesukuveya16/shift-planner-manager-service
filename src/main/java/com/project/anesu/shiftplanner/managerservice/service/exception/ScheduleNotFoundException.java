package com.project.anesu.shiftplanner.managerservice.service.exception;

public class ScheduleNotFoundException extends RuntimeException {
  private static final String SCHEDULE_NOT_FOUND_EXCEPTION = "Schedule not found with id: %s";

  public ScheduleNotFoundException(Long scheduleId) {
    super(SCHEDULE_NOT_FOUND_EXCEPTION.formatted(scheduleId));
  }
}
