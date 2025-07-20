
# Shift Planner: Manager Service

This microservice is part of an **employee scheduling system** in a health care facility.

The **Manager Microservice** focuses on enabling managers to excecute the following operations:

- View and manage employee schedules  
- Approve or reject shift requests  
- Approve or reject vacation requests  
- Update schedules

It operates **independently** of the Employee Microservice but communicates with it to:

- To ensure proper validation of employee data  
- Maintain consistency across both services

## Tech Stack

- Java (SapMachine 21)  
- Spring Boot  
- H2 Database (in-memory)  
- Maven  
- Lombok  
- JUnit 5 for testing  
- Rest Assured for integration testing  

##  Key Features

- Approve or reject shift requests  
- Approve or reject vacation requests  
- Update employee schedules based on manager actions  
- Get a specific employee’s schedule for a given day  
- Get a list of an employee’s shift requests in a specific date range  
- Delete existing schedules  
- Retrieve team calendar for a specific date range and location  
- Validation layer to enforce business rules (e.g., no overlapping shifts)  

##  Unit tests cover

Shift request validation

Vacation request validation

Schedule conflict detection

Successful and failing approval flows

##  Integration tests cover 

REST Endpoints functionality

##  Validation rules

Shifts and vacations must not overlap

Vacation days must be valid future dates

Weekly working hours must not be exceeded

Annual vacation days must not be exceeded

Only "pending" shift or vacation requests can be approved or rejected

##  REST Endpints

Schedule Request
| Method | Endpoint                                      | Description             |
| --------| -------------------------------------------  | ----------------------- |
| `POST`  | `/schedules`                                 | Create schedule         |
| `PUT`   | `/schedules/{scheduleId}`                    | Update schedule         |
| `GET`   | `/schedules/{scheduleId}`                    | Get schedule by ID      |
| `GET`   | `/schedules/{scheduleId}/range`              | Get schedules in range  |
| `DELETE`| `/schedules/{scheduleId}`                    | Delete schedule         |



Shift Request
| Method | Endpoint                                                    | Description             |
| ------ | ----------------------------------------------------------- | ----------------------- |
| `PUT`  | `/employees/{employeeId}/shifts`                            | Create shift request    |
| `PUT`  | `/employees/{employeeId}/shifts/{shiftRequestId}/approve`   | Approve shift request   |
| `GET`  | `/shifts/{shiftRequestId}/decline`                          | Reject shift request    |
| `GET`  | `/employees/{employeeId}/shifts`                            | Get shift request by specific employeeId |
| `GET`  | `/employees/{employeeId}/shifts/range`                      | Get shift requests in range  |


Vacation Request
| Method | Endpoint                                                    | Description              |
| ------ | ---------------------------------------------------------   | -----------------------  |
| `PUT`  | `/vacations/{vacationRequestId}/approve`                    | Approve vacation request |
| `GET`  | `/vacations/{vacationRequestId}/decline`                    | Reject vacation request  |
| `GET`  | `/employees/{employeeId}/vacations`                         | Get vacation requests by specific employeeId |
| `GET`  | `/employees/{employeeId}/vacations/range`                   | Get vacation requests in range  |
| `GET`  | `/offices/{officeLocationId}/vacations`                     | Get team calendar         |

## ✨ How to run locally:
```bash
Clone project:

git clone https://:github.com/anesukuveya16/Shift-planner-manager-service
cd manager-microservice bash

Build project:

./mvn clean install 

Run the application:

./mvn spring-boot:run
