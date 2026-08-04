# Smart Clinic Management System - Database Schema Design

This document describes the database design for the Smart Clinic Management System. The system uses a hybrid database architecture:

- **MySQL** for structured and relational data.
- **MongoDB** for flexible and unstructured data.

---

# MySQL Database Design

## Overview

MySQL is used to store structured data that requires strong relationships, consistency, and constraints. Core entities include patients, doctors, appointments, administrators, clinic locations, and payments.

---

## Table: patients

| Column | Data Type | Constraints |
|---------|-----------|-------------|
| id | INT | Primary Key, AUTO_INCREMENT |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| gender | VARCHAR(20) | NOT NULL |
| date_of_birth | DATE | NOT NULL |
| phone | VARCHAR(20) | UNIQUE |
| email | VARCHAR(150) | UNIQUE |
| address | TEXT | NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Notes
- Email and phone should be validated in application code.
- Appointment history should remain even if a patient account is deactivated.

---

## Table: doctors

| Column | Data Type | Constraints |
|---------|-----------|-------------|
| id | INT | Primary Key, AUTO_INCREMENT |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| specialization | VARCHAR(100) | NOT NULL |
| phone | VARCHAR(20) | UNIQUE |
| email | VARCHAR(150) | UNIQUE |
| clinic_location_id | INT | Foreign Key → clinic_locations(id) |
| available_from | TIME | NOT NULL |
| available_to | TIME | NOT NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Notes
- Doctors should not have overlapping appointments.
- Availability is managed using available_from and available_to.

---

## Table: appointments

| Column | Data Type | Constraints |
|---------|-----------|-------------|
| id | INT | Primary Key, AUTO_INCREMENT |
| patient_id | INT | Foreign Key → patients(id) |
| doctor_id | INT | Foreign Key → doctors(id) |
| appointment_time | DATETIME | NOT NULL |
| status | INT | Default 0 |
| reason | VARCHAR(255) | NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Status Values

| Value | Meaning |
|-------|---------|
| 0 | Scheduled |
| 1 | Completed |
| 2 | Cancelled |

### Notes

- Each appointment belongs to one patient and one doctor.
- Appointment records should be retained for medical history.
- Business logic should prevent overlapping appointments.

---

## Table: admin

| Column | Data Type | Constraints |
|---------|-----------|-------------|
| id | INT | Primary Key, AUTO_INCREMENT |
| username | VARCHAR(100) | UNIQUE |
| password_hash | VARCHAR(255) | NOT NULL |
| full_name | VARCHAR(150) | NOT NULL |
| email | VARCHAR(150) | UNIQUE |
| role | VARCHAR(50) | DEFAULT 'Administrator' |

---

## Table: clinic_locations

| Column | Data Type | Constraints |
|---------|-----------|-------------|
| id | INT | Primary Key, AUTO_INCREMENT |
| clinic_name | VARCHAR(150) | NOT NULL |
| address | TEXT | NOT NULL |
| city | VARCHAR(100) | NOT NULL |
| phone | VARCHAR(20) | NULL |

### Notes

- One clinic can have multiple doctors.
- Each doctor belongs to one clinic.

---

## Table: payments

| Column | Data Type | Constraints |
|---------|-----------|-------------|
| id | INT | Primary Key, AUTO_INCREMENT |
| appointment_id | INT | Foreign Key → appointments(id) |
| amount | DECIMAL(10,2) | NOT NULL |
| payment_method | VARCHAR(50) | NOT NULL |
| payment_status | VARCHAR(30) | NOT NULL |
| transaction_date | DATETIME | NOT NULL |

### Notes

- Every payment belongs to one appointment.
- Payment history should never be deleted.

---

# Database Relationships

- One Patient → Many Appointments
- One Doctor → Many Appointments
- One Clinic Location → Many Doctors
- One Appointment → One Payment
- One Admin manages the system

---

# MongoDB Collection Design

## Overview

MongoDB stores flexible data that changes frequently or does not require strict relational constraints. This includes doctor notes, prescriptions, feedback, and attachments.

---

## Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc1234567890abcdef12')",
  "appointmentId": 51,
  "patientId": 8,
  "doctorId": 3,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "duration": "5 Days"
    },
    {
      "name": "Vitamin C",
      "dosage": "1000mg",
      "frequency": "Once Daily",
      "duration": "10 Days"
    }
  ],
  "doctorNotes": "Patient should stay hydrated and rest.",
  "attachments": [
    {
      "fileName": "blood-test-report.pdf",
      "fileType": "application/pdf",
      "uploadedAt": "2026-08-05T10:30:00Z"
    }
  ],
  "tags": [
    "fever",
    "viral",
    "follow-up"
  ],
  "pharmacy": {
    "name": "City Pharmacy",
    "location": "Downtown Clinic"
  },
  "createdAt": "2026-08-05T10:30:00Z"
}
```

---

## MongoDB Design Decisions

- Store only **patientId**, **doctorId**, and **appointmentId** instead of duplicating relational data.
- Arrays allow multiple medications within one prescription.
- Embedded documents are used for pharmacy information and attachments.
- Additional fields can be added later without changing existing documents.
- MongoDB is ideal for storing doctor notes, uploaded reports, logs, and other evolving medical records.

---

# Design Considerations

- MySQL ensures data integrity through primary keys and foreign keys.
- MongoDB provides flexibility for semi-structured medical information.
- Email and phone validation will be handled by the application layer.
- Appointment history and payment records should never be permanently deleted.
- Business rules should prevent doctors from having overlapping appointments.
- Prescriptions are linked to appointments to maintain a complete treatment history.

---

# Conclusion

This hybrid database design combines the reliability of MySQL for transactional clinic operations with the flexibility of MongoDB for storing prescriptions and other evolving medical records. It provides a scalable foundation for future development of the Smart Clinic Management System.