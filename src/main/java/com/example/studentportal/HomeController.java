package com.example.studentportal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.Map;

@Controller
public class HomeController {

    @Value("${app.version:2.0.0}")
    private String appVersion;

    @Value("${app.slot-name:Production}")
    private String slotName;

    @Value("${app.environment:Azure Cloud}")
    private String environment;

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
        model.addAttribute("version", "Version 1.0");
        model.addAttribute("rawVersion", appVersion);
        model.addAttribute("releaseName", releaseName());
        model.addAttribute("slotName", slotName);
        model.addAttribute("environment", environment);
    }

    private void populateVersion2Model(Model model) {
        model.addAttribute("version", "Version 2.0");
        model.addAttribute("rawVersion", appVersion);
        model.addAttribute("releaseName", releaseName());
        model.addAttribute("slotName", slotName);
        model.addAttribute("environment", environment);
        model.addAttribute("healthStatus", "UP");
        model.addAttribute("warmupStatus", "READY");
        model.addAttribute("swapValidation", "READY");
        model.addAttribute("deploymentStatus", "VALIDATED");
        model.addAttribute("timestamp", Instant.now().toString());
    }

    private String releaseName() {
        return "production".equalsIgnoreCase(slotName) ? "Production Release" : "Staging Release";
    }
}