package com.project.anesu.shiftplanner.managerservice.model.repository;

import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> {

  Optional<ShiftRequest> findByEmployeeIdAndShiftDateAndStatus(
      Long employeeId, LocalDateTime shiftDate, ShiftRequestStatus status);

  List<ShiftRequest> findByEmployeeId(Long employeeId);

  List<ShiftRequest> findByShiftDateBetween(LocalDateTime startDate, LocalDateTime endDate);

  Optional<ShiftRequest> findByIdAndStatus(Long shiftRequestId, ShiftRequestStatus status);
}
