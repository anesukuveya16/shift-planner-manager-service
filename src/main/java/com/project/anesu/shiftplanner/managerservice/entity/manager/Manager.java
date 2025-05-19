package com.project.anesu.shiftplanner.managerservice.entity.manager;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;
  private String lastName;
  private String email;
  private String phone;

  @Column(updatable = false, nullable = false)
  private LocalDate birthDate;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<VacationRequest> vacationRequests;
}
