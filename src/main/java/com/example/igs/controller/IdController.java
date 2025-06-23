package com.example.igs.controller;

import com.example.igs.service.SnowflakeIdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IdController {
    private final SnowflakeIdGenerator idGenerator;

    public IdController() {
        this.idGenerator = new SnowflakeIdGenerator(10);
    }

    @GetMapping("/generate-id")
    public long generateId() {
        return idGenerator.generateId();
    }
}