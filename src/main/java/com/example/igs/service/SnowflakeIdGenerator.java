package com.example.igs.service;

import java.time.Instant;


public class SnowflakeIdGenerator {
    private static final long EPOCH = 1672531200000L; // January 1, 2023
    private static final long SEQUENCE_BITS = 17L;
    private static final long MACHINE_ID_BITS = 10L;
    private static final long TIMESTAMP_BITS = 41L;

    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1; // 4095
    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1; // 1023

    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    private final long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
        this.machineId = machineId;
    }

    public synchronized long generateId() {
        long currentTimestamp = getCurrentTimestamp();

        // Handle clock drift (up to 5ms)
        if (currentTimestamp < lastTimestamp) {
            long drift = lastTimestamp - currentTimestamp;
            if (drift > 5) {
                throw new IllegalStateException("Clock moved backwards by " + drift + "ms!");
            }
            currentTimestamp = waitNextMillis(lastTimestamp);
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) |
               (machineId << MACHINE_ID_SHIFT) |
               sequence;
    }

    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    private long waitNextMillis(long targetTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= targetTimestamp) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for next millisecond");
            }
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
}