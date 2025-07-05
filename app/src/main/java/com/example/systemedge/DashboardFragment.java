package com.example.systemedge;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.Build;
import android.os.Handler;
import android.os.BatteryManager;
import android.os.Looper;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Locale;
import java.util.regex.Pattern;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.TrafficStats;

import android.telephony.TelephonyManager;

import java.text.DecimalFormat;

import android.app.ActivityManager;

import org.w3c.dom.Text;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class DashboardFragment extends Fragment {

    private Handler ramUpdateHandler, cpuUpdateHandler, networkUpdateHandler;
    private BroadcastReceiver batteryInfoReceiver;
    private final int REFRESH_DELAY_MS = 1000;
    private long lastTxBytes = 0;
    private long lastRxBytes = 0;
    private long lastTime = 0;
    private final ArrayList<TextView> coreSpeedTextViews = new ArrayList<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    public DashboardFragment() {
        // field for constructors
    }

//User Defined Methods

    // chipset raw info
    private String getChipsetInfo() {
        String hardware = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Hardware")) {
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
            put("15", "Vanilla Ice Cream");
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

    public static String getCleanModelName() {
        String model = Build.MODEL;
        if (MODEL_MAPPINGS.containsKey(model)) {
            return MODEL_MAPPINGS.get(model);
        }
        return model;
    }

    //model polished info
    private static final Map<String, String> MODEL_MAPPINGS = new HashMap<String, String>() {{
        // Google Pixel Series
        put("G-2PW4100", "Pixel");
        put("G-2PW4200", "Pixel XL");
        put("G011A", "Pixel 2");
        put("G011C", "Pixel 2 XL");
        put("G013A", "Pixel 3");
        put("G013C", "Pixel 3 XL");
        put("G020I", "Pixel 3a");
        put("G020G", "Pixel 3a XL");
        put("G025J", "Pixel 4");
        put("G025M", "Pixel 4 XL");
        put("G025N", "Pixel 4a");
        put("G6QU3", "Pixel 4a 5G");
        put("GLU0G", "Pixel 5");
        put("GTT9Q", "Pixel 5a");
        put("GB17L", "Pixel 6");
        put("GB62Z", "Pixel 6 Pro");
        put("G1AZG", "Pixel 6a");
        put("GVU6C", "Pixel 7");
        put("GP4BC", "Pixel 7 Pro");
        put("GHL1X", "Pixel 7a");
        put("G1MNW", "Pixel 8");
        put("GC3VE", "Pixel 8 Pro");
        put("GX7AS", "Pixel 8a");
        put("G6VPR", "Pixel 9");
        put("G6V9P", "Pixel 9 Pro");
        put("G6V1P", "Pixel 9 Pro XL");

        // OnePlus Series
        put("A0001", "OnePlus One");
        put("ONE A2003", "OnePlus 2");
        put("ONE A3003", "OnePlus 3");
        put("ONEPLUS A5000", "OnePlus 5");
        put("ONEPLUS A6003", "OnePlus 6");
        put("GM1901", "OnePlus 7");
        put("GM1911", "OnePlus 7 Pro");
        put("HD1901", "OnePlus 7T");
        put("HD1911", "OnePlus 7T Pro");
        put("IN2013", "OnePlus 8");
        put("IN2023", "OnePlus 8 Pro");
        put("KB2003", "OnePlus 8T");
        put("LE2113", "OnePlus 9");
        put("LE2123", "OnePlus 9 Pro");
        put("MT2111", "OnePlus 9R");
        put("LE2125", "OnePlus 9RT");
        put("NE2213", "OnePlus 10 Pro");
        put("NE2215", "OnePlus 10T");
        put("CPH2449", "OnePlus 11");
        put("CPH2451", "OnePlus 11R");
        put("CPH2581", "OnePlus 12");
        put("CPH2583", "OnePlus 12R");
        put("CPH2611", "OnePlus 13");
        put("CPH2613", "OnePlus 13 Pro");
        put("CPH2615", "OnePlus 13T");
        put("AC2003", "OnePlus Nord");
        put("BE2011", "OnePlus Nord 2");
        put("NE2215", "OnePlus Nord 3");
        put("CPH2613", "OnePlus Nord 4");
        put("CPH2615", "OnePlus Nord 5");

        // Xiaomi/Redmi/POCO Series
        put("2013022", "Xiaomi Mi 1");
        put("2013023", "Xiaomi Mi 1S");
        put("2014011", "Xiaomi Mi 2");
        put("2014501", "Xiaomi Mi 3");
        put("2014215", "Xiaomi Mi 4");
        put("2015561", "Xiaomi Mi 5");
        put("2015711", "Xiaomi Mi 6");
        put("M1803E1A", "Xiaomi Mi 8");
        put("M1902F1G", "Xiaomi Mi 9");
        put("M2001J1E", "Xiaomi Mi 10");
        put("M2012K11G", "Xiaomi Mi 11");
        put("M2102K1G", "Xiaomi Mi 12");
        put("M2211133G", "Xiaomi 13");
        put("M2312W1SG", "Xiaomi 14");
        put("M2412W1SG", "Xiaomi 15 Ultra");
        put("HM2014011", "Redmi 1S");
        put("HM2014813", "Redmi 2");
        put("2014817", "Redmi 2 Prime");
        put("2015116", "Redmi 3");
        put("2016030", "Redmi 3S");
        put("2016090", "Redmi 4");
        put("MZB07DEDIN", "Redmi 4A");
        put("MDE40", "Redmi 5");
        put("MEE7S", "Redmi 6");
        put("M1901F7G", "Redmi 7");
        put("M1908C3IG", "Redmi 8");
        put("M2006C3MG", "Redmi 9");
        put("M2101K7AG", "Redmi 10");
        put("2201117TG", "Redmi 11");
        put("23021RAA2G", "Redmi 12");
        put("23129RAA4G", "Redmi 13C");
        put("2201116PG", "Redmi Note 11");
        put("2201117TG", "Redmi Note 12");
        put("2201117PG", "Redmi Note 12 Pro");
        put("2201117PG", "Redmi Note 12 Pro+");
        put("2201117PG", "Redmi Note 12 Pro 5G");
        put("2201117PG", "Redmi Note 12 Pro 4G");
        put("23113RKC6C", "Redmi k70");
        put("POCO F1", "POCO F1");
        put("MZB07Z0IN", "POCO F2");
        put("M2004J19PI", "POCO X2");
        put("M2010J19SG", "POCO X3");
        put("21061110AG", "POCO X4");
        put("2201116PG", "POCO X5");
        put("22101320G", "POCO X6");
        put("23013PC75G", "POCO X7 Pro");

        // Motorola Series
        put("DynaTAC 8000X", "DynaTAC 8000X");
        put("MicroTAC 9800X", "MicroTAC 9800X");
        put("StarTAC 130", "StarTAC 130");
        put("RAZR V3", "RAZR V3");
        put("XT2403", "Razr 40");
        put("A1200", "Ming");
        put("XT800", "Droid");
        put("XT1033", "Moto G");
        put("XT1092", "Moto X");
        put("XT1572", "Moto X Style");
        put("XT1650", "Moto Z");
        put("XT1925", "Moto G6");
        put("XT2041", "Moto G Power");
        put("XT2063", "Moto G Stylus");
        put("XT2201", "Edge 30");
        put("XT2301", "Edge 40");
        put("XT2403", "Edge 50");
        put("XT2501", "Edge 60");
        put("XT2335", "G84");
        put("XT2321", "G73");

        // Sony Xperia Series
        put("T68i", "Ericsson T68");
        put("K700i", "K700");
        put("W800i", "Walkman W800");
        put("K800i", "Cyber-shot K800");
        put("C902", "C902");
        put("X10i", "Xperia X10");
        put("LT15i", "Xperia Arc");
        put("LT26i", "Xperia S");
        put("C6603", "Xperia Z");
        put("D6503", "Xperia Z2");
        put("E6653", "Xperia Z5");
        put("F8331", "Xperia XZ");
        put("G8141", "Xperia XZ1");
        put("H8116", "Xperia 1");
        put("J9110", "Xperia 1 II");
        put("XQ-BC52", "Xperia 1 III");
        put("XQ-BC72", "Xperia 1 IV");
        put("XQ-DQ54", "Xperia 1 V");
        put("XQ-DQ72", "Xperia 1 VI");

        // Oppo Series
        put("A103", "Smile Phone A103");
        put("CPH2359", "A78");
        put("CPH2203", "A96");
        put("F1", "F1");
        put("F1s", "F1s");
        put("F3", "F3");
        put("F5", "F5");
        put("F7", "F7");
        put("F9", "F9 Pro");
        put("F11", "F11 Pro");
        put("F15", "F15");
        put("F17", "F17 Pro");
        put("CPH2487", "F23");
        put("Reno", "Reno");
        put("CPH1919", "Reno 2");
        put("CPH2023", "Reno 3 Pro");
        put("CPH2179", "Reno 5");
        put("CPH2203", "Reno 6");
        put("CPH2385", "Reno 8");
        put("CPH2459", "Reno 10");
        put("CPH2531", "K13x 5G");
        put("CPH2525", "Find X6");

        // Nokia Series
        put("TA-1394", "G60");
        put("TA-1478", "X30");
        put("TA-1452", "C32");
        put("TA-1528", "G42");
        put("TA-1398", "XR21");

        // Infinix Series
        put("X6827", "Note 30");
        put("X6716", "Hot 30");
        put("X695C", "Zero 30");
        put("X6812", "Smart 7");

        // Tecno Series
        put("TECNO-LG8n", "Pova 5");
        put("TECNO-LH8n", "Camon 20");
        put("TECNO-LG7n", "Spark 10");
        put("TECNO-LH7n", "Phantom X2");

        // Itel Series
        put("L606", "A48");
        put("P37", "P37");
        put("P38", "P38");
        put("S11", "S11");
        put("S12", "S12");
        put("S13", "S13");
        put("S15", "S15");
        put("S16", "S16");
        put("S21", "S21");
        put("S23", "S23");
        put("S24", "S24");

        // Realme Series
        put("RMX1801", "Realme 1");
        put("RMX1803", "Realme 2");
        put("RMX1821", "Realme 3");
        put("RMX1971", "Realme 5");
        put("RMX1991", "Realme X");
        put("RMX2071", "Realme X50");
        put("RMX2176", "Realme 7 Pro");
        put("RMX3081", "Realme 8");
        put("RMX3471", "Realme 11");
        put("RMX3663", "Realme GT 6");
        put("RMX3706", "Realme GT 7");
        put("RMX3708", "Realme GT 7T");
        put("RMX3933", "Note 60");
        put("RMX3081", "Narzo 30");

        // Honor Series
        put("H30-L01", "Honor 3C");
        put("H60-L04", "Honor 6");
        put("PLK-L01", "Honor 7");
        put("FRD-L09", "Honor 8");
        put("STF-L09", "Honor 9");
        put("COL-L29", "Honor 10");
        put("YAL-L21", "Honor 20");
        put("OXF-L29", "Honor 30");
        put("NTH-NX9", "Honor 50");
        put("FNE-NX9", "Honor 70");
        put("PGT-NX9", "Honor 90");
        put("MAA-NX9", "Honor 100");
        put("ADA-NX9", "Honor 200");
        put("BBA-NX9", "Honor 300");
        put("CCA-NX9", "Honor 400 Pro");

        // Vivo Series
        put("X1", "Vivo X1");
        put("X3", "Vivo X3");
        put("X5", "Vivo X5");
        put("X6", "Vivo X6");
        put("X7", "Vivo X7");
        put("X9", "Vivo X9");
        put("X20", "Vivo X20");
        put("X21", "Vivo X21");
        put("X27", "Vivo X27");
        put("X30", "Vivo X30");
        put("X50", "Vivo X50");
        put("X60", "Vivo X60");
        put("X70", "Vivo X70");
        put("V2218", "X80");
        put("V2246", "X90");
        put("X100", "Vivo X100");
        put("V2204", "Y22");
        put("V2219", "Y35");
        put("V50", "Vivo V50");
        put("V2166", "Y55");
        put("V2130", "V23");
        put("V2154", "V25");
        put("V2164", "V27");

        // Symphony Series
        put("C5", "Roar C5");
        put("E10", "E10");
        put("H100", "H100");
        put("H250", "H250");
        put("H400", "H400");
        put("i10", "i10");
        put("i20", "i20");
        put("ZVII", "ZVII");
        put("ZVIII", "ZVIII");
        put("ZIX", "ZIX");
        put("Max 10", "Max 10");
        put("G27 Lite", "G27 Lite");

        // Walton Series
        put("GH1", "Primo GH1");
        put("GH2", "Primo GH2");
        put("GH3", "Primo GH3");
        put("GH4", "Primo GH4");
        put("GH5", "Primo GH5");
        put("GH6", "Primo GH6");
        put("GH7", "Primo GH7");
        put("GH8", "Primo GH8");
        put("GH9", "Primo GH9");
        put("XANON X90", "XANON X90");
        put("XANON X91", "XANON X91");

        //Samsung Series
        put("SM-J100H", "Galaxy J1 (2015)");
        put("SM-J110H", "Galaxy J1 Ace");
        put("SM-J200G", "Galaxy J2");
        put("SM-J330F", "Galaxy J3 (2017)");
        put("SM-J400F", "Galaxy J4");
        put("SM-J500F", "Galaxy J5");
        put("SM-J600G", "Galaxy J6");
        put("SM-J700F", "Galaxy J7");
        put("SM-J810G", "Galaxy J8");
        put("SM-A300H", "Galaxy A3 (2015)");
        put("SM-A500F", "Galaxy A5 (2015)");
        put("SM-A720F", "Galaxy A7 (2017)");
        put("SM-A505F", "Galaxy A50");
        put("SM-A715F", "Galaxy A71");
        put("SM-A325F", "Galaxy A32");
        put("SM-A356E", "Galaxy A35");
        put("SM-A346E", "Galaxy A34");
        put("SM-A536E", "Galaxy A53");
        put("SM-A556E", "Galaxy A55");
        put("SM-A568E", "Galaxy A56");
        put("GT-I9000", "Galaxy S");
        put("GT-I9100", "Galaxy S II");
        put("GT-I9300", "Galaxy S III");
        put("SM-G900F", "Galaxy S5");
        put("SM-G950F", "Galaxy S8");
        put("SM-G975F", "Galaxy S10+");
        put("SM-G998B", "Galaxy S21 Ultra");
        put("SM-S918B", "Galaxy S23 Ultra");
        put("SM-S928U", "Galaxy S25 Ultra");
        put("SM-S928B", "Galaxy S25+");
        put("SM-S928N", "Galaxy S25 (Korea)");
        put("SM-F900F", "Galaxy Fold (1st Gen)");
        put("SM-F916B", "Galaxy Z Fold2");
        put("SM-F926B", "Galaxy Z Fold3");
        put("SM-F936B", "Galaxy Z Fold4");
        put("SM-F946B", "Galaxy Z Fold5");
        put("SM-F956U", "Galaxy Z Fold6");
        put("SM-F700F", "Galaxy Z Flip");
        put("SM-F721B", "Galaxy Z Flip4");
        put("SM-F731B", "Galaxy Z Flip5");
        put("SM-F741U", "Galaxy Z Flip6");

    }};


    //for capitalization of first letter
    public static String capitalizeFirstLetterOnly(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    //Byte to Gigabyte conversion
    private String formatSize(long size) {
        final double BYTES_IN_GB = 1024.0 * 1024.0 * 1024.0;
        DecimalFormat df = new DecimalFormat("#.#"); // One decimal place for a cleaner look
        return df.format(size / BYTES_IN_GB) + " GB";
    }

    // get cpu core numbers
    private int getNumberOfCores() {
        try {
            // Get the list of files in the CPU directory
            File[] files = new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    // Check if the file name matches the pattern "cpu[0-9]+"
                    return Pattern.matches("cpu[0-9]+", pathname.getName());
                }
            });
            // Return the number of files found, or 1 as a fallback
            return files != null ? files.length : 1;
        } catch (Exception e) {
            // Fallback to 1 if there's an error
            return 1;
        }
    }


    //read the core frequency
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


    //read cpu temperature
    private float getCpuTemperature() {
        String[] tempPaths = {
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/class/thermal/thermal_zone1/temp",
                // Add other common paths if needed
        };
        for (String path : tempPaths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                float temp = Float.parseFloat(reader.readLine());
                // The value is often in millidegrees Celsius, so divide by 1000
                return temp / 1000.0f;
            } catch (Exception e) {
                // Ignore and try the next path
            }
        }
        return -1F; // Return -1 if no path was found
    }

    //cpu runnable
    private final Runnable cpuUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // Make sure the fragment view is still available
            if (getView() == null) return;

            // Update Core Speeds on the UI
            for (int i = 0; i < coreSpeedTextViews.size(); i++) {
                int currentFreq = readCoreFrequency(i);
                TextView coreTextView = coreSpeedTextViews.get(i);
                if (currentFreq != -1) {
                    coreTextView.setText(currentFreq + " MHz");
                } else {
                    coreTextView.setText("Offline");
                }
            }

            // Update CPU Temperature on the UI
            TextView cpuTemp = getView().findViewById(R.id.cpu_temp_value);
            float temp = getCpuTemperature();
            if (temp != -1F) {
                cpuTemp.setText(String.format(Locale.US, "CPU Temperature: %.1f °C", temp));
            } else {
                cpuTemp.setText("unknown");
            }

            // Schedule the next update
            if (cpuUpdateHandler != null) {
                cpuUpdateHandler.postDelayed(this, REFRESH_DELAY_MS);
            }
        }
    };


    //network available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // get wifi or mobile data info
    private String getNetworkSource() {
        //ImageView wifiIcon = getView().findViewById(R.id.wifi_icon);
        //ImageView dataIcon = getView().findViewById(R.id.data_icon);
        ImageView connectIcon = getView().findViewById(R.id.connection_icon);

        if (getContext() == null) {
            return "N/A";
        }
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return "N/A";

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //wifiIcon.setVisibility(View.VISIBLE);
                //dataIcon.setVisibility(View.GONE);
                //disconnectIcon.setVisibility(View.GONE);
                connectIcon.setImageResource(R.drawable.wifi);

                final WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null && wifiInfo.getSSID() != null && !wifiInfo.getSSID().isEmpty()) {

                        if (wifiInfo.getSSID().contains("<unknown ssid>")) {
                            return "Turn on Location";
                        } else {
                            // The SSID is often returned with surrounding quotes, so we remove them.
                            return wifiInfo.getSSID().replace("\"", "");
                        }
                    }
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //wifiIcon.setVisibility(View.GONE);
                //dataIcon.setVisibility(View.VISIBLE);
                //disconnectIcon.setVisibility(View.GONE);
                connectIcon.setImageResource(R.drawable.mobile_data);

                TelephonyManager tm = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
                // Return the network operator name (e.g., "T-Mobile")
                if (tm != null) return tm.getNetworkOperatorName();
            }
        }
        return "N/A";
    }

    //format internet speed
    private String formatSpeed(double speedInBytes) {
        if (speedInBytes < 1024) {
            return String.format(Locale.US, "%.1f B/s", speedInBytes);
        } else if (speedInBytes < 1024 * 1024) {
            return String.format(Locale.US, "%.1f KB/s", speedInBytes / 1024);
        } else {
            return String.format(Locale.US, "%.1f MB/s", speedInBytes / (1024 * 1024));
        }
    }

    //network update runnable
    private final Runnable networkUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (getView() == null || getContext() == null) return;

            // Find the TextViews
            TextView networkStatus = getView().findViewById(R.id.network_status);
            TextView networkSource = getView().findViewById(R.id.network_source);
            TextView pingValue = getView().findViewById(R.id.ping);
            TextView netSpeed = getView().findViewById(R.id.net_speed);
            //ImageView wifiIcon = getView().findViewById(R.id.wifi_icon);
            //ImageView dataIcon = getView().findViewById(R.id.data_icon);
            ImageView connectIcon = getView().findViewById(R.id.connection_icon);

            // 1. Update Network Status and Source
            if (isNetworkAvailable()) {
                networkStatus.setText("Connected");
                networkSource.setText(getNetworkSource());

                // 2. Calculate Network Speed
                long currentTxBytes = TrafficStats.getTotalTxBytes();
                long currentRxBytes = TrafficStats.getTotalRxBytes();
                long currentTime = System.currentTimeMillis();

                if (lastTime > 0) {
                    long timeDelta = currentTime - lastTime;
                    long bytesDelta = (currentTxBytes - lastTxBytes) + (currentRxBytes - lastRxBytes);
                    if (timeDelta > 0) {
                        double speed = (double) bytesDelta * 1000 / timeDelta;
                        netSpeed.setText(formatSpeed(speed));
                    }
                }
                lastTxBytes = currentTxBytes;
                lastRxBytes = currentRxBytes;
                lastTime = currentTime;

                // 3. Get Ping in a background thread to avoid blocking the UI
                new Thread(() -> {
                    String pingResult = "N/A";
                    try {
                        String command = "ping -c 1 8.8.8.8"; // Ping Google's DNS once
                        Process process = Runtime.getRuntime().exec(command);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("time=")) {
                                String[] parts = line.split("time=");
                                if (parts.length > 1) {
                                    pingResult = parts[1].split(" ")[0] + " ms";
                                }
                                break;
                            }
                        }
                        reader.close();
                    } catch (IOException e) {
                        pingResult = "Error";
                    }

                    final String finalPingResult = pingResult;
                    // Update UI on the main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> pingValue.setText(finalPingResult));
                    }
                }).start();

            } else {
                networkStatus.setText("Disconnected");
                networkSource.setText("N/A");
                pingValue.setText("N/A");
                netSpeed.setText("0.0 B/s");
                //wifiIcon.setVisibility(View.GONE);
                //dataIcon.setVisibility(View.GONE);
                //disconnectIcon.setVisibility(View.VISIBLE);
                connectIcon.setImageResource(R.drawable.disconnect);
            }

            // Schedule the next update
            if (networkUpdateHandler != null) {
                networkUpdateHandler.postDelayed(this, REFRESH_DELAY_MS);
            }
        }
    };


    // for location
    private void checkAndRequestLocationPermission() {
        if (getContext() != null &&
                androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, so request it
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                android.widget.Toast.makeText(getContext(), "Location Permission Granted", android.widget.Toast.LENGTH_SHORT).show();

                if (networkUpdateHandler != null) {
                    // Remove any pending updates to avoid running twice in quick succession
                    networkUpdateHandler.removeCallbacks(networkUpdateRunnable);
                    // Post the runnable to run now
                    networkUpdateHandler.post(networkUpdateRunnable);
                }

            } else {
                android.widget.Toast.makeText(getContext(), "Location Permission is required to show Network Info.", android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }




    //onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

//code starts here <<<<<<<<<<<<<

//location permission
        checkAndRequestLocationPermission();


//chipset segment
        TextView chipsetText = view.findViewById(R.id.chipset_cardview_text);

        String rawHardware = getChipsetInfo();
        String readableChipset = getReadableChipset(rawHardware);
        chipsetText.setText(readableChipset);


//device segment
        TextView manufacturerName = view.findViewById(R.id.manufacturer_name);
        TextView modelName = view.findViewById(R.id.model_name);

        String brand_name = capitalizeFirstLetterOnly(Build.MANUFACTURER);
        manufacturerName.setText(brand_name);
        String mod_name = getCleanModelName();
        modelName.setText(mod_name);


//android version segment
        TextView osNumeber = view.findViewById(R.id.os_version_number);
        TextView osName = view.findViewById(R.id.os_version_name);

        osNumeber.setText("Android " + Build.VERSION.RELEASE);
        String codename = getFormattedAndroidVersion();
        osName.setText(codename);


//circuler progress view and live ram usage graph
        CircularProgressView ramCircularProgress = view.findViewById(R.id.ram_usage_progrssbar);
        RamLineGraphView ramLineGraphView = view.findViewById(R.id.ramLineGraph);
        TextView graphUsedRam = view.findViewById(R.id.used_ram);
        TextView graphFreeRam = view.findViewById(R.id.free_ram);
        TextView progressText = view.findViewById(R.id.progress_percent);
        TextView totalRam = view.findViewById(R.id.ram_total);

        ramUpdateHandler = new Handler();
        ramUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
                if (activityManager != null) {
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);

                    long total_ram_mb = memoryInfo.totalMem / (1024 * 1024);
                    long used_ram_mb = (memoryInfo.totalMem - memoryInfo.availMem) / (1024 * 1024);
                    int ramPercentage = (int) ((used_ram_mb * 100) / total_ram_mb);
                    ramCircularProgress.setProgress(ramPercentage);
                    progressText.setText(ramPercentage + "%");
                    totalRam.setText(total_ram_mb + " MB RAM Total");

                    ramLineGraphView.updateRamData(used_ram_mb);
                    graphUsedRam.setText(used_ram_mb + " MB Used");
                    graphFreeRam.setText((total_ram_mb - used_ram_mb) + " MB Free");
                }
                ramUpdateHandler.postDelayed(this, REFRESH_DELAY_MS);
            }
        }, REFRESH_DELAY_MS);


