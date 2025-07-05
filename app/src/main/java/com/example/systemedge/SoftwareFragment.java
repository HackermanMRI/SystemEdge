package com.example.systemedge;

import com.example.systemedge.R;

import android.os.Bundle;
import android.os.Build;
import android.os.SystemClock;

import android.net.Uri;

import android.database.Cursor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.*;

import android.telephony.TelephonyManager;

import android.content.pm.ConfigurationInfo;
import android.content.ContentResolver;
import android.content.Context;

import android.provider.Settings;

import androidx.fragment.app.Fragment;

import android.app.ActivityManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;

import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.UUID;

import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;

import java.net.NetworkInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;

import java.security.Provider;
import java.security.Security;









public class SoftwareFragment extends Fragment {

    //declarations
    private LinearLayout containerCard1, containerCard2, containerCard3;
    private LayoutInflater inflater;
    private final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);


    //methods here
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

    private String getAndroidVersionReleaseDate() {
        int sdkVersion = Build.VERSION.SDK_INT;
        switch (sdkVersion) {
            case Build.VERSION_CODES.UPSIDE_DOWN_CAKE: // API 34
                return "October 4, 2023";
            case Build.VERSION_CODES.TIRAMISU: // API 33
                return "August 15, 2022";
            case Build.VERSION_CODES.S_V2: // API 32
                return "March 7, 2022";
            case Build.VERSION_CODES.S: // API 31
                return "October 4, 2021";
            case Build.VERSION_CODES.R: // API 30
                return "September 8, 2020";
            case Build.VERSION_CODES.Q: // API 29
                return "September 3, 2019";
            case Build.VERSION_CODES.P: // API 28
                return "August 6, 2018";
            case Build.VERSION_CODES.O_MR1: // API 27
                return "December 5, 2017";
            case Build.VERSION_CODES.O: // API 26
                return "August 21, 2017";
            case Build.VERSION_CODES.N_MR1: // API 25
                return "October 4, 2016";
            case Build.VERSION_CODES.N: // API 24
                return "August 22, 2016";
            case Build.VERSION_CODES.M: // API 23
                return "October 5, 2015";
            case Build.VERSION_CODES.LOLLIPOP_MR1: // API 22
                return "March 9, 2015";
            case Build.VERSION_CODES.LOLLIPOP: // API 21
                return "November 12, 2014";
            case Build.VERSION_CODES.KITKAT_WATCH: // API 20
                return "June 25, 2014";
            case Build.VERSION_CODES.KITKAT: // API 19
                return "October 31, 2013";
            case Build.VERSION_CODES.JELLY_BEAN_MR2: // API 18
                return "July 24, 2013";
            case Build.VERSION_CODES.JELLY_BEAN_MR1: // API 17
                return "November 13, 2012";
            case Build.VERSION_CODES.JELLY_BEAN: // API 16
                return "July 9, 2012";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1: // API 15
                return "December 16, 2011";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH: // API 14
                return "October 18, 2011";
            case Build.VERSION_CODES.HONEYCOMB_MR2: // API 13
                return "July 15, 2011";
            case Build.VERSION_CODES.HONEYCOMB_MR1: // API 12
                return "May 10, 2011";
            case Build.VERSION_CODES.HONEYCOMB: // API 11
                return "February 22, 2011";
            case Build.VERSION_CODES.GINGERBREAD_MR1: // API 10
                return "February 9, 2011";
            case Build.VERSION_CODES.GINGERBREAD: // API 9
                return "December 6, 2010";
            case Build.VERSION_CODES.FROYO: // API 8
                return "May 20, 2010";
            case Build.VERSION_CODES.ECLAIR_MR1: // API 7
                return "December 3, 2009";
            case Build.VERSION_CODES.ECLAIR_0_1: // API 6
                return "October 26, 2009";
            case Build.VERSION_CODES.ECLAIR: // API 5
                return "October 26, 2009";
            case Build.VERSION_CODES.DONUT: // API 4
                return "September 15, 2009";
            case Build.VERSION_CODES.CUPCAKE: // API 3
                return "April 27, 2009";
            // Note: There are no specific Build.VERSION_CODES constants for API 1 and 2 in the SDK.
            // case 2: return "February 9, 2009"; // Android 1.1
            // case 1: return "September 23, 2008"; // Android 1.0
            default:
                return "N/A";
        }
    }


    private String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + key);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = input.readLine();
            if (line != null && !line.isEmpty()) {
                value = line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    private boolean isRooted() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_software, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //code starts here
        containerCard1 = view.findViewById(R.id.container_card_1);
        containerCard2 = view.findViewById(R.id.container_card_2);
        containerCard3 = view.findViewById(R.id.container_card_3);

        TextView textView1 = view.findViewById(R.id.text_android_version);
        TextView textView2 = view.findViewById(R.id.text_android_version_name);
        TextView textView3 = view.findViewById(R.id.text_version_release_date);

        String version = Build.VERSION.RELEASE;
        textView1.setText("ANDROID " + version);
        String osname = getVersionCodename(version);
        textView2.setText(osname.toUpperCase());
        textView3.setText("Release Date: " + getAndroidVersionReleaseDate());


//card 1

        //android version
        addRowToContainer(containerCard1, "Version name", "Android " + version + " " + osname);

        //api level
        addRowToContainer(containerCard1, "API Level", String.valueOf(Build.VERSION.SDK_INT));


        //build time
        long time = Build.TIME;
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy  hh:mm:ss a", Locale.getDefault());
        addRowToContainer(containerCard1, "Build Time", sdf.format(date));

        //build id
        addRowToContainer(containerCard1, "Build ID", Build.ID);

        //security patch level
        addRowToContainer(containerCard1, "Security Patch Level", Build.VERSION.SECURITY_PATCH);

        //bootloader
        addRowToContainer(containerCard1, "Bootloader Version", Build.BOOTLOADER);

        //baseband
        addRowToContainer(containerCard1, "Baseband", Build.getRadioVersion());

        //language
        addRowToContainer(containerCard1, "Language", Locale.getDefault().getDisplayLanguage());

        //timezone
        addRowToContainer(containerCard1, "Time-Zone", TimeZone.getDefault().getID());

        //root access
        addRowToContainer(containerCard1, "Root Access", isRooted() ? "Yes" : "No");

        //system update
        long uptimeMillis = SystemClock.elapsedRealtime();
        String uptimeString = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(uptimeMillis),
                TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % TimeUnit.MINUTES.toSeconds(1));
        addRowToContainer(containerCard1, "System Uptime", uptimeString);

        //system as root
        addRowToContainer(containerCard1, "System-as-Root", getSystemProperty("ro.build.system_root_image", "false").equals("true") ? "Supported" : "Not Supported");

        // seamless updates
        addRowToContainer(containerCard1, "Seamless Updates", getSystemProperty("ro.build.ab_update", "false").equals("true") ? "Supported" : "Not Supported");

        // daynamic partitions
        addRowToContainer(containerCard1, "Dynamic Partitions", getSystemProperty("ro.boot.dynamic_partitions", "false").equals("true") ? "Supported" : "Not Supported");

        //project treble
        addRowToContainer(containerCard1, "Project Treble", getSystemProperty("ro.treble.enabled", "false").equals("true") ? "Supported" : "Not Supported");


