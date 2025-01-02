package com.csye6225_ChenniXu.healthcheck.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import io.micrometer.core.annotation.Timed;

@RestController
public class HealthCheckController {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private MeterRegistry meterRegistry;

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
    
    @GetMapping("/healthz")
    public ResponseEntity<Void> healthCheck() {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "getHealthcheck");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        try (Connection connection = dataSource.getConnection()) {
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of healthcheck API call")
                    .tag("endpoint", "getHealthcheck")
                    .register(meterRegistry));
            // If connection is successful, return 200 OK
            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache")
                    .build();
        } catch (SQLTimeoutException e) {
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of healthcheck API call")
                    .tag("endpoint", "getHealthcheck")
                    .register(meterRegistry));
            // Return 503 Service Unavailable if there's a timeout connecting to the database
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache")
                    .build();
        } catch (SQLException e) {
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of healthcheck API call")
                    .tag("endpoint", "getHealthcheck")
                    .register(meterRegistry));
            // If connection fails, return 503 Service Unavailable
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache")
                    .build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the Health Check Application!");
    }
}
