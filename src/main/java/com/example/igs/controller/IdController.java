package com.example.igs.controller;

import com.example.igs.service.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IdController {
    private final SnowflakeIdGenerator idGenerator;

    public IdController(@Value("${snowflake.machine-id:0}") long machineId) {
        this.idGenerator = new SnowflakeIdGenerator(machineId);
    }

    @GetMapping("/generate-id")
    public long generateId() {
        return idGenerator.generateId();
    }
}