package com.example.systemedge;

public class BenchmarkResult {
    public final int finalScore;
    public final String cpuScore;
    public final String gpuScore;
    public final String memoryScore;
    public final String storageScore;
    public final String thermalStatus;
    public final String batteryDrop;

    public BenchmarkResult(int finalScore, String cpuScore, String gpuScore, String memoryScore, String storageScore, String thermalStatus, String batteryDrop) {
        this.finalScore = finalScore;
        this.cpuScore = cpuScore;
        this.gpuScore = gpuScore;
        this.memoryScore = memoryScore;
        this.storageScore = storageScore;
        this.thermalStatus = thermalStatus;
        this.batteryDrop = batteryDrop;
    }
}