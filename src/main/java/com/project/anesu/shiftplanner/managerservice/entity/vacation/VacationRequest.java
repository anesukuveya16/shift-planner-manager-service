package com.project.anesu.shiftplanner.managerservice.entity.vacation;

import com.project.anesu.shiftplanner.managerservice.entity.manager.Manager;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VacationRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long employeeId;

  private Long officeLocationId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  private VacationRequestStatus status;

  private String rejectionReason;

  @ManyToOne
  @JoinColumn(name = "manager_id")
  private Manager manager;
}
