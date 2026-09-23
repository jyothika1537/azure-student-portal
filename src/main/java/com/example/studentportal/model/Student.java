package com.example.studentportal.model;

/**
 * Lightweight domain model representing a student in the Azure Student Portal.
 */
public record Student(
    String id,
    String name,
    String department,
    String email,
    double gpa,
    String status
) {}
