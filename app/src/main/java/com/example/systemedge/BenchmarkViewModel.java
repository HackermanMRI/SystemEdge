package com.example.systemedge; // Change to your package name

import android.app.Application;
import android.content.Context;
import android.os.BatteryManager;
import android.os.PowerManager;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.atomic.AtomicLong;

public class BenchmarkViewModel extends AndroidViewModel {

    // LiveData for UI updates
    private final MutableLiveData<Integer> _progress = new MutableLiveData<>();
    public final LiveData<Integer> progress = _progress;

    private final MutableLiveData<String> _progressMessage = new MutableLiveData<>();
    public final LiveData<String> progressMessage = _progressMessage;

    private final MutableLiveData<Boolean> _isBenchmarking = new MutableLiveData<>();
    public final LiveData<Boolean> isBenchmarking = _isBenchmarking;

    private final MutableLiveData<Integer> _finalScore = new MutableLiveData<>();
    public final LiveData<Integer> finalScore = _finalScore;

    // LiveData for individual results
    private final MutableLiveData<String> _cpuResult = new MutableLiveData<>();
    public final LiveData<String> cpuResult = _cpuResult;
    private final MutableLiveData<String> _gpuResult = new MutableLiveData<>();
    public final LiveData<String> gpuResult = _gpuResult;
    private final MutableLiveData<String> _memoryResult = new MutableLiveData<>();
    public final LiveData<String> memoryResult = _memoryResult;
    private final MutableLiveData<String> _storageResult = new MutableLiveData<>();
    public final LiveData<String> storageResult = _storageResult;
    private final MutableLiveData<String> _thermalResult = new MutableLiveData<>();
    public final LiveData<String> thermalResult = _thermalResult;
    private final MutableLiveData<String> _batteryResult = new MutableLiveData<>();
    public final LiveData<String> batteryResult = _batteryResult;

    private BenchmarkResult benchmarkResult = null;

    /*private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final PowerManager powerManager;
    private final BatteryManager batteryManager;*/
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final PowerManager powerManager;
    private final BatteryManager batteryManager;


    public BenchmarkViewModel(@NonNull Application application) {
        super(application);
        powerManager = (PowerManager) application.getSystemService(Context.POWER_SERVICE);
        batteryManager = (BatteryManager) application.getSystemService(Context.BATTERY_SERVICE);
    }

    public void startBenchmark() {
        /*executorService.execute(() -> {
            _isBenchmarking.postValue(true);
            _finalScore.postValue(0);*/
        Executors.newSingleThreadExecutor().execute(() -> {
            _isBenchmarking.postValue(true);
            _finalScore.postValue(0);


            int initialBattery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            // 1. CPU Benchmark
            _progress.postValue(0);
            _progressMessage.postValue("Running CPU Benchmark...");
            long cpuOps = runCpuBenchmark();
            double cpuScore = cpuOps / 1000000.0;
            _cpuResult.postValue("CPU: " + new DecimalFormat("#,###.##").format(cpuOps / 1_000_000.0) + " M-ops/sec");
            _progress.postValue(25);

            // 2. GPU Benchmark (Proxy)
            _progressMessage.postValue("Running GPU Benchmark...");
            double gpuTime = 1000.0; // Placeholder
            double gpuScore = 50000 / gpuTime;
            _gpuResult.postValue("GPU: " + new DecimalFormat("#.##").format(gpuScore) + " FPS (Simulated)");
            _progress.postValue(50);

            // 3. Memory Benchmark
            _progressMessage.postValue("Running Memory Benchmark...");
            double memThroughput = runMemoryBenchmark();
            _memoryResult.postValue("Memory: " + new DecimalFormat("#.##").format(memThroughput / 1024) + " GB/s");
            _progress.postValue(75);

            // 4. Storage Benchmark
            _progressMessage.postValue("Running Storage Benchmark...");
            double[] storageSpeeds = runStorageBenchmark();
            _storageResult.postValue("Storage: " + new DecimalFormat("#.##").format(storageSpeeds[0]) + " MB/s    Read / " + new DecimalFormat("#.##").format(storageSpeeds[1]) + " MB/s Write");
            _progress.postValue(90);

            // 5. Finalize
            _progressMessage.postValue("Finalizing...");
            String thermalStatus = getThermalStatus();
            int finalBattery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            _thermalResult.postValue("Thermal: " + thermalStatus);
            _batteryResult.postValue("Battery Drop: " + (initialBattery - finalBattery) + "%");

            double finalScoreValue = (cpuScore * 5) + (gpuScore * 3) + (memThroughput * 0.1) + ((storageSpeeds[0] + storageSpeeds[1]) * 1.5);
            _finalScore.postValue((int)finalScoreValue);

            benchmarkResult = new BenchmarkResult(
                    (int)finalScoreValue,
                    _cpuResult.getValue(), _gpuResult.getValue(), _memoryResult.getValue(),
                    _storageResult.getValue(), _thermalResult.getValue(), _batteryResult.getValue()
            );

            _progress.postValue(100);
            _progressMessage.postValue("Benchmark Complete!");
            _isBenchmarking.postValue(false);
        });
    }

