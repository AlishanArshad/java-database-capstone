package com.example.java_database_capstone.service;

import com.example.java_database_capstone.entity.Appointment;
import com.example.java_database_capstone.repository.AppointmentRepository;
import com.example.java_database_capstone.repository.DoctorRepository;
import com.example.java_database_capstone.repository.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    /**
     * Book a new appointment.
     */
    public int bookAppointment(Appointment appointment) {
        try {

            if (appointment == null) {
                return 0;
            }

            appointmentRepository.save(appointment);

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update an existing appointment.
     */
    public ResponseEntity<Map<String, String>> updateAppointment(
            Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        try {

            if (appointment == null || appointment.getId() == null) {

                response.put("message", "Invalid appointment");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            if (!appointmentRepository
                    .findById(appointment.getId())
                    .isPresent()) {

                response.put("message", "Appointment not found");

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            appointmentRepository.save(appointment);

            response.put(
                    "message",
                    "Appointment updated successfully"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Error updating appointment"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /**
     * Cancel an existing appointment.
     */
    public ResponseEntity<Map<String, String>> cancelAppointment(
            long id,
            String token) {

        Map<String, String> response = new HashMap<>();

        try {

            if (token == null || token.isBlank()) {

                response.put(
                        "message",
                        "Authorization token is required"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            var appointmentOptional =
                    appointmentRepository.findById(id);

            if (appointmentOptional.isEmpty()) {

                response.put(
                        "message",
                        "Appointment not found"
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            Appointment appointment =
                    appointmentOptional.get();

            appointmentRepository.delete(appointment);

            response.put(
                    "message",
                    "Appointment cancelled successfully"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Error cancelling appointment"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /**
     * Retrieve appointments for a doctor on a particular date.
     */
    public Map<String, Object> getAppointment(
            String pname,
            LocalDate date,
            String token) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (date == null) {

                response.put(
                        "message",
                        "Appointment date is required"
                );

                return response;
            }

            if (token == null || token.isBlank()) {

                response.put(
                        "message",
                        "Authorization token is required"
                );

                return response;
            }

            /*
             * Extract doctor email/username from token.
             */
            String identifier =
                    tokenService.extractIdentifier(token);

            /*
             * Find the doctor using the identifier.
             * In this project doctors login using email.
             */
            var doctor =
                    doctorRepository.findByEmail(identifier);

            if (doctor == null) {

                response.put(
                        "message",
                        "Doctor not found"
                );

                return response;
            }

            Long doctorId = doctor.getId();

            LocalDateTime start =
                    date.atStartOfDay();

            LocalDateTime end =
                    date.plusDays(1).atStartOfDay();

            List<Appointment> appointments;

            if (pname == null || pname.trim().isEmpty()) {

                appointments =
                        appointmentRepository
                                .findByDoctorIdAndAppointmentTimeBetween(
                                        doctorId,
                                        start,
                                        end
                                );

            } else {

                appointments =
                        appointmentRepository
                                .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                        doctorId,
                                        pname,
                                        start,
                                        end
                                );
            }

            response.put(
                    "appointments",
                    appointments
            );

            return response;

        } catch (Exception e) {

            response.put(
                    "message",
                    "Error retrieving appointments"
            );

            return response;
        }
    }
}