package com.project.anesu.shiftplanner.managerservice.unitTests;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftEntry;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequest;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftRequestStatus;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftType;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationEntry;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequest;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationRequestStatus;
import com.project.anesu.shiftplanner.managerservice.model.repository.ScheduleRepository;
import com.project.anesu.shiftplanner.managerservice.service.ScheduleServiceImpl;
import com.project.anesu.shiftplanner.managerservice.service.exception.ScheduleNotFoundException;
import com.project.anesu.shiftplanner.managerservice.service.util.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepositoryMock;
    @Mock private ScheduleValidator scheduleValidatorMock;

    private ScheduleServiceImpl cut;

    @BeforeEach
    void setUp() {
        cut = new ScheduleServiceImpl(scheduleRepositoryMock, scheduleValidatorMock);
    }

    @Test
    void shouldUpdateAndSaveTheNewlyUpdatedSchedule() {
        // Given
        Schedule oldSchedule =
                scheduleWithDateAndDuration(6L, LocalDate.now().plusDays(2).atTime(9, 0), 8);

        Schedule newSchedule = scheduleWithDateAndDuration(8L, oldSchedule.getStartDate(), 9);

        when(scheduleRepositoryMock.findById(oldSchedule.getId())).thenReturn(Optional.of(oldSchedule));
        doNothing().when(scheduleValidatorMock).validateSchedule(any(Schedule.class));
        when(scheduleRepositoryMock.save(any(Schedule.class))).thenReturn(newSchedule);

        // When
        Schedule newlyUpdatedSchedule = cut.updateEmployeeSchedule(oldSchedule.getId(), newSchedule);

        // Then
        assertNotNull(newlyUpdatedSchedule);
        assertThat(newlyUpdatedSchedule.getEmployeeId()).isEqualTo(oldSchedule.getEmployeeId());
        assertThat(newlyUpdatedSchedule.getTotalWorkingHours())
                .isEqualTo(newSchedule.getTotalWorkingHours());

        verify(scheduleValidatorMock).validateSchedule(oldSchedule);
        verify(scheduleRepositoryMock, times(1)).save(oldSchedule);
    }

    @Test
    void shouldThrowExceptionWhenScheduleToBeUpdatedIsNotFound() {

        // Given
        Long scheduleId = 100L;
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);

        when(scheduleRepositoryMock.findById(scheduleId)).thenReturn(Optional.empty());

        // When
        assertThrows(
                ScheduleNotFoundException.class, () -> cut.updateEmployeeSchedule(scheduleId, schedule));

        // Then
        verify(scheduleRepositoryMock, times(1)).findById(scheduleId);
        verifyNoMoreInteractions(scheduleRepositoryMock);
    }

    @Test
    void shouldRetrieveTheScheduleByGivenScheduleId() {
        // Given
        long scheduleId = 100L;
        Schedule schedule = new Schedule();
        when(scheduleRepositoryMock.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // When
        Optional<Schedule> retrievedSchedule = cut.getScheduleById(scheduleId);

        // Then
        assertNotNull(retrievedSchedule);
        assertTrue(retrievedSchedule.isPresent());
    }

    @Test
    void shouldAddApprovedShiftRequestToSchedule() {
        // Given
        Long employeeId = 1L;
        ShiftRequest approvedShiftRequest = givenShiftRequest();

        when(scheduleRepositoryMock.save(any(Schedule.class)))
                .thenReturn(createNewScheduleForApprovedShiftRequest(employeeId, approvedShiftRequest));

        // When
        Schedule updatedSchedule = cut.addShiftToSchedule(employeeId, approvedShiftRequest);

        // Then
        assertNotNull(updatedSchedule);

        List<ShiftEntry> shifts = updatedSchedule.getShifts();
        assertThat(shifts).hasSize(1);

        ShiftEntry shiftEntry = shifts.getFirst();
        assertThat(shiftEntry.getShiftDate()).isEqualTo(approvedShiftRequest.getShiftDate());
        assertThat(shiftEntry.getWorkingHours())
                .isEqualTo(approvedShiftRequest.getShiftLengthInHours());
        assertThat(shiftEntry.getShiftType()).isEqualTo(approvedShiftRequest.getShiftType());
    }

    @Test
    void updateEmployeeSchedule_shouldNotProceedWithScheduleUpdate_WhenValidationFails() {

        // Given
        Schedule oldSchedule =
                scheduleWithDateAndDuration(6L, LocalDate.now().plusDays(1).atTime(9, 0), 6);

        Schedule updatedSchedule =
                scheduleWithDateAndDuration(8L, LocalDate.now().plusDays(1).atTime(8, 0), 8);

        when(scheduleRepositoryMock.findById(oldSchedule.getId())).thenReturn(Optional.of(oldSchedule));

        doThrow(ScheduleNotFoundException.class)
                .when(scheduleValidatorMock)
                .validateSchedule(oldSchedule);

        // When
        assertThrows(
                ScheduleNotFoundException.class,
                () -> cut.updateEmployeeSchedule(oldSchedule.getId(), updatedSchedule));

        // Then
        verify(scheduleRepositoryMock, times(1)).findById(oldSchedule.getId());
        verify(scheduleValidatorMock).validateSchedule(oldSchedule);
        verifyNoMoreInteractions(scheduleRepositoryMock);
    }

    @Test
    void addOnlyTheApprovedVacationToSchedule() {

        // Given
        Long employeeId = 1L;

        VacationRequest approvedVacationRequest = givenVacationRequest();

        when(scheduleRepositoryMock.save(any(Schedule.class)))
                .thenReturn(createNewScheduleForApprovedVacation(employeeId, approvedVacationRequest));

        // When
        Schedule updatedSchedule =
                cut.addApprovedVacationRequestToSchedule(employeeId, approvedVacationRequest);

        // Then
        List<VacationEntry> approvedVacationsDays = updatedSchedule.getVacations();

        assertNotNull(approvedVacationsDays);

        VacationEntry vacationEntry = approvedVacationsDays.getFirst();
        assertThat(approvedVacationsDays).hasSize(1);
        assertThat(vacationEntry.getStartDate()).isEqualTo(approvedVacationRequest.getStartDate());
        assertThat(vacationEntry.getEndDate()).isEqualTo(approvedVacationRequest.getEndDate());
    }

    @Test
    void shouldThrowScheduleNotFoundException_WhenScheduleIsNotFoundByGivenId() {
        // Given
        long scheduleId = 100L;
        doThrow(ScheduleNotFoundException.class).when(scheduleRepositoryMock).findById(scheduleId);

        // When
        assertThrows(ScheduleNotFoundException.class, () -> cut.getScheduleById(scheduleId));

        // Then
        verify(scheduleRepositoryMock, times(1)).findById(scheduleId);
        verifyNoMoreInteractions(scheduleRepositoryMock);
    }

    @Test
    void deleteSchedule_ShouldThrowExceptionWhenScheduleIsNotFound() {
        // Given
        long employeeId = 1L;
        doThrow(ScheduleNotFoundException.class).when(scheduleRepositoryMock).existsById(employeeId);

        // When
        assertThrows(ScheduleNotFoundException.class, () -> cut.deleteSchedule(employeeId));

        // Then
        verify(scheduleRepositoryMock, times(1)).existsById(employeeId);
        verifyNoMoreInteractions(scheduleRepositoryMock);
    }

    // Helper methods
    private Schedule scheduleWithDateAndDuration(
            long totalWorkingHours, LocalDateTime startDate, int hour) {
        Schedule oldSchedule = new Schedule();
        oldSchedule.setId(100L);
        oldSchedule.setEmployeeId(1L);
        oldSchedule.setTotalWorkingHours(totalWorkingHours);
        oldSchedule.setStartDate(startDate);
        oldSchedule.setEndDate(LocalDate.now().plusDays(3).atTime(hour, 0));
        return oldSchedule;
    }

    private ShiftRequest givenShiftRequest() {
        ShiftRequest approvedShiftRequest = new ShiftRequest();
        approvedShiftRequest.setId(100L);
        approvedShiftRequest.setShiftLengthInHours(6L);
        approvedShiftRequest.setShiftType(ShiftType.NIGHT_SHIFT);
        approvedShiftRequest.setStatus(ShiftRequestStatus.APPROVED);
        approvedShiftRequest.setShiftDate(LocalDateTime.of(2025, 5, 29, 20, 30));
        return approvedShiftRequest;
    }

    private Schedule createNewScheduleForApprovedShiftRequest(
            Long employeeId, ShiftRequest approvedShiftRequest) {

        List<ShiftEntry> shiftEntries = new ArrayList<>();
        shiftEntries.add(ShiftEntry.fromApprovedShiftEntry(approvedShiftRequest));

        return  Schedule
                .builder()
                .employeeId(employeeId)
                .startDate(approvedShiftRequest.getShiftDate())
                .totalWorkingHours(approvedShiftRequest.getShiftLengthInHours())
                .shifts(shiftEntries)
                .build();
    }

    private VacationRequest givenVacationRequest() {
        VacationRequest approvedVacationRequest = new VacationRequest();
        approvedVacationRequest.setId(100L);
        approvedVacationRequest.setStatus(VacationRequestStatus.APPROVED);
        approvedVacationRequest.setStartDate(LocalDateTime.now());
        approvedVacationRequest.setEndDate(LocalDateTime.now().plusDays(10));
        return approvedVacationRequest;
    }

    private Schedule createNewScheduleForApprovedVacation(
            Long employeeId, VacationRequest approvedVacationRequest) {

        List<VacationEntry> vacationEntries = new ArrayList<>();
        vacationEntries.add(VacationEntry.fromApprovedVacationRequest(approvedVacationRequest));

        return Schedule
                .builder()
                .employeeId(employeeId)
                .startDate(approvedVacationRequest.getStartDate())
                .endDate(approvedVacationRequest.getEndDate())
                .vacations(vacationEntries)
                .build();
    }

}
