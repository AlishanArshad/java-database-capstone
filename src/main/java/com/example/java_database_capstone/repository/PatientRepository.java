package com.example.java_database_capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.java_database_capstone.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Patient findByEmail(String email);

    Patient findByEmailOrPhone(String email, String phone);
}