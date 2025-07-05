package com.example.systemedge;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable; // <-- Import for @Nullable
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;





public class ChipsetsFragment extends Fragment {

    //declarations
    private LinearLayout containerCard1,containerCard2;
    private LayoutInflater inflater;


    //methods
    private void addRowToContainer(LinearLayout container, String key, String value) {
        // Use the default text color
        addRowToContainer(container, key, value, R.color.value_color);
    }

    private void addRowToContainer(LinearLayout container, String key, String value, int valueColorRes) {
        // Inflate the row layout "prototype"
        View rowView = inflater.inflate(R.layout.layout_info_row, container, false);

        // Find the TextViews within the inflated view
        TextView textKey = rowView.findViewById(R.id.text_key);
        TextView textValue = rowView.findViewById(R.id.text_value);

        // Set the text
        textKey.setText(key);
        textValue.setText(value);

        // Set the custom color
        if (getContext() != null) {
            textValue.setTextColor(ContextCompat.getColor(getContext(), valueColorRes));
        }

        // Add the newly created row to the container
        container.addView(rowView);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_chipsets, container, false);
    }

    @Override
    @SuppressWarnings("deprecation") // Add this to suppress warnings for the whole method
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        TextView textView1 = view.findViewById(R.id.text_android_version);
        TextView textView2 = view.findViewById(R.id.text_android_version_name);

        String rawHardware = getChipsetInfo();
        String readableChipset = getReadableChipset(rawHardware);
        textView1.setText(readableChipset);

        int coreCount = Runtime.getRuntime().availableProcessors();
        textView2.setText("Number of Cores: " + coreCount);



        containerCard1 = view.findViewById(R.id.container_card_1);
        containerCard2 = view.findViewById(R.id.container_card_2);
        containerCard1.removeAllViews();



        addRowToContainer(containerCard1, "Processor", splitChipsetName("Processor"));
        addRowToContainer(containerCard1, "Vendor", rawHardware);
        populateClusterInfo();

        String cpuArc = splitChipsetName("CPU architecture") + " nm";
        addRowToContainer(containerCard1, "CPU Architecture", cpuArc);

        addRowToContainer(containerCard1, "Supported ABI", Build.SUPPORTED_ABIS[0]);
        addRowToContainer(containerCard1, "CPU Implementer", splitChipsetName("CPU implementer"));
        addRowToContainer(containerCard1, "BogoMIPS", splitChipsetName("BogoMIPS"));
        addRowToContainer(containerCard1, "CPU Variant", splitChipsetName("CPU variant"));
        addRowToContainer(containerCard1, "CPU Part", splitChipsetName("CPU part"));
        addRowToContainer(containerCard1, "CPU Revision", splitChipsetName("CPU revision"));

        // card 2

        populateGpuInfo();
        populateVulkanInfo();
        populateGpuFrequencyInfo();







    }


    //methods



    /**
     * This method now ONLY creates and adds the GLSurfaceView.
     * It no longer calls onResume().
     */
    private void populateGpuInfo() {
        // Get OpenGL Version (this is fast)
        ActivityManager am = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ConfigurationInfo configInfo = am.getDeviceConfigurationInfo();
            if (configInfo != null) {
                addRowToContainer(containerCard2, "GPU Version", configInfo.getGlEsVersion());
            }
        }

        // Get Vulkan & Frequency Info (also fast)


        // This EGL method runs in the background to get vendor/renderer.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String vendor = "Unknown";
            String renderer = "Unknown";

            // EGL logic to get GPU info without a view
            EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
                int[] version = new int[2];
                if (EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
                    int[] numConfigs = new int[1];
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] attribList = {EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_NONE};
                    if (EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, 1, numConfigs, 0)) {
                        int[] contextAttribs = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
                        EGLContext eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT, contextAttribs, 0);
                        if (eglContext != EGL14.EGL_NO_CONTEXT) {
                            int[] surfaceAttribs = {EGL14.EGL_WIDTH, 64, EGL14.EGL_HEIGHT, 64, EGL14.EGL_NONE};
                            EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, configs[0], surfaceAttribs, 0);
                            if (eglSurface != EGL14.EGL_NO_SURFACE) {
                                if (EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                                    vendor = GLES20.glGetString(GLES20.GL_VENDOR);
                                    renderer = GLES20.glGetString(GLES20.GL_RENDERER);
                                    EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                                }
                                EGL14.eglDestroySurface(eglDisplay, eglSurface);
                            }
                            EGL14.eglDestroyContext(eglDisplay, eglContext);
                        }
                    }
                }
                EGL14.eglTerminate(eglDisplay);
            }

            final String finalVendor = vendor;
            final String finalRenderer = renderer;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    addRowToContainer(containerCard2, "GPU Vendor", finalVendor);
                    addRowToContainer(containerCard2, "GPU Renderer", finalRenderer);
                });
            }
        });
    }


    private void populateGpuFrequencyInfo() {
        String maxFreq = readSystemFile("/sys/class/kgsl/kgsl-3d0/max_gpuclk");
        String currentFreq = readSystemFile("/sys/class/kgsl/kgsl-3d0/gpuclk");

        addRowToContainer(containerCard2, "Frequency", maxFreq.equals("N/A") ? "Unknown" : maxFreq + " Hz");
        addRowToContainer(containerCard2, "Current frequency", currentFreq.equals("N/A") ? "Unknown" : currentFreq + " Hz");
    }

    /**
     * Helper method to read a system file.
     */
    private String readSystemFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (IOException e) {
            return "N/A";
        }
    }

    private void populateVulkanInfo() {
        PackageManager pm = requireContext().getPackageManager();
        FeatureInfo[] features = pm.getSystemAvailableFeatures();

        boolean vulkanFound = false;
        if (features != null) {
            // Loop through all available features on the device
            for (FeatureInfo feature : features) {
                // Check if the feature's name matches the Vulkan version feature
                if (feature.name != null && feature.name.equals(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)) {
                    int version = feature.version;

                    // Decode the version integer
                    int major = (version >> 22);
                    int minor = (version >> 12) & 0x3FF;

                    addRowToContainer(containerCard2, "Vulkan", major + "." + minor);
                    vulkanFound = true;
                    break; // Exit the loop once we've found it
                }
            }
        }

        if (!vulkanFound) {
            addRowToContainer(containerCard2, "Vulkan", "Not Supported");
        }
    }


    // In ChipsetsFragment.java





    private void populateClusterInfo() {
        // A map to hold cluster data: MaxFrequency -> List of Core Indexes
        Map<Long, List<Integer>> clusters = new HashMap<>();
        int coreCount = Runtime.getRuntime().availableProcessors();

        // 1. Read max frequency for each core and group them into clusters
        for (int i = 0; i < coreCount; i++) {
            String maxFreqPath = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_max_freq";
            try {
                long maxFreq = Long.parseLong(readSystemFile(maxFreqPath).trim());
                if (!clusters.containsKey(maxFreq)) {
                    clusters.put(maxFreq, new ArrayList<>());
                }
                clusters.get(maxFreq).add(i);
            } catch (NumberFormatException e) {
                // This core might be offline or its frequency unreadable
            }
        }

        // 2. Build the formatted strings from the cluster data
        StringBuilder structBuilder = new StringBuilder();
        StringBuilder freqBuilder = new StringBuilder();

        // Sort clusters by frequency for consistent ordering
        List<Long> sortedFrequencies = new ArrayList<>(clusters.keySet());
        Collections.sort(sortedFrequencies, Collections.reverseOrder()); // Big cores first

        for (Long maxFreq : sortedFrequencies) {
            List<Integer> coreIndices = clusters.get(maxFreq);
            if (coreIndices == null || coreIndices.isEmpty()) continue;

            int clusterCoreCount = coreIndices.size();

            // Get min frequency from the first core in the cluster
            String minFreqPath = "/sys/devices/system/cpu/cpu" + coreIndices.get(0) + "/cpufreq/scaling_min_freq";
            String minFreqStr = readSystemFile(minFreqPath);

            try {
                long minMhz = Long.parseLong(minFreqStr.trim()) / 1000;
                long maxMhz = maxFreq / 1000;

                // Append to the string builders
                if (structBuilder.length() > 0) {
                    structBuilder.append("\n");
                    freqBuilder.append("\n");
                }

                // Note: We use generic names like "Big" and "LITTLE" as we can't get "Kryo" names
                String coreType = (maxMhz > 2000) ? "Major" : "Minor"; // Simple guess
                structBuilder.append(clusterCoreCount).append(" x ").append(coreType).append(" Cores");
                freqBuilder.append(clusterCoreCount).append(" x ").append(minMhz).append("MHz - ").append(maxMhz).append("MHz");

            } catch (NumberFormatException e) {
                // Skip this cluster if data is unreadable
            }
        }

        // 3. Add the final formatted strings to your UI
        addRowToContainer(containerCard1, "Core Structure", structBuilder.toString());
        addRowToContainer(containerCard1, "Frequency", freqBuilder.toString());
    }




    private int readCoreFrequency(int coreNumber) {
        // Path to the file that holds the current frequency for a given core
        String path = "/sys/devices/system/cpu/cpu" + coreNumber + "/cpufreq/scaling_cur_freq";
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // The value is in KHz, so divide by 1000 to get MHz
            return Integer.parseInt(reader.readLine()) / 1000;
        } catch (IOException | NumberFormatException e) {
            // This can happen if the core is offline
            return -1;
        }
    }


    private String splitChipsetName(String name) {
        String hardware = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(name)) {
                    hardware = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            hardware = "Unknown";
        }
        return hardware;
    }


    private String getChipsetInfo() {
        String hardware = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ardware")) {
                    hardware = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            hardware = "Unknown";
        }
        return hardware;
    }

    // chipset polished info
    private String getReadableChipset(String hardwareId) {
        Map<String, String> chipsetMap = new HashMap<>();
        // Snapdragon 8 Series
        chipsetMap.put("SM8750", "Qualcomm® Snapdragon™ 8 Elite");
        chipsetMap.put("SM8650", "Qualcomm® Snapdragon™ 8 Gen 3");
        chipsetMap.put("SM8550", "Qualcomm® Snapdragon™ 8 Gen 2");
        chipsetMap.put("SM8475", "Qualcomm® Snapdragon™ 8+ Gen 1");
        chipsetMap.put("SM8450", "Qualcomm® Snapdragon™ 8 Gen 1");
        chipsetMap.put("SM8350", "Qualcomm® Snapdragon™ 888 / 888+");
        chipsetMap.put("SM8250", "Qualcomm® Snapdragon™ 865 / 865+");
        chipsetMap.put("SM8150", "Qualcomm® Snapdragon™ 855 / 855+");

        // Snapdragon 7 Series
        chipsetMap.put("SM7675", "Qualcomm® Snapdragon™ 7+ Gen 3");
        chipsetMap.put("SM7635", "Qualcomm® Snapdragon™ 7s Gen 3");
        chipsetMap.put("SM7550", "Qualcomm® Snapdragon™ 7 Gen 3");
        chipsetMap.put("SM7475", "Qualcomm® Snapdragon™ 7+ Gen 2");
        chipsetMap.put("SM7450", "Qualcomm® Snapdragon™ 7 Gen 1");
        chipsetMap.put("SM7350", "Qualcomm® Snapdragon™ 780G");
        chipsetMap.put("SM7325", "Qualcomm® Snapdragon™ 778G / 778G+");

        // Snapdragon 6 Series
        chipsetMap.put("SM6650", "Qualcomm® Snapdragon™ 6 Gen 4");
        chipsetMap.put("SM6475", "Qualcomm® Snapdragon™ 6 Gen 3");
        chipsetMap.put("SM6450", "Qualcomm® Snapdragon™ 6 Gen 1");
        chipsetMap.put("SM6375", "Qualcomm® Snapdragon™ 6s Gen 3");
        chipsetMap.put("SM6115", "Qualcomm® Snapdragon™ 6s Gen 1");
        chipsetMap.put("SM6125", "Qualcomm® Snapdragon™ 665");
        chipsetMap.put("SM6150", "Qualcomm® Snapdragon™ 675");

        // Snapdragon 4 Series
        chipsetMap.put("SM4450", "Qualcomm® Snapdragon™ 4 Gen 2");
        chipsetMap.put("SM4375", "Qualcomm® Snapdragon™ 4 Gen 1");
        chipsetMap.put("SM4250", "Qualcomm® Snapdragon™ 460");

        // Other known Snapdragon models
        chipsetMap.put("SM7225", "Qualcomm® Snapdragon™ 750G");
        chipsetMap.put("SM7125", "Qualcomm® Snapdragon™ 720G");
        chipsetMap.put("SDM660", "Qualcomm® Snapdragon™ 660");
        chipsetMap.put("SDM636", "Qualcomm® Snapdragon™ 636");
        chipsetMap.put("SDM450", "Qualcomm® Snapdragon™ 450");

        // Samsung Exynos
        chipsetMap.put("exynos9820", "Samsung Exynos 9820");
        chipsetMap.put("exynos1380", "Samsung Exynos 1380");
        chipsetMap.put("exynos990", "Samsung Exynos 990");

        // MediaTek
        chipsetMap.put("MT6768", "MediaTek Helio G85");
        chipsetMap.put("MT6785", "MediaTek Helio G90T");
        chipsetMap.put("MT6893", "MediaTek Dimensity 1200");

        // HiSilicon Kirin
        chipsetMap.put("kirin980", "HiSilicon Kirin 980");
        chipsetMap.put("kirin990", "HiSilicon Kirin 990");

        // UNISOC
        chipsetMap.put("ums512", "UNISOC Tiger T618");
        // Add more mappings as needed

        for (String key : chipsetMap.keySet()) {
            if (hardwareId.toLowerCase().contains(key.toLowerCase())) {
                return chipsetMap.get(key);
            }
        }
        return "Unknown Chipset (" + hardwareId + ")";
    }






}