//card 2

        //java runtime
        String javaRuntimeName = System.getProperty("java.runtime.name");
        String javaRuntimeVersion = System.getProperty("java.runtime.version");
        addRowToContainer(containerCard2, "Java Runtime", javaRuntimeName + " " + javaRuntimeVersion);

        //java VN
        String javaVmName = System.getProperty("java.vm.name");
        String javaVmVersion = System.getProperty("java.vm.version");
        addRowToContainer(containerCard2, "Java VM", javaVmName + " " + javaVmVersion);

        //java vm stacksize
        long maxMemoryBytes = Runtime.getRuntime().maxMemory();
        long vmHeapSizeMb = (maxMemoryBytes == Long.MAX_VALUE) ? 0 : maxMemoryBytes / (1024 * 1024);
        addRowToContainer(containerCard2, "Java VM Heap Size", vmHeapSizeMb + " MB");

        //kernel architechture
        addRowToContainer(containerCard2, "Kernel Architecture", System.getProperty("os.arch"));

        //kernel version
        addRowToContainer(containerCard2, "Kernel Version", System.getProperty("os.version"));

        //opengl es
        ActivityManager activityManager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            String glEsVersion = activityManager.getDeviceConfigurationInfo().getGlEsVersion();
            addRowToContainer(containerCard2, "OpenGL ES", glEsVersion);
        }

        //selinux
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            String seLinuxStatus;
            if (!SELinux.isSELinuxEnabled()) {
                seLinuxStatus = "Disabled";
            } else if (SELinux.isSELinuxEnforced()) {
                seLinuxStatus = "Enforcing";
            } else {
                seLinuxStatus = "Permissive";
            }
            addRowToContainer(containerCard2, "SELinux", seLinuxStatus);
        } else {
            // For devices older than API 17
            addRowToContainer(containerCard2, "SELinux", "N/A (Unsupported API)");
        }*/
        addRowToContainer(containerCard2, "SELinux", "Unknown");

        //opensl version
        String openSslInfo = "Unable to determine";
        try {
            Provider provider = Security.getProvider("Conscrypt");
            if (provider != null) {
                openSslInfo = provider.getName() + " " + provider.getVersion();
            }
        } catch (Exception e) {
            // Ignored
        }
        addRowToContainer(containerCard2, "OpenSSL Version", openSslInfo);



