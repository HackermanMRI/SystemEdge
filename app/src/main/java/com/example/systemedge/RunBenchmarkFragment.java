package com.example.systemedge; // Change to your package name

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.io.OutputStream;

public class RunBenchmarkFragment extends Fragment {

    private BenchmarkViewModel viewModel;
    private Button startButton;
    private Button exportButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView finalScoreText;
    private TextView scoreTitle;
    private LinearLayout resultsLayout;
    private TextView cpuResultText, gpuResultText, memoryResultText, storageResultText, thermalResultText, batteryResultText;

    private ActivityResultLauncher<Intent> createFileLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BenchmarkViewModel.class);

        createFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            writeJsonToUri(uri);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run_benchmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);

        startButton.setOnClickListener(v -> viewModel.startBenchmark());
        exportButton.setOnClickListener(v -> exportResults());

        observeViewModel();
    }

    private void bindViews(View view) {
        startButton = view.findViewById(R.id.startButton);
        exportButton = view.findViewById(R.id.exportButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
        finalScoreText = view.findViewById(R.id.finalScoreText);
        scoreTitle = view.findViewById(R.id.scoreTitle);
        resultsLayout = view.findViewById(R.id.resultsLayout);
        cpuResultText = view.findViewById(R.id.cpuResultText);
        gpuResultText = view.findViewById(R.id.gpuResultText);
        memoryResultText = view.findViewById(R.id.memoryResultText);
        storageResultText = view.findViewById(R.id.storageResultText);
        thermalResultText = view.findViewById(R.id.thermalResultText);
        batteryResultText = view.findViewById(R.id.batteryResultText);
    }

    private void observeViewModel() {
        viewModel.isBenchmarking.observe(getViewLifecycleOwner(), isRunning -> {
            startButton.setEnabled(!isRunning);
            progressBar.setVisibility(isRunning ? View.VISIBLE : View.GONE);
            progressText.setVisibility(isRunning ? View.VISIBLE : View.GONE);
            if (isRunning) {
                finalScoreText.setVisibility(View.GONE);
                scoreTitle.setVisibility(View.GONE);
                resultsLayout.setVisibility(View.GONE);
                exportButton.setVisibility(View.GONE);
            }
        });

        viewModel.progress.observe(getViewLifecycleOwner(), progress -> progressBar.setProgress(progress));
        viewModel.progressMessage.observe(getViewLifecycleOwner(), message -> progressText.setText(message));

        viewModel.finalScore.observe(getViewLifecycleOwner(), score -> {
            if (score > 0) {
                finalScoreText.setText(String.valueOf(score));
                finalScoreText.setVisibility(View.VISIBLE);
                scoreTitle.setVisibility(View.VISIBLE);
                resultsLayout.setVisibility(View.VISIBLE);
                exportButton.setVisibility(View.VISIBLE);
            }
        });

        viewModel.cpuResult.observe(getViewLifecycleOwner(), text -> cpuResultText.setText(text));
        viewModel.gpuResult.observe(getViewLifecycleOwner(), text -> gpuResultText.setText(text));
        viewModel.memoryResult.observe(getViewLifecycleOwner(), text -> memoryResultText.setText(text));
        viewModel.storageResult.observe(getViewLifecycleOwner(), text -> storageResultText.setText(text));
        viewModel.thermalResult.observe(getViewLifecycleOwner(), text -> thermalResultText.setText(text));
        viewModel.batteryResult.observe(getViewLifecycleOwner(), text -> batteryResultText.setText(text));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify all view references
        startButton = null;
        exportButton = null;
        progressBar = null;
        progressText = null;
        // ... and so on for all other views
    }

    private void exportResults() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "benchmark_results.json");
        createFileLauncher.launch(intent);
    }

    private void writeJsonToUri(Uri uri) {
        String jsonString = viewModel.getResultsAsJson();
        if (jsonString == null) return;

        try {
            OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(jsonString.getBytes());
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}