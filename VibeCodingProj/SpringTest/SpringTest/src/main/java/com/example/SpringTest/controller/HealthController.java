package com.example.SpringTest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Health Check", description = "System health and monitoring endpoints")
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private DataSource dataSource;

    @Operation(summary = "Get system health status", description = "Returns detailed health information about the application")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        logger.debug("Health check requested");
        
        Map<String, Object> healthInfo = new HashMap<>();
        
        // Basic application info
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "Book Management System");
        healthInfo.put("version", "1.0.0");
        
        // System information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
        systemInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
        systemInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
        systemInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
        systemInfo.put("availableProcessors", runtime.availableProcessors());
        
        healthInfo.put("system", systemInfo);
        
        // Database health
        Map<String, Object> dbHealth = checkDatabaseHealth();
        healthInfo.put("database", dbHealth);
        
        // JVM information
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("javaVersion", System.getProperty("java.version"));
        jvmInfo.put("javaVendor", System.getProperty("java.vendor"));
        jvmInfo.put("osName", System.getProperty("os.name"));
        jvmInfo.put("osVersion", System.getProperty("os.version"));
        
        healthInfo.put("jvm", jvmInfo);
        
        return ResponseEntity.ok(healthInfo);
    }

    @Operation(summary = "Get application readiness", description = "Check if application is ready to serve requests")
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> getReadiness() {
        Map<String, Object> readiness = new HashMap<>();
        
        boolean isReady = true;
        StringBuilder message = new StringBuilder();
        
        // Check database connectivity
        Map<String, Object> dbHealth = checkDatabaseHealth();
        if (!"UP".equals(dbHealth.get("status"))) {
            isReady = false;
            message.append("Database not available. ");
        }
        
        readiness.put("ready", isReady);
        readiness.put("status", isReady ? "READY" : "NOT_READY");
        readiness.put("message", message.toString().trim());
        readiness.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(readiness);
    }

    @Operation(summary = "Get application liveness", description = "Check if application is alive and responding")
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> getLiveness() {
        Map<String, Object> liveness = new HashMap<>();
        
        liveness.put("alive", true);
        liveness.put("status", "ALIVE");
        liveness.put("uptime", getUptime());
        liveness.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(liveness);
    }

    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try {
            Connection connection = dataSource.getConnection();
            boolean isValid = connection.isValid(5); // 5 seconds timeout
            connection.close();
            
            dbHealth.put("status", isValid ? "UP" : "DOWN");
            dbHealth.put("database", "H2");
            dbHealth.put("validationQuery", "SELECT 1");
            
        } catch (Exception e) {
            logger.error("Database health check failed", e);
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        
        return dbHealth;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - getStartTime();
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%d days, %d hours, %d minutes, %d seconds", 
                            days, hours % 24, minutes % 60, seconds % 60);
    }

    private long getStartTime() {
        return System.currentTimeMillis() - 
               java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
    }
} 