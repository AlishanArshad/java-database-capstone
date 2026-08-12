
package com.example.java_database_capstone.service;

import com.example.java_database_capstone.dto.AppointmentDTO;
import com.example.java_database_capstone.entity.Appointment;
import com.example.java_database_capstone.entity.Patient;
import com.example.java_database_capstone.repository.AppointmentRepository;
import com.example.java_database_capstone.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

        private final PatientRepository patientRepository;
        private final AppointmentRepository appointmentRepository;
        private final TokenService tokenService;

        public PatientService(
                        PatientRepository patientRepository,
                        AppointmentRepository appointmentRepository,
                        TokenService tokenService) {

                this.patientRepository = patientRepository;
                this.appointmentRepository = appointmentRepository;
                this.tokenService = tokenService;
        }

        /**
         * Create a new patient.
         *
         * @return 1 on success, 0 on failure
         */
        public int createPatient(Patient patient) {

                try {
                        patientRepository.save(patient);
                        return 1;

                } catch (Exception e) {
                        return 0;
                }
        }

        /**
         * Get appointments belonging to a specific patient.
         */
        public ResponseEntity<Map<String, Object>> getPatientAppointment(
                        Long id,
                        String token) {

                Map<String, Object> response = new HashMap<>();

                try {

                        if (token == null || token.isBlank()) {
                                response.put("message", "Unauthorized");
                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        String email = tokenService.extractEmail(token);

                        Patient patient = patientRepository.findByEmail(email);

                        if (patient == null) {
                                response.put("message", "Patient not found");
                                return ResponseEntity
                                                .status(HttpStatus.NOT_FOUND)
                                                .body(response);
                        }

                        if (!patient.getId().equals(id)) {
                                response.put("message", "Unauthorized");
                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

                        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

                        for (Appointment appointment : appointments) {

                                AppointmentDTO dto = new AppointmentDTO(
                                                appointment.getId(),
                                                appointment.getDoctor().getId(),
                                                appointment.getDoctor().getName(),
                                                appointment.getPatient().getId(),
                                                appointment.getPatient().getName(),
                                                appointment.getPatient().getEmail(),
                                                appointment.getPatient().getPhone(),
                                                appointment.getPatient().getAddress(),
                                                appointment.getAppointmentTime(),
                                                appointment.getStatus());

                                appointmentDTOs.add(dto);
                        }

                        response.put("appointments", appointmentDTOs);

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put("message", "Error retrieving appointments");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Filter patient appointments by past/future condition.
         *
         * past = status 1
         * future = status 0
         */
        public ResponseEntity<Map<String, Object>> filterByCondition(
                        String condition,
                        Long id) {

                Map<String, Object> response = new HashMap<>();

                try {

                        List<Appointment> appointments;

                        if ("past".equalsIgnoreCase(condition)) {

                                appointments = appointmentRepository
                                                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                                                id,
                                                                1);

                        } else if ("future".equalsIgnoreCase(condition)) {

                                appointments = appointmentRepository
                                                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                                                id,
                                                                0);

                        } else {

                                response.put("message",
                                                "Condition must be past or future");

                                return ResponseEntity
                                                .badRequest()
                                                .body(response);
                        }

                        response.put("appointments",
                                        convertToDTO(appointments));

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put("message",
                                        "Error filtering appointments");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Filter appointments by doctor's name.
         */
        public ResponseEntity<Map<String, Object>> filterByDoctor(
                        String name,
                        Long patientId) {

                Map<String, Object> response = new HashMap<>();

                try {

                        List<Appointment> appointments = appointmentRepository
                                        .filterByDoctorNameAndPatientId(
                                                        name,
                                                        patientId);

                        response.put("appointments",
                                        convertToDTO(appointments));

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put("message",
                                        "Error filtering appointments");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Filter appointments by doctor and past/future condition.
         */
        public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
                        String condition,
                        String name,
                        long patientId) {

                Map<String, Object> response = new HashMap<>();

                try {

                        int status;

                        if ("past".equalsIgnoreCase(condition)) {

                                status = 1;

                        } else if ("future".equalsIgnoreCase(condition)) {

                                status = 0;

                        } else {

                                response.put("message",
                                                "Condition must be past or future");

                                return ResponseEntity
                                                .badRequest()
                                                .body(response);
                        }

                        List<Appointment> appointments = appointmentRepository
                                        .filterByDoctorNameAndPatientIdAndStatus(
                                                        name,
                                                        patientId,
                                                        status);

                        response.put("appointments",
                                        convertToDTO(appointments));

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put("message",
                                        "Error filtering appointments");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Get patient details using the JWT token.
         */
        public ResponseEntity<Map<String, Object>> getPatientDetails(
                        String token) {

                Map<String, Object> response = new HashMap<>();

                try {

                        if (token == null || token.isBlank()) {

                                response.put("message", "Unauthorized");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        String email = tokenService.extractEmail(token);

                        Patient patient = patientRepository.findByEmail(email);

                        if (patient == null) {

                                response.put("message",
                                                "Patient not found");

                                return ResponseEntity
                                                .status(HttpStatus.NOT_FOUND)
                                                .body(response);
                        }

                        response.put("patient", patient);

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put("message",
                                        "Error retrieving patient details");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Convert Appointment entities into AppointmentDTO objects.
         */
        private List<AppointmentDTO> convertToDTO(
                        List<Appointment> appointments) {

                List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

                for (Appointment appointment : appointments) {

                        AppointmentDTO dto = new AppointmentDTO(
                                        appointment.getId(),
                                        appointment.getDoctor().getId(),
                                        appointment.getDoctor().getName(),
                                        appointment.getPatient().getId(),
                                        appointment.getPatient().getName(),
                                        appointment.getPatient().getEmail(),
                                        appointment.getPatient().getPhone(),
                                        appointment.getPatient().getAddress(),
                                        appointment.getAppointmentTime(),
                                        appointment.getStatus());

                        appointmentDTOs.add(dto);
                }

                return appointmentDTOs;
        }
}
