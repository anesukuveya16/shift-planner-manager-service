package com.project.anesu.shiftplanner.managerservice.controller;

public class ManagerServiceRestEndpoints {

  public static final String LANDING_PAGE = "/api/manager";

  public static final String CREATE_SCHEDULE = "/schedules";
  public static final String UPDATE_SCHEDULE = "/schedules/{scheduleId}";
  public static final String GET_SCHEDULE_BY_ID = "/schedules/{scheduleId}";
  public static final String GET_SCHEDULES_IN_RANGE = "/schedules/{scheduleId}/range";
  public static final String DELETE_SCHEDULE = "/schedules/{scheduleId}";

  public static final String CREATE_SHIFT_REQUEST = "/employees/{employeeId}/shifts";
  public static final String APPROVE_SHIFT_REQUEST =
      "/employees/{employeeId}/shifts/{shiftRequestId}/approve";
  public static final String DECLINE_SHIFT_REQUEST = "/shifts/{shiftRequestId}/decline";
  public static final String GET_SHIFT_REQUEST_BY_EMPLOYEE_ID = "/employees/{employeeId}/shifts";
  public static final String GET_SHIFT_REQUESTS_IN_RANGE = "/shifts/range";

  public static final String APPROVE_VACATION_REQUEST = "/vacations/{vacationRequestId}/approve";
  public static final String DECLINE_VACATION_REQUEST = "/vacations/{vacationRequestId}/decline";
  public static final String GET_VACATIONS_BY_EMPLOYEE_ID = "/employees/{employeeId}/vacations";
  public static final String GET_EMPLOYEE_VACATIONS_IN_RANGE =
      "/employees/{employeeId}/vacations/range";
  public static final String GET_TEAM_CALENDAR = "/offices/{officeLocationId}/vacations";

  private ManagerServiceRestEndpoints() {}
}
