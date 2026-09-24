package com.example.studentportal.model;

/**
 * Retained domain model from the original portal; the current release dashboard does not use it.
 */
public record Student(
    String id,
    String name,
    String department,
    String email,
    double gpa,
    String status
) {}