    /*private long runCpuBenchmark() {
        int numCores = Runtime.getRuntime().availableProcessors();
        long durationMillis = 3000L;
        long totalOps = 0L;
        Random random = new Random();

        long endTime = System.currentTimeMillis() + durationMillis;
        while(System.currentTimeMillis() < endTime) {
            double value = random.nextDouble();
            for (int i = 0; i < 1000; i++) {
                value += random.nextDouble() * random.nextDouble() - random.nextDouble() / 2.0;
                value *= 1.00000001;
            }
            totalOps += 1000;
        }
        return (totalOps * 1000 / durationMillis) * numCores;
    }*/
    private long runCpuBenchmark() {
        int numCores = Math.max(1, Runtime.getRuntime().availableProcessors());
        long durationMillis = 3000L;
        AtomicLong totalOps = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(numCores);

        for (int i = 0; i < numCores; i++) {
            executorService.execute(() -> {
                Random random = new Random();
                long endTime = System.currentTimeMillis() + durationMillis;
                long coreOps = 0;
                while (System.currentTimeMillis() < endTime) {
                    double value = random.nextDouble();
                    // This loop remains a simple but effective mix of floating-point operations
                    for (int j = 0; j < 1000; j++) {
                        value += random.nextDouble() - 0.5;
                        value *= 1.00000001;
                    }
                    coreOps += 1000;
                }
                totalOps.addAndGet(coreOps);
                latch.countDown();
            });
        }

        try {
            latch.await(); // Wait for all threads to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return (totalOps.get() * 1000) / durationMillis;
    }


    /*private double runMemoryBenchmark() {
        int bufferSize = 50 * 1024 * 1024;
        byte[] buffer = new byte[bufferSize];
        Random random = new Random();

        long startTimeWrite = System.currentTimeMillis();
        for (int i = 0; i < bufferSize; i++) {
            buffer[i] = (byte) random.nextInt();
        }
        long writeTime = System.currentTimeMillis() - startTimeWrite;

        long startTimeRead = System.currentTimeMillis();
        for (int i = 0; i < bufferSize; i++) {
            byte b = buffer[i];
        }
        long readTime = System.currentTimeMillis() - startTimeRead;

        double totalBytes = (double) (bufferSize * 2) / (1024 * 1024);
        double totalTimeSec = (double) (writeTime + readTime) / 1000.0;
        return (totalBytes / totalTimeSec) * 1000; // MB/s
    }*/
    private double runMemoryBenchmark() {
        int bufferSize = 64 * 1024 * 1024; // 64 MB
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
        Random random = new Random();
        byte[] randomBytes = new byte[1024];

        long startTimeWrite = System.currentTimeMillis();
        for (int i = 0; i < bufferSize / randomBytes.length; i++) {
            random.nextBytes(randomBytes);
            buffer.put(randomBytes);
        }
        long writeTime = System.currentTimeMillis() - startTimeWrite;
        buffer.flip(); // Prepare for reading

        long startTimeRead = System.currentTimeMillis();
        byte[] readBuffer = new byte[1024];
        for (int i = 0; i < bufferSize / readBuffer.length; i++) {
            buffer.get(readBuffer);
        }
        long readTime = System.currentTimeMillis() - startTimeRead;

        double totalBytesMB = (double) (bufferSize * 2) / (1024 * 1024);
        double totalTimeSec = (double) (writeTime + readTime) / 1000.0;
        if (totalTimeSec == 0) return 0;
        return totalBytesMB / totalTimeSec; // Throughput in MB/s
    }


    /*private double[] runStorageBenchmark() {
        Context context = getApplication().getApplicationContext();
        String fileName = "benchmark_temp_file.tmp";
        int fileSize = 128 * 1024 * 1024;
        byte[] buffer = new byte[8192];
        File file = new File(context.getFilesDir(), fileName);

        try {
            long startTimeWrite = System.currentTimeMillis();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (int written = 0; written < fileSize; written += buffer.length) {
                    fos.write(buffer);
                }
            }
            long writeTime = System.currentTimeMillis() - startTimeWrite;
            double writeSpeed = (fileSize / (1024.0 * 1024.0)) / (writeTime / 1000.0);

            long startTimeRead = System.currentTimeMillis();
            try (FileInputStream fis = new FileInputStream(file)) {
                while (fis.read(buffer) != -1) {
                    // Reading
                }
            }
            long readTime = System.currentTimeMillis() - startTimeRead;
            double readSpeed = (fileSize / (1024.0 * 1024.0)) / (readTime / 1000.0);

            return new double[]{readSpeed, writeSpeed};
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }*/

    private double[] runStorageBenchmark() {
        Context context = getApplication().getApplicationContext();
        String fileName = "benchmark_temp_file.tmp";
        int fileSize = 128 * 1024 * 1024;
        byte[] buffer = new byte[32 * 1024]; // 32KB buffer
        File file = new File(context.getFilesDir(), fileName);

        try {
            long startTimeWrite = System.currentTimeMillis();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (int written = 0; written < fileSize; written += buffer.length) {
                    fos.write(buffer);
                }
            }
            long writeTime = System.currentTimeMillis() - startTimeWrite;
            double writeSpeed = (fileSize / (1024.0 * 1024.0)) / (writeTime / 1000.0);

            long startTimeRead = System.currentTimeMillis();
            try (FileInputStream fis = new FileInputStream(file)) {
                //noinspection ResultOfMethodCallIgnored
                while (fis.read(buffer) != -1) {
                    // Reading into buffer
                }
            }
            long readTime = System.currentTimeMillis() - startTimeRead;
            double readSpeed = (fileSize / (1024.0 * 1024.0)) / (readTime / 1000.0);

            return new double[]{readSpeed, writeSpeed};
        } catch (Exception e) {
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        } finally {
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    private String getThermalStatus() {
        switch (powerManager.getCurrentThermalStatus()) {
            case PowerManager.THERMAL_STATUS_NONE: return "OK";
            case PowerManager.THERMAL_STATUS_LIGHT: return "Light Throttling";
            case PowerManager.THERMAL_STATUS_MODERATE: return "Moderate Throttling";
            case PowerManager.THERMAL_STATUS_SEVERE: return "Severe Throttling";
            case PowerManager.THERMAL_STATUS_CRITICAL: return "Critical";
            default: return "Unknown";
        }
    }

    public String getResultsAsJson() {
        if (benchmarkResult != null) {
            return new Gson().toJson(benchmarkResult);
        }
        return null;
    }
}