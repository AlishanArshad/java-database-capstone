package com.example.java_database_capstone.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.java_database_capstone.dto.Login;
import com.example.java_database_capstone.entity.Appointment;
import com.example.java_database_capstone.entity.Doctor;
import com.example.java_database_capstone.repository.AppointmentRepository;
import com.example.java_database_capstone.repository.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {

        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Get available appointment slots for a doctor on a specific date.
     */
    public List<String> getDoctorAvailability(
            Long doctorId,
            LocalDate date) {

        List<String> availableSlots = new ArrayList<>();

        try {

            Optional<Doctor> doctorOptional =
                    doctorRepository.findById(doctorId);

            if (doctorOptional.isEmpty()) {
                return availableSlots;
            }

            Doctor doctor = doctorOptional.get();

            /*
             * Doctor availability is stored as a String
             * in the Doctor entity.
             *
             * Example:
             * "09:00,10:00,11:00,14:00,15:00"
             */
            List<String> doctorSlots =
                    getAvailabilitySlots(doctor.getAvailability());

            LocalDateTime start =
                    date.atStartOfDay();

            LocalDateTime end =
                    date.plusDays(1).atStartOfDay();

            List<Appointment> appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctorId,
                                    start,
                                    end
                            );

            List<String> bookedSlots =
                    new ArrayList<>();

            for (Appointment appointment : appointments) {

                if (appointment.getAppointmentTime() != null) {

                    LocalTime time =
                            appointment
                                    .getAppointmentTime()
                                    .toLocalTime();

                    bookedSlots.add(formatTime(time));
                }
            }

            for (String slot : doctorSlots) {

                if (!bookedSlots.contains(slot)) {
                    availableSlots.add(slot);
                }
            }

        } catch (Exception e) {

            return new ArrayList<>();
        }

        return availableSlots;
    }

    /**
     * Convert the availability String into a List.
     *
     * Supports:
     *
     * "09:00,10:00,11:00"
     *
     * and
     *
     * "[09:00, 10:00, 11:00]"
     */
    private List<String> getAvailabilitySlots(
            String availability) {

        List<String> slots = new ArrayList<>();

        if (availability == null ||
                availability.isBlank()) {

            return slots;
        }

        String cleaned =
                availability
                        .replace("[", "")
                        .replace("]", "");

        String[] values =
                cleaned.split(",");

        for (String value : values) {

            String slot =
                    value.trim()
                            .replace("\"", "");

            if (!slot.isBlank()) {
                slots.add(slot);
            }
        }

        return slots;
    }

    /**
     * Format LocalTime consistently as HH:mm.
     */
    private String formatTime(LocalTime time) {

        return String.format(
                "%02d:%02d",
                time.getHour(),
                time.getMinute()
        );
    }

    /**
     * Save a new doctor.
     *
     * @return 1 = success
     *        -1 = doctor already exists
     *         0 = error
     */
    public int saveDoctor(Doctor doctor) {

        try {

            if (doctor == null) {
                return 0;
            }

            Doctor existingDoctor =
                    doctorRepository.findByEmail(
                            doctor.getEmail());

            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);

            return 1;

        } catch (Exception e) {

            return 0;
        }
    }

    /**
     * Update an existing doctor.
     *
     * @return 1 = success
     *        -1 = doctor not found
     *         0 = error
     */
    public int updateDoctor(Doctor doctor) {

        try {

            if (doctor == null ||
                    doctor.getId() == null) {

                return 0;
            }

            Optional<Doctor> existingDoctor =
                    doctorRepository.findById(
                            doctor.getId());

            if (existingDoctor.isEmpty()) {
                return -1;
            }

            doctorRepository.save(doctor);

            return 1;

        } catch (Exception e) {

            return 0;
        }
    }

    /**
     * Get all doctors.
     */
    public List<Doctor> getDoctors() {

        return doctorRepository.findAll();
    }

    /**
     * Delete a doctor and all appointments.
     */
    public int deleteDoctor(long id) {

        try {

            Optional<Doctor> doctor =
                    doctorRepository.findById(id);

            if (doctor.isEmpty()) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(id);

            doctorRepository.deleteById(id);

            return 1;

        } catch (Exception e) {

            return 0;
        }
    }

    /**
     * Validate doctor login credentials.
     */
    public ResponseEntity<Map<String, String>> validateDoctor(
            Login login) {

        Map<String, String> response =
                new HashMap<>();

        try {

            if (login == null ||
                    login.getIdentifier() == null ||
                    login.getPassword() == null) {

                response.put(
                        "message",
                        "Email and password are required"
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            Doctor doctor =
                    doctorRepository.findByEmail(
                            login.getIdentifier());

            if (doctor == null) {

                response.put(
                        "message",
                        "Invalid email or password"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            if (!doctor.getPassword()
                    .equals(login.getPassword())) {

                response.put(
                        "message",
                        "Invalid email or password"
                );

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            /*
             * TokenService expects a String identifier.
             *
             * Use doctor email instead of doctor ID.
             */
            String token =
                    tokenService.generateToken(
                            doctor.getEmail());

            response.put("token", token);
            response.put(
                    "message",
                    "Login successful"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put(
                    "message",
                    "Internal server error"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /**
     * Find doctors by partial name.
     */
    public Map<String, Object> findDoctorByName(
            String name) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository
                            .findByNameLike(name);

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());

            response.put(
                    "message",
                    "Unable to find doctors");
        }

        return response;
    }

    /**
     * Filter doctors by name, specialty and AM/PM.
     */
    public Map<String, Object>
    filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String amOrPm) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository
                            .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                    name,
                                    specialty
                            );

            doctors =
                    filterDoctorByTime(
                            doctors,
                            amOrPm);

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());
        }

        return response;
    }

    /**
     * Filter doctors by name and AM/PM.
     */
    public Map<String, Object>
    filterDoctorByNameAndTime(
            String name,
            String amOrPm) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository
                            .findByNameLike(name);

            doctors =
                    filterDoctorByTime(
                            doctors,
                            amOrPm);

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());
        }

        return response;
    }

    /**
     * Filter doctors by name and specialty.
     */
    public Map<String, Object>
    filterDoctorByNameAndSpecility(
            String name,
            String specilty) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository
                            .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                    name,
                                    specilty
                            );

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());
        }

        return response;
    }

    /**
     * Filter doctors by specialty and AM/PM.
     */
    public Map<String, Object>
    filterDoctorByTimeAndSpecility(
            String specilty,
            String amOrPm) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository
                            .findBySpecialtyIgnoreCase(
                                    specilty);

            doctors =
                    filterDoctorByTime(
                            doctors,
                            amOrPm);

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());
        }

        return response;
    }

    /**
     * Filter doctors by specialty.
     */
    public Map<String, Object>
    filterDoctorBySpecility(
            String specilty) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository
                            .findBySpecialtyIgnoreCase(
                                    specilty);

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());
        }

        return response;
    }

    /**
     * Filter all doctors by AM/PM.
     */
    public Map<String, Object>
    filterDoctorsByTime(
            String amOrPm) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Doctor> doctors =
                    doctorRepository.findAll();

            doctors =
                    filterDoctorByTime(
                            doctors,
                            amOrPm);

            response.put("doctors", doctors);

        } catch (Exception e) {

            response.put(
                    "doctors",
                    new ArrayList<>());
        }

        return response;
    }

    /**
     * Filter doctors according to availability.
     */
    private List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String amOrPm) {

        List<Doctor> filteredDoctors =
                new ArrayList<>();

        if (doctors == null ||
                amOrPm == null) {

            return filteredDoctors;
        }

        for (Doctor doctor : doctors) {

            List<String> availability =
                    getAvailabilitySlots(
                            doctor.getAvailability());

            for (String slot : availability) {

                try {

                    LocalTime time =
                            LocalTime.parse(slot);

                    boolean isMatch =
                            amOrPm.equalsIgnoreCase("AM")
                                    ? time.getHour() < 12
                                    : time.getHour() >= 12;

                    if (isMatch) {

                        filteredDoctors.add(doctor);

                        break;
                    }

                } catch (Exception ignored) {
                    // Ignore invalid availability values
                }
            }
        }

        return filteredDoctors;
    }
}
