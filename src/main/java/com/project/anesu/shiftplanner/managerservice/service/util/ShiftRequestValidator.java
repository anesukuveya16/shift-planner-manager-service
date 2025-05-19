package com.project.anesu.shiftplanner.managerservice.service.util;

import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.repository.ShiftRequestRepository;
import com.project.anesu.shiftplanner.managerservice.service.exception.ShiftValidationException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ShiftRequestValidator {
  private static final int MAX_LEGAL_WORKING_HOURS = 10;

  public void validateShiftRequest(
      ShiftRequest shiftRequest, ShiftRequestRepository shiftRequestRepository) {
    Optional<ShiftRequest> shiftRequestOptional =
        shiftRequestRepository.findByEmployeeIdAndShiftDateAndStatus(
            shiftRequest.getEmployeeId(), shiftRequest.getShiftDate(), ShiftRequestStatus.APPROVED);

    if (shiftRequestOptional.isPresent()) {
      ShiftRequest existingShift = shiftRequestOptional.get();
      boolean exceedsMaximumWorkingHours =
          existingShift.getShiftLengthInHours() + shiftRequest.getShiftLengthInHours()
              >= MAX_LEGAL_WORKING_HOURS;

      if (exceedsMaximumWorkingHours) {
        throw new ShiftValidationException(
            "New shift request violates working hours. Employee ID: "
                + shiftRequest.getEmployeeId()
                + " already has "
                + shiftRequestOptional.get().getShiftLengthInHours()
                + " hours for this shift scheduled/recorded. Maximum working hours should not exceed : "
                + MAX_LEGAL_WORKING_HOURS
                + " hours.");
      }
    }
  }
}
