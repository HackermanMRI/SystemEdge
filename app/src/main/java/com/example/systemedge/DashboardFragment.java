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


//for capitalization of first letter
    public static String capitalizeFirstLetterOnly(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }







//oncreateView method
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
        String brand_name = capitalizeFirstLetterOnly(Build.BRAND);
        manufacturerName.setText(brand_name);
        String mod_name = getCleanModelName();
        modelName.setText(mod_name);


//android version segment
        TextView osNumeber = view.findViewById(R.id.os_version_number);
        TextView osName = view.findViewById(R.id.os_version_name);
        osNumeber.setText("Android " + Build.VERSION.RELEASE);
        String codename = getFormattedAndroidVersion();
        osName.setText(codename);








        return view;
    }



    // model name infos
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


}