package com.example.igs.service;

import jakarta.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class MachineIdService {
    private final CuratorFramework curatorFramework;
    private volatile long machineId = -1;
    private volatile String znodePath; // Tracks znode (e.g., "/machine-ids/id-0")

    @Autowired
    public MachineIdService(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
        // Handle session expiry (e.g., if ZooKeeper quorum is lost)
        curatorFramework.getConnectionStateListenable().addListener((client, state) -> {
            System.err.println("ZooKeeper state: " + state);
            if (state == ConnectionState.LOST || state == ConnectionState.SUSPENDED) {
                machineId = -1;
                znodePath = null;
            } else if (state == ConnectionState.RECONNECTED) {
                try {
                    assignMachineId();
                } catch (Exception e) {
                    System.err.println("Failed to reassign machineId: " + e.getMessage());
                }
            }
        });
    }

    @PostConstruct
    protected void init() throws Exception {
        assignMachineId();
    }

    private synchronized void assignMachineId() throws Exception {
        if (machineId >= 0 && znodePath != null) {
            return; // Already assigned
        }
        // Create lock on /machine-ids/lock
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/machine-ids/lock");
        try {
            // Wait up to 5 seconds to acquire lock
            if (!lock.acquire(5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Failed to acquire lock for machineId");
            }
            // Ensure parent znode exists
            try {
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/machine-ids");
            } catch (KeeperException.NodeExistsException e) {
                // Ignore if exists
            }
            // List existing znodes (e.g., [id-0, id-1, id-6])
            List<String> existingIds = curatorFramework.getChildren().forPath("/machine-ids");
            // Find first unused machineId (0-31)
            long newId = -1;
            for (int i = 0; i < 32; i++) {
                if (!existingIds.contains("id-" + i)) {
                    newId = i;
                    break;
                }
            }
            if (newId == -1) {
                throw new RuntimeException("No available machineId (0-31)");
            }
            // Create ephemeral znode (e.g., /machine-ids/id-2)
            znodePath = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath("/machine-ids/id-" + newId);
            machineId = newId;
            System.out.println("Assigned machineId: " + machineId + " from znode: " + znodePath);
        } finally {
            lock.release(); // Always release lock
        }
    }

    public long getMachineId() {
        if (machineId < 0) {
            throw new RuntimeException("MachineId not assigned");
        }
        return machineId;
    }
}