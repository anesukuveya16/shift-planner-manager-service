package com.project.anesu.shiftplanner.managerservice.service.exception;

public class ScheduleNotFoundException extends RuntimeException {
  public ScheduleNotFoundException(String message) {
    super(message);
  }
}
