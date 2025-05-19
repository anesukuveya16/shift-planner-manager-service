package com.project.anesu.shiftplanner.managerservice.model.repository;

import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRequestService extends JpaRepository<VacationRequest, Long> {
    
}
