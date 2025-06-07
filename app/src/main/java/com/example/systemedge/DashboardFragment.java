package com.example.systemedge;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    TextView chipsetText,manufacturerName,modelName;

    public DashboardFragment() {
        // field for constructors
    }
    // chipset raw info
    private String getChipsetInfo() {
      String hardware = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("hardware")) {
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

    //android version info
    private static String getVersionCodename(String versionNumber) {
        Map<String, String> versionMap = new HashMap<String, String>() {{
            put("1.0", "Alpha");
            put("1.1", "Beta");
            put("1.5", "Cupcake");
            put("1.6", "Donut");
            put("2.0", "Eclair");
            put("2.2", "Froyo");
            put("2.3", "Gingerbread");
            put("3.0", "Honeycomb");
            put("4.0", "Ice Cream Sandwich");
            put("4.1", "Jelly Bean");
            put("4.4", "KitKat");
            put("5.0", "Lollipop");
            put("6.0", "Marshmallow");
            put("7.0", "Nougat");
            put("8.0", "Oreo");
            put("9", "Pie");
            put("10", "Queen Cake");
            put("11", "Red Velvet Cake");
            put("12", "Snow Cone");
            put("12L", "Snow Cone V2");
            put("13", "Tiramisu");
            put("14", "Upside Down Cake");
        }};

        if (versionNumber.endsWith("L")) {
            versionNumber = versionNumber.replace("L", "");
            if (versionMap.containsKey(versionNumber + "L")) {
                return versionMap.get(versionNumber + "L");
            }
        }

        if (versionMap.containsKey(versionNumber)) {
            return versionMap.get(versionNumber);
        }

        String[] parts = versionNumber.split("\\.");
        if (parts.length >= 2) {
            String majorVersion = parts[0] + "." + parts[1];
            if (versionMap.containsKey(majorVersion)) {
                return versionMap.get(majorVersion);
            }
        }

        return "Unknown";
    }

    public static String getFormattedAndroidVersion() {
        String versionNumber = Build.VERSION.RELEASE;
        String codename = getVersionCodename(versionNumber);

        // Handle special case for development builds
        if (!"REL".equals(Build.VERSION.CODENAME)) {
            codename = Build.VERSION.CODENAME;
        }
        return codename;
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

//code starts here <<<<<<<<<<<<<


//chipset segment
        TextView chipsetText = view.findViewById(R.id.chipset_cardview_text);
        String rawHardware = getChipsetInfo();
        String readableChipset = getReadableChipset(rawHardware);
        chipsetText.setText(readableChipset);


//device segment
        TextView manufacturerName = view.findViewById(R.id.manufacturer_name);
        TextView modelName = view.findViewById(R.id.model_name);
        manufacturerName.setText(Build.MANUFACTURER);
        modelName.setText(Build.MODEL);


//android version segment
        TextView osNumeber = view.findViewById(R.id.os_version_number);
        TextView osName = view.findViewById(R.id.os_version_name);
        osNumeber.setText("Android "+Build.VERSION.RELEASE);
        String codename = getFormattedAndroidVersion();
        osName.setText(codename);








        return view;
    }




}