package com.example.systemedge;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class StorageTestActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private View resultsView;
    private TextView redundantResult, largeResult, duplicateResult, screenshotResult;

    // Launcher to handle the result of the permission request
    private final ActivityResultLauncher<Intent> storagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // After the user returns from the settings screen, check the permission again.
                if (hasStoragePermission()) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    startStorageAnalysis();
                } else {
                    Toast.makeText(this, "All Files Access permission is required to analyze storage.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_test);

        progressBar = findViewById(R.id.progress_bar);
        resultsView = findViewById(R.id.results_scroll_view);
        redundantResult = findViewById(R.id.redundant_files_result);
        largeResult = findViewById(R.id.large_files_result);
        duplicateResult = findViewById(R.id.duplicate_files_result);
        screenshotResult = findViewById(R.id.screenshots_result);

        // Check for permission when the activity is created
        checkAndRequestPermission();
    }

    private void checkAndRequestPermission() {
        if (hasStoragePermission()) {
            // If we already have permission, start the analysis
            startStorageAnalysis();
        } else {
            // If not, request it by taking the user to settings
            requestStoragePermission();
        }
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            // On older versions, this permission is granted at install time
            // and doesn't need a special check.
            return true;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                storagePermissionLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storagePermissionLauncher.launch(intent);
            }
        }
    }

    private void startStorageAnalysis() {
        // Show the progress bar and hide the results
        progressBar.setVisibility(View.VISIBLE);
        resultsView.setVisibility(View.GONE);

        // Run the entire scan on a background thread to keep the UI responsive
        ExecutorService executor = Executors.newSingleThreadExecutor();
        android.os.Handler handler = new android.os.Handler(getMainLooper());

        executor.execute(() -> {
            // --- BACKGROUND SCANNING ---
            List<File> redundantFiles = new ArrayList<>();
            List<File> largeFiles = new ArrayList<>();
            List<File> screenshotFiles = new ArrayList<>();
            Map<Long, List<File>> filesBySize = new HashMap<>(); // For duplicate detection

            // Start scanning from the root of the user's storage
            File root = Environment.getExternalStorageDirectory();
            scanDirectory(root, redundantFiles, largeFiles, screenshotFiles, filesBySize);

            // Find duplicates from the files grouped by size
            List<File> duplicateFiles = findDuplicates(filesBySize);
            // --- END OF BACKGROUND SCANNING ---

            // Post the final results back to the UI thread
            handler.post(() -> updateUiWithResults(redundantFiles, largeFiles, duplicateFiles, screenshotFiles));
        });
    }

    private void scanDirectory(File directory, List<File> redundantFiles, List<File> largeFiles, List<File> screenshotFiles, Map<Long, List<File>> filesBySize) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, redundantFiles, largeFiles, screenshotFiles, filesBySize);
            } else {
                // Check for Redundant Files (e.g., empty files or log files)
                String name = file.getName().toLowerCase();
                if (file.length() == 0 || name.endsWith(".tmp") || name.endsWith(".log")) {
                    redundantFiles.add(file);
                }

                // Check for Large Files (e.g., > 100 MB)
                if (file.length() > (100 * 1024 * 1024)) {
                    largeFiles.add(file);
                }

                // Check for Screenshots
                if (file.getParent() != null && file.getParentFile().getName().equalsIgnoreCase("Screenshots")) {
                    screenshotFiles.add(file);
                }

                // Group files by size for duplicate check
                long size = file.length();
                if (size > 0) {
                    List<File> fileList = filesBySize.getOrDefault(size, new ArrayList<>());
                    fileList.add(file);
                    filesBySize.put(size, fileList);
                }
            }
        }
    }

    private List<File> findDuplicates(Map<Long, List<File>> filesBySize) {
        List<File> duplicateFiles = new ArrayList<>();
        Map<String, List<File>> filesByHash = new HashMap<>();

        // Only check files that have size-matches
        for (List<File> fileList : filesBySize.values()) {
            if (fileList.size() > 1) {
                for (File file : fileList) {
                    try {
                        String hash = calculateMD5(file);
                        List<File> hashList = filesByHash.getOrDefault(hash, new ArrayList<>());
                        hashList.add(file);
                        filesByHash.put(hash, hashList);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Add all files from sets with more than one item to the duplicate list
        for (List<File> hashList : filesByHash.values()) {
            if (hashList.size() > 1) {
                duplicateFiles.addAll(hashList);
            }
        }
        return duplicateFiles;
    }

    private void updateUiWithResults(List<File> redundant, List<File> large, List<File> duplicates, List<File> screenshots) {
        // Hide the progress bar and show the results
        progressBar.setVisibility(View.GONE);
        resultsView.setVisibility(View.VISIBLE);

        // Update each result card
        redundantResult.setText(formatResult(redundant));
        largeResult.setText(formatResult(large));
        duplicateResult.setText(formatResult(duplicates));
        screenshotResult.setText(formatResult(screenshots));
    }

    private String formatResult(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        double totalSizeMB = totalSize / (1024.0 * 1024.0);
        return String.format(Locale.US, "%d Files (%.2f MB)", files.size(), totalSizeMB);
    }

    private String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        BigInteger bigInt = new BigInteger(1, bytes);
        String md5 = bigInt.toString(16);
        // Pad with leading zeros to get the full 32 chars
        while(md5.length() < 32 ){
            md5 = "0" + md5;
        }
        return md5;
    }
}