//Storage Information
        TextView usedStorage = view.findViewById(R.id.used_storage);
        TextView totalStorage = view.findViewById(R.id.total_storage);
        TextView storagePercentage = view.findViewById(R.id.storage_percentage);
        TextView warningStorage = view.findViewById(R.id.warning_storage);
        ProgressBar storageProgressBar = view.findViewById(R.id.storage_progress_bar);

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        long totalSize = totalBlocks * blockSize;
        long freeSize = availableBlocks * blockSize;
        long usedSize = totalSize - freeSize;
        int usedPercentage = (int) ((usedSize * 100) / totalSize);

        usedStorage.setText("Used: " + formatSize(usedSize));
        totalStorage.setText("Total: " + formatSize(totalSize));
        storagePercentage.setText(usedPercentage + "%");
        storageProgressBar.setProgress(usedPercentage);

        if (usedPercentage >= 90f) { // Threshold of 45°C
            warningStorage.setVisibility(View.VISIBLE);
        } else {
            warningStorage.setVisibility(View.GONE);
        }




//battery Information
        TextView batteryState = view.findViewById(R.id.battery_state);
        TextView batteryVoltage = view.findViewById(R.id.voltage_battery);
        TextView batteryTemperature = view.findViewById(R.id.temp_battery);
        TextView batteryPercentage = view.findViewById(R.id.battery_percentage);
        TextView warningBattery = view.findViewById(R.id.warning_battery);
        ProgressBar batteryProgressBar = view.findViewById(R.id.battery_progress_bar);

        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get battery level
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int batteryPct = (scale > 0) ? (int) ((level / (float) scale) * 100) : 0;

                // Get battery status
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String statusString = "Unknown";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = "Discharging";
                        break;
                }

                // Get battery voltage
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                float voltageFloat = (float) voltage / 1000.0f;

                // Get battery temperature
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                float tempFloat = (float) temperature / 10.0f;

                // Add null checks to be safe in case the view is being destroyed
                if(batteryPercentage != null) batteryPercentage.setText(String.format(Locale.US, "%d%%", batteryPct));
                if(batteryProgressBar != null) batteryProgressBar.setProgress(batteryPct);
                if(batteryState != null) batteryState.setText(String.format("Battery (%s)", statusString));
                if(batteryVoltage != null) batteryVoltage.setText(String.format(Locale.US, "Voltage: %.2f V", voltageFloat));
                if(batteryTemperature != null) batteryTemperature.setText(String.format(Locale.US, "Temperature: %.1f °C", tempFloat));

                if (warningBattery != null) {
                    if (tempFloat >= 45.0f) { // Threshold of 45°C
                        warningBattery.setVisibility(View.VISIBLE);
                    } else {
                        warningBattery.setVisibility(View.GONE);
                    }
                }
            }
        };
        // Register the receiver to start listening for battery updates
        requireActivity().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));




