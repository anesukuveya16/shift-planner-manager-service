package com.project.anesu.shiftplanner.managerservice.unitTests.util;

import com.project.anesu.shiftplanner.managerservice.entity.schedule.Schedule;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftEntry;
import com.project.anesu.shiftplanner.managerservice.entity.shift.ShiftType;
import com.project.anesu.shiftplanner.managerservice.entity.vacation.VacationEntry;
import com.project.anesu.shiftplanner.managerservice.service.exception.InvalidScheduleException;
import com.project.anesu.shiftplanner.managerservice.service.util.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ScheduleValidatorTest {

    private static final LocalDateTime START_DATE =
            LocalDateTime.from(LocalDate.of(2024, 2, 20).atTime(10, 0));
    private static final LocalDateTime END_DATE =
            LocalDateTime.from(LocalDate.of(2024, 2, 21).atTime(18, 0));

    private ScheduleValidator cut;

    @BeforeEach
    void setUp() {
        cut = new ScheduleValidator();
    }

    @Test
    void validateDates_shouldThrowExceptionWhenStartDateIsNull() {
        // Given

        Schedule schedule = providedSchedule(null, END_DATE, null, null);

        // When
        InvalidScheduleException invalidScheduleException =
                assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

        // Then
        assertEquals(invalidScheduleException.getMessage(), "Start date or end date is null");
    }

    @Test
    void validateDates_shouldThrowExceptionWhenEndDateIsNull() {
        // Given

        Schedule schedule = providedSchedule(START_DATE, null, null, null);

        // When
        InvalidScheduleException invalidScheduleException =
                assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

        // Then
        assertEquals(invalidScheduleException.getMessage(), "Start date or end date is null");
    }

    @Test
    void validateDates_shouldThrowExceptionWhenShiftsOrVacationAreNull() {
        // Given
        Schedule schedule = providedSchedule(START_DATE, END_DATE, null, null);

        // When
        InvalidScheduleException invalidScheduleException =
                assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

        // Then
        assertEquals(invalidScheduleException.getMessage(), "Shifts or vacations must be provided.");
    }

    @Test
    void validateDates_shouldThrowException_WhenEndDateAfterStartDate() {
        // Given

        Schedule schedule = new Schedule();
        schedule.setStartDate(LocalDateTime.of(2025, 12, 10, 14, 0));
        schedule.setEndDate(END_DATE);

        // When
        InvalidScheduleException invalidScheduleException =
                assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

        // Then
        assertEquals(invalidScheduleException.getMessage(), "Start date cannot be after end date");
    }

    @Test
    void validateWorkingHours_shouldThrowExceptionWhenWorkingHoursExceed_MaxHoursPerShift() {
        // Given

        ShiftEntry shiftEntry = new ShiftEntry();

        shiftEntry.setWorkingHours(10L);
        shiftEntry.setShiftType(ShiftType.AFTERNOON_SHIFT);

        Schedule schedule = providedSchedule(START_DATE, END_DATE, List.of(shiftEntry), null);

        // When
        InvalidScheduleException invalidScheduleException =
                assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

        // Then
        assertEquals(invalidScheduleException.getMessage(), "Shift exceeds maximum working hours.");
    }

    @Test
    void validateWeeklyWorkingHours_shouldThrowExceptionWhenWorkingHoursHaveBeenExceeded() {
        // Given

        Schedule schedule = providedSchedule(START_DATE, END_DATE, givenMultipleShiftEntries(), null);

        // When
        InvalidScheduleException invalidScheduleException =
                assertThrows(InvalidScheduleException.class, () -> cut.validateSchedule(schedule));

        // Then

        assertEquals(
                invalidScheduleException.getMessage(), "Weekly working hours exceed maximum limit.");
    }

    private Schedule providedSchedule(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<ShiftEntry> shiftEntries,
            List<VacationEntry> vacationEntries) {
        Schedule schedule = new Schedule();
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setShifts(shiftEntries);
        schedule.setVacations(vacationEntries);
        return schedule;
    }

    private List<ShiftEntry> givenMultipleShiftEntries() {
        List<ShiftEntry> shiftEntries = new ArrayList<>();

        LocalDateTime shiftStartDate = LocalDateTime.of(2024, 12, 28, 20, 0);

        for (int i = 0; i < 7; i++) {
            ShiftEntry shiftEntry = new ShiftEntry();
            shiftEntry.setWorkingHours(8L);
            shiftEntry.setShiftDate(shiftStartDate);
            shiftEntry.setShiftType(ShiftType.NIGHT_SHIFT);

            shiftEntries.add(shiftEntry);
        }

        return shiftEntries;
    }

}
