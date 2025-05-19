package com.project.anesu.shiftplanner.managerservice.entity.vacation;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacationEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vacation_request_id", nullable = false)
  private VacationRequest vacationRequest;

  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Long vacationDuration;

  public static VacationEntry fromApprovedVacationRequest(VacationRequest vacationRequest) {
    return VacationEntry.builder()
        .startDate(vacationRequest.getStartDate())
        .endDate(vacationRequest.getEndDate())
        .vacationDuration(
            ChronoUnit.DAYS.between(vacationRequest.getStartDate(), vacationRequest.getEndDate()))
        .build();
  }
}
