package com.example.igs.service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class SnowflakeSimulation {
    // Snowflake ID Generator class
    static class SnowflakeIdGenerator {
        private static final long EPOCH = 1672531200000L; // January 1, 2023
        private static final long SEQUENCE_BITS = 17L;    // 131,072 IDs/ms
        private static final long MACHINE_ID_BITS = 5L;   // 32 machines
        private static final long TIMESTAMP_BITS = 41L;

        private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1; // 131,071
        private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1; // 31

        private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
        private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

        private final long machineId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        // Simulated clock skew for testing
        private final Random random = new Random();
        private final boolean simulateClockSkew;

        public SnowflakeIdGenerator(long machineId, boolean simulateClockSkew) {
            if (machineId < 0 || machineId > MAX_MACHINE_ID) {
                throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
            }
            this.machineId = machineId;
            this.simulateClockSkew = simulateClockSkew;
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

        // Decode ID for logging
        public Map<String, Long> decodeId(long id) {
            Map<String, Long> components = new HashMap<>();
            components.put("timestampMs", ((id >> TIMESTAMP_SHIFT) + EPOCH));
            components.put("machineId", (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID);
            components.put("sequence", id & MAX_SEQUENCE);
            return components;
        }

        private long getCurrentTimestamp() {
            long timestamp = Instant.now().toEpochMilli();
            // Simulate small clock skew (0-2ms) for testing
            if (simulateClockSkew && random.nextInt(100) < 10) {
                timestamp -= random.nextInt(3);
            }
            return timestamp;
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

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int numInstances = 4; // Simulate 4 service instances
        int idsPerInstance = 10000; // IDs per instance
        boolean simulateClockSkew = true; // Simulate clock drift

        // Thread pool to simulate concurrent instances
        ExecutorService executor = Executors.newFixedThreadPool(numInstances);
        List<Future<List<Long>>> futures = new ArrayList<>();
        Set<Long> allIds = Collections.synchronizedSet(new HashSet<>());

        // Start each instance
        for (int machineId = 0; machineId < numInstances; machineId++) {
            final int id = machineId;
            futures.add(executor.submit(() -> {
                SnowflakeIdGenerator generator = new SnowflakeIdGenerator(id, simulateClockSkew);
                List<Long> ids = new ArrayList<>(idsPerInstance);
                
                // Generate IDs
                for (int i = 0; i < idsPerInstance; i++) {
                    long generatedId = generator.generateId();
                    ids.add(generatedId);
                    allIds.add(generatedId);
                    
                    // Log first few IDs for each instance
                    if (i < 3) {
                        Map<String, Long> components = generator.decodeId(generatedId);
                        System.out.printf("Instance %d, ID: %d, Timestamp: %d, Machine ID: %d, Sequence: %d%n",
                                id, generatedId, components.get("timestampMs"),
                                components.get("machineId"), components.get("sequence"));
                    }
                }
                return ids;
            }));
        }

        // Collect results
        for (Future<List<Long>> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Shutdown executor
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Verify results
        System.out.println("\nSimulation Summary:");
        System.out.println("Total IDs generated: " + allIds.size());
        System.out.println("Expected IDs: " + (numInstances * idsPerInstance));
        System.out.println("Duplicates found: " + (numInstances * idsPerInstance - allIds.size()));
        System.out.println("Elapsed Time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}