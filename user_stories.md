# Hospital Appointment Management System - User Stories

This document contains the user stories for the Hospital Appointment Management System. Each story follows the Agile user story format and includes acceptance criteria, priority, story points, and implementation notes.

---

# Admin User Stories

## US-001: Manage Doctor Accounts

**Title:**  
_As an admin, I want to create, update, and delete doctor accounts, so that I can manage the list of doctors in the system._

### Acceptance Criteria
1. The admin can create a new doctor account.
2. The admin can update existing doctor information.
3. The admin can delete or deactivate doctor accounts.

**Priority:** High

**Story Points:** 5

**Notes:**
- Only administrators can perform these actions.
- Deleted accounts should not remove appointment history.

---

## US-002: Manage Patient Accounts

**Title:**  
_As an admin, I want to manage patient accounts, so that I can maintain accurate user records._

### Acceptance Criteria
1. The admin can view all registered patients.
2. The admin can update patient information.
3. The admin can deactivate patient accounts.

**Priority:** High

**Story Points:** 3

**Notes:**
- Appointment history should remain available.

---

## US-003: Manage User Roles

**Title:**  
_As an admin, I want to assign and manage user roles, so that users have appropriate permissions._

### Acceptance Criteria
1. The admin can assign user roles.
2. The admin can modify user permissions.
3. Unauthorized users cannot access restricted features.

**Priority:** High

**Story Points:** 5

**Notes:**
- Changes should take effect immediately.

---

## US-004: View All Appointments

**Title:**  
_As an admin, I want to view all appointments, so that I can monitor hospital operations._

### Acceptance Criteria
1. The admin can view every appointment.
2. The admin can search appointments by doctor or patient.
3. Appointment details are displayed correctly.

**Priority:** Medium

**Story Points:** 3

**Notes:**
- Support filtering by date.

---

## US-005: Manage Departments

**Title:**  
_As an admin, I want to manage hospital departments, so that doctors are organized by specialty._

### Acceptance Criteria
1. The admin can create departments.
2. The admin can edit departments.
3. The admin can remove unused departments.

**Priority:** Medium

**Story Points:** 3

**Notes:**
- Departments assigned to doctors cannot be deleted.

---

## US-006: Reset User Passwords

**Title:**  
_As an admin, I want to reset user passwords, so that users can regain access to their accounts._

### Acceptance Criteria
1. The admin can reset passwords.
2. Users are prompted to change temporary passwords.
3. Password reset actions are recorded.

**Priority:** High

**Story Points:** 2

**Notes:**
- Temporary passwords should expire.

---

## US-007: Generate Reports

**Title:**  
_As an admin, I want to generate reports, so that I can analyze hospital performance._

### Acceptance Criteria
1. Reports include appointment statistics.
2. Reports can be filtered by date.
3. Reports can be downloaded.

**Priority:** Medium

**Story Points:** 5

**Notes:**
- Export formats may include PDF or CSV.

---

## US-008: Manage System Settings

**Title:**  
_As an admin, I want to configure system settings, so that the application follows hospital policies._

### Acceptance Criteria
1. The admin can update system settings.
2. Changes are saved successfully.
3. Only administrators can modify settings.

**Priority:** Medium

**Story Points:** 3

**Notes:**
- Validate settings before saving.

---

# Patient User Stories

## US-009: Register an Account

**Title:**  
_As a patient, I want to register an account, so that I can use the appointment system._

### Acceptance Criteria
1. Patients can complete registration.
2. Required fields are validated.
3. Duplicate accounts are prevented.

**Priority:** High

**Story Points:** 3

---

## US-010: Log In

**Title:**  
_As a patient, I want to securely log in, so that I can manage my appointments._

### Acceptance Criteria
1. Valid credentials allow login.
2. Invalid credentials display an error.
3. User sessions are securely managed.

**Priority:** High

**Story Points:** 2

---

## US-011: Update Profile

**Title:**  
_As a patient, I want to update my profile, so that my personal information remains current._

### Acceptance Criteria
1. Patients can edit profile information.
2. Changes are saved successfully.
3. Required fields are validated.

**Priority:** Medium

**Story Points:** 2

---

## US-012: Search for Doctors

**Title:**  
_As a patient, I want to search for doctors by specialty, so that I can find the appropriate healthcare provider._

### Acceptance Criteria
1. Search by doctor name.
2. Search by specialty.
3. Matching doctors are displayed.

**Priority:** High

**Story Points:** 3