//cpu core speed segment
            GridLayout coreGrid = view.findViewById(R.id.cpu_core_grid);
            int coreCount = getNumberOfCores();

            // Ensure the grid is empty before adding new views
            coreGrid.removeAllViews();
            coreSpeedTextViews.clear();

            for (int i = 0; i < coreCount; i++) {
                // Inflate our corrected layout file
                View coreView = getLayoutInflater().inflate(R.layout.item_core_speed, coreGrid, false);

                TextView coreTitle = coreView.findViewById(R.id.core_title);
                TextView coreSpeed = coreView.findViewById(R.id.core_speed);

                coreTitle.setText("Core " + (i + 1));
                coreSpeed.setText("... MHz");

                // Add the speed TextView to our list for live updates
                coreSpeedTextViews.add(coreSpeed);
                // Add the finished core view to the grid
                coreGrid.addView(coreView);
            }

            // Initialize the handler for live updates
            cpuUpdateHandler = new Handler(Looper.getMainLooper());



//sensor count
        TextView sensorCount = view.findViewById(R.id.sensor_count);
        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensorCount.setText(String.valueOf(sensorList.size()));


//app count
        TextView appCount = view.findViewById(R.id.app_count);
        PackageManager packageManager = requireActivity().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        appCount.setText(String.valueOf(appList.size()));



 //network segment
        networkUpdateHandler = new Handler(Looper.getMainLooper());


