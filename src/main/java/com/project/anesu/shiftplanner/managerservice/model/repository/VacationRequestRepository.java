package com.project.anesu.shiftplanner.managerservice.model.repository;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

  List<VacationRequest> findByEmployeeId(Long employeeId);

  @Query(
      "SELECT v FROM VacationRequest v WHERE v.employeeId = :employeeId "
          + "AND v.startDate <= :endDate AND v.endDate >= :startDate")
  List<VacationRequest> findByEmployeeIdAndDateRange(
      @Param("employeeId") Long employeeId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  Optional<VacationRequest> findByIdAndStatus(Long vacationRequestId, VacationRequestStatus status);

  @Query(
      "SELECT v FROM VacationRequest v WHERE v.employeeId = :employeeId "
          + "AND (v.startDate <= :endOfYear AND v.endDate >= :startOfYear)")
  List<VacationRequest> findByEmployeeIdAndOverlappingIntoNewYear(
      @Param("employeeId") Long employeeId,
      @Param("startOfYear") LocalDateTime startDate,
      @Param("endOfYear") LocalDateTime endDate);

  @Query(
      "SELECT v FROM VacationRequest v "
          + "WHERE v.officeLocationId = :officeLocationId "
          + "AND v.status IN :status "
          + "AND v.startDate >= :startDate "
          + "AND v.endDate <= :endDate")
  List<VacationRequest> findByOfficeLocationIdAndStatusAndDateRange(
      @Param("officeLocationId") Long officeLocationId,
      @Param("status") List<VacationRequestStatus> status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
