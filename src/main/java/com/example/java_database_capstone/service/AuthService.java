package com.example.java_database_capstone.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.java_database_capstone.dto.Login;
import com.example.java_database_capstone.entity.Admin;
import com.example.java_database_capstone.entity.Appointment;
import com.example.java_database_capstone.entity.Doctor;
import com.example.java_database_capstone.entity.Patient;
import com.example.java_database_capstone.repository.AdminRepository;
import com.example.java_database_capstone.repository.DoctorRepository;
import com.example.java_database_capstone.repository.PatientRepository;

@Service
public class AuthService {

        private final TokenService tokenService;
        private final AdminRepository adminRepository;
        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;
        private final DoctorService doctorService;
        private final PatientService patientService;

        public AuthService(
                        TokenService tokenService,
                        AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        DoctorService doctorService,
                        PatientService patientService) {

                this.tokenService = tokenService;
                this.adminRepository = adminRepository;
                this.doctorRepository = doctorRepository;
                this.patientRepository = patientRepository;
                this.doctorService = doctorService;
                this.patientService = patientService;
        }

        /**
         * Validate a JWT token for a specific user.
         */
        public ResponseEntity<Map<String, String>> validateToken(
                        String token,
                        String user) {

                Map<String, String> response = new HashMap<>();

                try {

                        if (token == null || token.isBlank()) {
                                response.put("message", "Unauthorized");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        boolean valid = tokenService.validateToken(token, user);

                        if (!valid) {
                                response.put(
                                                "message",
                                                "Token is invalid or expired");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        response.put("message", "Token is valid");

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put(
                                        "message",
                                        "Token validation failed");

                        return ResponseEntity
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .body(response);
                }
        }

        /**
         * Validate admin login credentials.
         */
        public ResponseEntity<Map<String, String>> validateAdmin(
                        Admin receivedAdmin) {

                Map<String, String> response = new HashMap<>();

                try {

                        if (receivedAdmin == null
                                        || receivedAdmin.getUsername() == null
                                        || receivedAdmin.getPassword() == null) {

                                response.put(
                                                "message",
                                                "Username and password are required");

                                return ResponseEntity
                                                .badRequest()
                                                .body(response);
                        }

                        Admin admin = adminRepository.findByUsername(
                                        receivedAdmin.getUsername());

                        if (admin == null) {

                                response.put(
                                                "message",
                                                "Invalid username or password");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        if (!admin.getPassword().equals(
                                        receivedAdmin.getPassword())) {

                                response.put(
                                                "message",
                                                "Invalid username or password");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        String token = tokenService.generateToken(
                                        admin.getUsername());

                        response.put("token", token);
                        response.put("message", "Login successful");

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put(
                                        "message",
                                        "Internal server error");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Filter doctors by name, specialty and time.
         */
        public Map<String, Object> filterDoctor(
                        String name,
                        String specialty,
                        String time) {

                if (name != null && !name.isBlank()
                                && specialty != null && !specialty.isBlank()
                                && time != null && !time.isBlank()) {

                        return doctorService
                                        .filterDoctorsByNameSpecilityandTime(
                                                        name,
                                                        specialty,
                                                        time);
                }

                if (name != null && !name.isBlank()
                                && specialty != null && !specialty.isBlank()) {

                        return doctorService
                                        .filterDoctorByNameAndSpecility(
                                                        name,
                                                        specialty);
                }

                if (name != null && !name.isBlank()
                                && time != null && !time.isBlank()) {

                        return doctorService
                                        .filterDoctorByNameAndTime(
                                                        name,
                                                        time);
                }

                if (specialty != null && !specialty.isBlank()
                                && time != null && !time.isBlank()) {

                        return doctorService
                                        .filterDoctorByTimeAndSpecility(
                                                        specialty,
                                                        time);
                }

                if (name != null && !name.isBlank()) {

                        return doctorService.findDoctorByName(name);
                }

                if (specialty != null && !specialty.isBlank()) {

                        return doctorService
                                        .filterDoctorBySpecility(specialty);
                }

                if (time != null && !time.isBlank()) {

                        return doctorService
                                        .filterDoctorsByTime(time);
                }

                Map<String, Object> response = new HashMap<>();

                response.put(
                                "doctors",
                                doctorService.getDoctors());

                return response;
        }

        /**
         * Validate whether an appointment can be booked.
         *
         * @return 1 = available
         *         0 = unavailable
         *         -1 = doctor doesn't exist
         */
        public int validateAppointment(
                        Appointment appointment) {

                try {

                        if (appointment == null
                                        || appointment.getDoctor() == null
                                        || appointment.getAppointmentTime() == null) {

                                return 0;
                        }

                        Long doctorId = appointment
                                        .getDoctor()
                                        .getId();

                        Optional<Doctor> doctor = doctorRepository.findById(doctorId);

                        if (doctor.isEmpty()) {
                                return -1;
                        }

                        LocalDate date = appointment
                                        .getAppointmentTime()
                                        .toLocalDate();

                        LocalTime time = appointment
                                        .getAppointmentTime()
                                        .toLocalTime();

                        List<String> availableSlots = doctorService.getDoctorAvailability(
                                        doctorId,
                                        date);

                        String requestedTime = time.toString();

                        if (availableSlots.contains(requestedTime)) {
                                return 1;
                        }

                        return 0;

                } catch (Exception e) {
                        return 0;
                }
        }

        /**
         * Check whether a patient already exists.
         *
         * @return true if patient does not exist
         *         false if patient already exists
         */
        public boolean validatePatient(
                        Patient patient) {

                try {

                        if (patient == null) {
                                return false;
                        }

                        Patient existingPatient = patientRepository.findByEmailOrPhone(
                                        patient.getEmail(),
                                        patient.getPhone());

                        return existingPatient == null;

                } catch (Exception e) {
                        return false;
                }
        }

        /**
         * Validate patient login credentials.
         */
        public ResponseEntity<Map<String, String>> validatePatientLogin(
                        Login login) {

                Map<String, String> response = new HashMap<>();

                try {

                        if (login == null
                                        || login.getIdentifier() == null
                                        || login.getPassword() == null) {

                                response.put(
                                                "message",
                                                "Email and password are required");

                                return ResponseEntity
                                                .badRequest()
                                                .body(response);
                        }

                        Patient patient = patientRepository.findByEmail(
                                        login.getIdentifier());

                        if (patient == null) {

                                response.put(
                                                "message",
                                                "Invalid email or password");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        if (!patient.getPassword().equals(
                                        login.getPassword())) {

                                response.put(
                                                "message",
                                                "Invalid email or password");

                                return ResponseEntity
                                                .status(HttpStatus.UNAUTHORIZED)
                                                .body(response);
                        }

                        String token = tokenService.generateToken(
                                        patient.getEmail());

                        response.put("token", token);
                        response.put("message", "Login successful");

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        response.put(
                                        "message",
                                        "Internal server error");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

    /**
     * Filter patient appointments.
     */
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token) {

        try {

            /*
             * The JWT subject contains the patient's email.
             */
            String email =
                    tokenService.extractIdentifier(token);

            Patient patient =
                    patientRepository.findByEmail(email);

            if (patient == null) {

                Map<String, Object> response =
                        new HashMap<>();

                response.put(
                        "message",
                        "Patient not found"
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            Long patientId = patient.getId();

            /*
             * Condition + doctor name
             */
            if (condition != null
                    && !condition.isBlank()
                    && name != null
                    && !name.isBlank()) {

                return patientService
                        .filterByDoctorAndCondition(
                                condition,
                                name,
                                patientId
                        );
            }

            /*
             * Condition only
             */
            if (condition != null
                    && !condition.isBlank()) {

                return patientService
                        .filterByCondition(
                                condition,
                                patientId
                        );
            }

            /*
             * Doctor name only
             */
            if (name != null
                    && !name.isBlank()) {

                return patientService
                        .filterByDoctor(
                                name,
                                patientId
                        );
            }

            /*
             * No filters
             */
            return patientService
                    .getPatientAppointment(
                            patientId,
                            token
                    );

        } catch (Exception e) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Unable to filter patient appointments"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
