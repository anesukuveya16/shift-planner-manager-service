package com.project.anesu.shiftplanner.managerservice.service.util;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.model.ScheduleService;
import com.project.anesu.shiftplanner.managerservice.service.exception.ShiftValidationException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ShiftRequestValidator {
  private static final int MAX_LEGAL_WORKING_HOURS = 10;

  public void validateShiftRequest(ShiftRequest shiftRequest, ScheduleService scheduleService) {

    Optional<Schedule> existingSchedule =
        scheduleService.getEmployeeScheduleForGivenDate(
            shiftRequest.getEmployeeId(), shiftRequest.getShiftDate());

    if (shiftExceedsMaximumWorkingHours(shiftRequest, existingSchedule)) {
      throw new ShiftValidationException(
          "New shift request violates working hours. Employee ID: "
              + shiftRequest.getEmployeeId()
              + " already has "
              + existingSchedule.get().getTotalWorkingHours()
              + " hours for this shift scheduled/recorded. Maximum working hours should not exceed : "
              + MAX_LEGAL_WORKING_HOURS
              + " hours.");
    }
  }

  private boolean shiftExceedsMaximumWorkingHours(
      ShiftRequest incomingShiftRequest, Optional<Schedule> existingSchedule) {
    return existingSchedule.stream()
        .anyMatch(
            scheduledShift ->
                scheduledShift.getTotalWorkingHours() + incomingShiftRequest.getShiftLengthInHours()
                    >= MAX_LEGAL_WORKING_HOURS);
  }
}
