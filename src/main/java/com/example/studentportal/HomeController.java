package com.example.studentportal;

import com.example.studentportal.model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Value("${app.version:2.0.0}")
    private String appVersion;

    @Value("${app.slot-name:Production}")
    private String slotName;

    @Value("${app.environment:Azure Cloud}")
    private String environment;

    private static final List<Student> SAMPLE_STUDENTS = List.of(
        new Student("STU-1001", "Jyothika Vucha", "Cloud Architecture & DevOps", "jyothika@portal.azure.edu", 3.94, "Active"),
        new Student("STU-1002", "Alex Mercer", "Distributed Systems & Kubernetes", "alex.m@portal.azure.edu", 3.82, "Active"),
        new Student("STU-1003", "Priya Sharma", "Artificial Intelligence & ML", "priya.s@portal.azure.edu", 3.90, "Active"),
        new Student("STU-1004", "Marcus Vance", "Cybersecurity & Cloud Security", "marcus.v@portal.azure.edu", 3.75, "Active"),
        new Student("STU-1005", "Elena Rostova", "Data Engineering & Analytics", "elena.r@portal.azure.edu", 3.88, "Active")
    );

    /**
     * Default landing page.
     * Evaluates active version (or query parameter ?v=1 or ?v=2) to seamlessly
     * demonstrate Version 1 vs Version 2 during Azure Deployment Slot Swaps.
     */
    @GetMapping("/")
    public String home(@RequestParam(name = "v", required = false) String requestedVersion, Model model) {
        if ("1".equals(requestedVersion) || (requestedVersion == null && appVersion.startsWith("1"))) {
            populateVersion1Model(model);
            return "home-v1";
        }

        populateVersion2Model(model);
        return "home";
    }

    /**
     * Dedicated Version 1 endpoint - preserves original baseline portal demonstration.
     */
    @GetMapping("/v1")
    public String version1(Model model) {
        populateVersion1Model(model);
        return "home-v1";
    }

    /**
     * Dedicated Version 2 endpoint - showcases enhanced modern portal release.
     */
    @GetMapping("/v2")
    public String version2(Model model) {
        populateVersion2Model(model);
        return "home";
    }

    /**
     * Lightweight JSON diagnostic endpoint for automated slot and swap health testing.
     */
    @GetMapping(value = "/api/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "version", appVersion,
            "slot", slotName,
            "environment", environment,
            "timestamp", Instant.now().toString(),
            "actuatorHealthUrl", "/actuator/health",
            "swapValidation", "READY"
        ));
    }

    private void populateVersion1Model(Model model) {
        model.addAttribute("version", "VERSION 1");
        model.addAttribute("slotName", slotName);
        model.addAttribute("environment", environment);
    }

    private void populateVersion2Model(Model model) {
        model.addAttribute("version", "VERSION 2");
        model.addAttribute("rawVersion", appVersion);
        model.addAttribute("slotName", slotName);
        model.addAttribute("environment", environment);
        model.addAttribute("students", SAMPLE_STUDENTS);
        model.addAttribute("totalStudents", 1420);
        model.addAttribute("totalCourses", 36);
        model.addAttribute("averageGpa", "3.78");
        model.addAttribute("healthStatus", "UP");
        model.addAttribute("timestamp", Instant.now().toString());
    }
}