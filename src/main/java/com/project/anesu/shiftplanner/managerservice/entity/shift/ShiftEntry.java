package com.project.anesu.shiftplanner.managerservice.entity.shift;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long shiftId;

  private LocalDateTime shiftDate;
  private ShiftType shiftType;
  private Long workingHours;

  public static ShiftEntry fromApprovedShiftEntry(ShiftRequest approvedShiftRequest) {
    return ShiftEntry.builder()
        .shiftDate(approvedShiftRequest.getShiftDate())
        .shiftType(approvedShiftRequest.getShiftType())
        .workingHours(approvedShiftRequest.getShiftLengthInHours())
        .build();
  }
}