//drawer buttons setup
        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        FloatingActionButton settingsButton = view.findViewById(R.id.settings_button);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);

        settingsButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_settings) {

            } else if (id == R.id.nav_about) {

            } else if (id == R.id.nav_rate_us) {

            }

            // Close the drawer after an item is tapped

            return true;
        });










        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start the handler when the fragment becomes visible
        if (cpuUpdateHandler != null) {
            cpuUpdateHandler.post(cpuUpdateRunnable);
        }
        if (networkUpdateHandler != null) {
            networkUpdateHandler.post(networkUpdateRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop the handler when the fragment is no longer visible
        if (cpuUpdateHandler != null) {
            cpuUpdateHandler.removeCallbacks(cpuUpdateRunnable);
        }
        if (networkUpdateHandler != null) {
            networkUpdateHandler.removeCallbacks(networkUpdateRunnable);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop the CPU updater to prevent leaks
        if (cpuUpdateHandler != null) {
            cpuUpdateHandler.removeCallbacksAndMessages(null);
            cpuUpdateHandler = null;
        }
        // Stop the RAM updater to prevent leaks
        if (ramUpdateHandler != null) {
            ramUpdateHandler.removeCallbacksAndMessages(null);
            ramUpdateHandler = null;
        }
        // Unregister the battery receiver to prevent leaks
        if (batteryInfoReceiver != null) {
            requireActivity().unregisterReceiver(batteryInfoReceiver);
            batteryInfoReceiver = null;
        }
        //stop the network updater to prevent leaks
        if (networkUpdateHandler != null) {
            networkUpdateHandler.removeCallbacksAndMessages(null);
            networkUpdateHandler = null;
        }
    }

}