---

## US-013: View Doctor Availability

**Title:**  
_As a patient, I want to view doctor availability, so that I can select a suitable appointment time._

### Acceptance Criteria
1. Available appointment slots are displayed.
2. Booked slots cannot be selected.
3. Availability updates automatically.

**Priority:** High

**Story Points:** 3

---

## US-014: Book an Appointment

**Title:**  
_As a patient, I want to book an appointment, so that I can schedule a consultation._

### Acceptance Criteria
1. Patients can choose an available slot.
2. The booking is confirmed.
3. The appointment appears in the patient's schedule.

**Priority:** High

**Story Points:** 5

---

## US-015: Reschedule an Appointment

**Title:**  
_As a patient, I want to reschedule an appointment, so that I can choose another available time._

### Acceptance Criteria
1. Patients can select a new time.
2. The previous booking is updated.
3. The doctor receives the updated schedule.

**Priority:** Medium

**Story Points:** 3

---

## US-016: Cancel an Appointment

**Title:**  
_As a patient, I want to cancel an appointment, so that I can free the reserved time slot._

### Acceptance Criteria
1. Future appointments can be cancelled.
2. Appointment status changes to cancelled.
3. The time slot becomes available again.

**Priority:** Medium

**Story Points:** 2

---

## US-017: View Appointment History

**Title:**  
_As a patient, I want to view my appointment history, so that I can review previous consultations._

### Acceptance Criteria
1. Past appointments are listed.
2. Appointment details are available.
3. History is displayed in chronological order.

**Priority:** Medium

**Story Points:** 2

---

## US-018: Receive Appointment Notifications

**Title:**  
_As a patient, I want to receive appointment reminders, so that I do not miss scheduled visits._

### Acceptance Criteria
1. Reminder notifications are sent.
2. Appointment details are included.
3. Notifications are sent before the appointment.

**Priority:** Medium

**Story Points:** 3

---

# Doctor User Stories

## US-019: Doctor Login

**Title:**  
_As a doctor, I want to securely log in, so that I can manage my appointments._

### Acceptance Criteria
1. Doctors can log in successfully.
2. Invalid credentials are rejected.
3. Secure sessions are maintained.

**Priority:** High

**Story Points:** 2

---

## US-020: Manage Availability

**Title:**  
_As a doctor, I want to manage my availability, so that patients can book appointments during my working hours._

### Acceptance Criteria
1. Doctors can add available time slots.
2. Doctors can modify schedules.
3. Patients only see available slots.

**Priority:** High

**Story Points:** 5

---

## US-021: View Scheduled Appointments

**Title:**  
_As a doctor, I want to view my scheduled appointments, so that I can prepare for consultations._

### Acceptance Criteria
1. Doctors can view daily appointments.
2. Appointment details are displayed.
3. Appointments are sorted by time.

**Priority:** High

**Story Points:** 3

---

## US-022: View Patient Information

**Title:**  
_As a doctor, I want to access patient information, so that I can provide informed medical care._

### Acceptance Criteria
1. Doctors can view patient profiles.
2. Appointment history is available.
3. Information is displayed securely.

**Priority:** High

**Story Points:** 3

---

## US-023: Accept or Decline Appointments

**Title:**  
_As a doctor, I want to accept or decline appointment requests, so that I can manage my workload._

### Acceptance Criteria
1. Doctors can accept requests.
2. Doctors can decline requests.
3. Patients are notified of the decision.

**Priority:** Medium

**Story Points:** 3

---

## US-024: Update Appointment Status

**Title:**  
_As a doctor, I want to update appointment status, so that records remain accurate._

### Acceptance Criteria
1. Doctors can mark appointments as completed.
2. Doctors can mark appointments as cancelled.
3. Updated status is visible to patients.

**Priority:** Medium

**Story Points:** 2

---

## US-025: View Appointment History

**Title:**  
_As a doctor, I want to review previous appointments, so that I can reference past consultations._

### Acceptance Criteria
1. Past appointments are available.
2. Records are displayed chronologically.
3. History is read-only.

**Priority:** Medium

**Story Points:** 2

---

## US-026: Update Professional Profile

**Title:**  
_As a doctor, I want to update my professional profile, so that patients can view accurate information._

### Acceptance Criteria
1. Doctors can edit profile details.
2. Changes are saved successfully.
3. Updated information is visible to patients.

**Priority:** Low

**Story Points:** 2

**Notes:**
- Some profile updates may require administrator approval.