// card 3

        MediaDrm mediaDrm = null;


        try {
            mediaDrm = new MediaDrm(WIDEVINE_UUID);

        // vendor
        addRowToContainer(containerCard3, "DRM Vendor", mediaDrm.getPropertyString(MediaDrm.PROPERTY_VENDOR));

        //version
        addRowToContainer(containerCard3, "DRM Version", mediaDrm.getPropertyString(MediaDrm.PROPERTY_VERSION));

        //Description
            addRowToContainer(containerCard3, "Description", mediaDrm.getPropertyString(MediaDrm.PROPERTY_DESCRIPTION));

        //algorithm
            addRowToContainer(containerCard3, "Algorithms", mediaDrm.getPropertyString(MediaDrm.PROPERTY_ALGORITHMS));

        //security level
            addRowToContainer(containerCard3, "Security Level", mediaDrm.getPropertyString("securityLevel"));

        //system id
            addRowToContainer(containerCard3, "System ID", mediaDrm.getPropertyString("systemId"));

        // hcdp level
            addRowToContainer(containerCard3, "HDCP Level", mediaDrm.getPropertyString("hdcpLevel"));

        // max hcdp level
            addRowToContainer(containerCard3, "Max HDCP Level", mediaDrm.getPropertyString("maxHdcpLevel"));

        //usage reporting support
            addRowToContainer(containerCard3, "Usage Reporting", mediaDrm.getPropertyString("usageReportingSupport"));

        //max number of sessions
            addRowToContainer(containerCard3, "Max Sessions", mediaDrm.getPropertyString("maxNumberOfSessions"));

        //open sessions
            addRowToContainer(containerCard3, "Open Sessions", mediaDrm.getPropertyString("numberOfOpenSessions"));

        } catch (UnsupportedSchemeException e) {
            // This device does not support Widevine DRM
            addRowToContainer(containerCard3, "DRM Status", "Widevine not supported");
        } finally {
            if (mediaDrm != null) {
                // It's crucial to release the MediaDrm instance to free up resources.
                mediaDrm.release();
            }
        }


    }
}