package com.example.java_database_capstone.service;

import com.example.java_database_capstone.entity.Prescription;
import com.example.java_database_capstone.repository.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Save a prescription.
     */
    public ResponseEntity<Map<String, String>> savePrescription(
            Prescription prescription) {

        Map<String, String> response = new HashMap<>();

        try {

            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {

            response.put("message",
                    "Error saving prescription");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    /**
     * Get prescriptions associated with an appointment.
     */
    public ResponseEntity<Map<String, Object>> getPrescription(
            Long appointmentId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Prescription> prescriptions =
                    prescriptionRepository
                            .findByAppointmentId(appointmentId);

            response.put("prescription", prescriptions);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("message",
                    "Error retrieving prescription");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
