package com.project.anesu.shiftplanner.managerservice.entity.schedule;

import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftEntry;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationEntry;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long employeeId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Long totalWorkingHours;

  private String rejectionReason;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<ShiftEntry> shifts;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "schedule_id")
  private List<VacationEntry> vacations;

  public List<LocalDateTime> getShiftsInRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
    return shifts.stream()
        .filter(
            shift ->
                !shift.getShiftDate().isBefore(rangeStart)
                    && !shift.getShiftDate().isAfter(rangeEnd))
        .map(ShiftEntry::getShiftDate)
        .toList();
  }

  public List<LocalDateTime> getVacationsInRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
    return vacations.stream()
        .filter(
            vacation ->
                vacation.getStartDate().isBefore(rangeEnd.plusDays(1))
                    && vacation.getEndDate().isAfter(rangeStart.minusDays(1)))
        .flatMap(
            vacation -> {
              LocalDateTime current = vacation.getStartDate();
              LocalDateTime end = vacation.getEndDate().plusDays(1);
              return Stream.iterate(current, date -> date.plusDays(1))
                  .limit(ChronoUnit.DAYS.between(current, end))
                  .filter(date -> !date.isBefore(rangeStart) && !date.isAfter(rangeEnd));
            })
        .collect(Collectors.toList());
  }
}
