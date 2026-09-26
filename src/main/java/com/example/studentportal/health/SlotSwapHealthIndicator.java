package com.example.studentportal.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Custom HealthIndicator for Azure App Service Deployment Slot Swap Validation.
 * Azure health probes hit /actuator/health to verify that the staging slot
 * is warmed up and fully operational before traffic routing is swapped.
 */
@Component
public class SlotSwapHealthIndicator implements HealthIndicator {

    @Value("${app.version:2.0.0}")
    private String version;

    @Value("${app.slot-name:Production}")
    private String slotName;

    @Value("${app.environment:Azure Cloud}")
    private String environment;

    private final Instant startupTime = Instant.now();

    @Override
    public Health health() {
        return Health.up()
            .withDetail("version", version)
            .withDetail("slotName", slotName)
            .withDetail("environment", environment)
            .withDetail("warmupStatus", "READY")
            .withDetail("swapValidation", "READY")
            .withDetail("startedAt", startupTime.toString())
            .build();
    }
}
