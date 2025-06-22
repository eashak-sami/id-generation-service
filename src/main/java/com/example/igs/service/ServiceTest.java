package com.example.igs.service;

public class ServiceTest {
    public static void main(String[] args) {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(10);

        long currentTime = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++) {

            generator.generateId();

        }
        System.out.println("Elapsed Time: " + (System.currentTimeMillis() - currentTime)/1000);
    }
}
