# Smart Clinic System - Database Schema Design

## Overview

This document describes the database design for the Smart Clinic System. The application uses a hybrid database approach:

- **MySQL** is used to store structured relational data such as patients, doctors, appointments, and administrators.
- **MongoDB** is used to store flexible document-based data such as prescriptions, which may contain varying numbers of medications and additional notes.

This hybrid approach combines the reliability of relational databases with the flexibility of document databases.

## MySQL Database Design

### 1. Patients Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| patient_id | INT | PRIMARY KEY, AUTO_INCREMENT |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| date_of_birth | DATE | NOT NULL |
| gender | VARCHAR(10) | NOT NULL |
| email | VARCHAR(100) | UNIQUE |
| phone | VARCHAR(20) | NOT NULL |
| address | VARCHAR(255) | NOT NULL |

**Purpose:**  
Stores patient information required for appointments, prescriptions, and medical records.

### 2. Doctors Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| doctor_id | INT | PRIMARY KEY, AUTO_INCREMENT |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| specialization | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE |
| phone | VARCHAR(20) | NOT NULL |
| years_of_experience | INT | NOT NULL |

**Purpose:**  
Stores information about doctors, including their specialization and contact details.

### 3. Appointments Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| appointment_id | INT | PRIMARY KEY, AUTO_INCREMENT |
| patient_id | INT | NOT NULL, FOREIGN KEY REFERENCES Patients(patient_id) |
| doctor_id | INT | NOT NULL, FOREIGN KEY REFERENCES Doctors(doctor_id) |
| appointment_date | DATETIME | NOT NULL |
| appointment_status | VARCHAR(20) | NOT NULL |
| reason_for_visit | VARCHAR(255) | NOT NULL |

**Purpose:**  
Stores appointment details and links each appointment to a specific patient and doctor using foreign keys.

Patients (1) ───────< Appointments >─────── (1) Doctors
        patient_id                     doctor_id

### 4. Admin Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| admin_id | INT | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| full_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE |

**Purpose:**  
Stores administrator accounts used to manage the Smart Clinic System.        

## MongoDB Collection Design

### Prescriptions Collection

The `prescriptions` collection stores prescription information in a flexible document format. Each prescription can contain multiple medications, making MongoDB a suitable choice.

#### Sample Document

```json
{
  "_id": "66b123abc456def789",
  "patientId": 1,
  "doctorId": 2,
  "appointmentId": 5,
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500 mg",
      "frequency": "Twice a day",
      "duration": "7 days"
    },
    {
      "name": "Paracetamol",
      "dosage": "500 mg",
      "frequency": "Every 6 hours",
      "duration": "3 days"
    }
  ],
  "notes": "Take medications after meals.",
  "issuedDate": "2026-08-02T10:00:00Z"
}
```
## Design Justification

- **MySQL** is used for structured data such as patients, doctors, appointments, and administrators because these entities have well-defined relationships that are enforced using primary and foreign keys.
- **MongoDB** is used for prescriptions because each prescription may contain a different number of medications and additional notes, making a flexible document structure more suitable.
- This hybrid approach combines the consistency of relational databases with the flexibility of document databases, providing an efficient solution for the Smart Clinic System.
