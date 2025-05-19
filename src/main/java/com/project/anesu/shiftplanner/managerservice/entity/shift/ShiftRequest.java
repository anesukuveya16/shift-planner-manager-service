package com.project.anesu.shiftplanner.managerservice.entity.shift;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShiftRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long employeeId;
  private LocalDateTime shiftDate;

  @Enumerated(EnumType.STRING)
  private ShiftRequestStatus status;

  private String rejectionReason;

  private Long shiftLengthInHours;

  @Enumerated(EnumType.STRING)
  private ShiftType shiftType;
}
