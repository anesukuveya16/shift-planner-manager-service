package com.project.anesu.shiftplanner.managerservice.model.repository;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  @Query(
      "SELECT s FROM Schedule s WHERE s.employeeId = :employeeId AND s.startDate BETWEEN :start AND :end")
  Optional<List<Schedule>> findByEmployeeIdAndDateRange(
      @Param("employeeId") Long employeeId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  @Query(
      "SELECT s FROM Schedule s WHERE s.employeeId = :employeeId AND s.startDate BETWEEN :startOfWeek AND :endOfWeek")
  Optional<Schedule> findByEmployeeIdAndWeekRange(
      @Param("employeeId") Long employeeId,
      @Param("startOfWeek") LocalDateTime startOfWeek,
      @Param("endOfWeek") LocalDateTime endOfWeek);
}
