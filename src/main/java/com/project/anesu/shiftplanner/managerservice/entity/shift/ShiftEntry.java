package com.project.anesu.shiftplanner.managerservice.entity.shift;

import jakarta.persistence.*;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shift_request_id", nullable = false)
  private ShiftRequest shiftRequest;

  private LocalDateTime shiftDate;
  private ShiftType shiftType;
  private Long workingHours;

  public static ShiftEntry fromApprovedShiftEntry(ShiftRequest approvedShiftRequest) {
    return ShiftEntry.builder()
        .shiftDate(approvedShiftRequest.getShiftDate())
        .shiftType(approvedShiftRequest.getShiftType())
        .workingHours(approvedShiftRequest.getShiftLengthInHours())
        .shiftRequest(approvedShiftRequest)
        .build();
  }
}
