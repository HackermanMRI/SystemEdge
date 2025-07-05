package com.example.systemedge;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Display;
import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Arrays;

import android.util.DisplayMetrics;
import android.provider.Settings;


public class DisplayFragment extends Fragment {

    //declarations
    private LinearLayout containerCard1;
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
        return inflater.inflate(R.layout.fragment_display, container, false);
    }

    @Override
    @SuppressWarnings("deprecation") // Add this to suppress warnings for the whole method
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        populateAllDisplayInfo();


    }

    // In your DisplayFragment.java

    /**
     * Fetches all display properties from your list and populates the card.
     */
    private void populateAllDisplayInfo() {
        LinearLayout containerCard1 = requireView().findViewById(R.id.container_card_1);
        LinearLayout containerCard2 = requireView().findViewById(R.id.container_card_2);


        if (containerCard1 == null) return;
        containerCard1.removeAllViews();

        WindowManager windowManager = (WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics); // Use getRealMetrics for accurate screen dimensions

        Configuration config = getResources().getConfiguration();

        // Name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addRowToContainer(containerCard1, "Display Type", display.getName());
        }

        // Screen height & width
        addRowToContainer(containerCard1, "Screen Height", metrics.heightPixels + " px");
        addRowToContainer(containerCard1, "Screen Width", metrics.widthPixels + " px");

        // Screen size bucket
        addRowToContainer(containerCard1, "Screen Size", getScreenSizeBucket(config));

        // Physical size (calculated)
        double x = Math.pow(metrics.widthPixels / (double) metrics.xdpi, 2);
        double y = Math.pow(metrics.heightPixels / (double) metrics.ydpi, 2);
        double screenSizeInches = Math.sqrt(x + y);
        addRowToContainer(containerCard1, "Physical Size", String.format("%.2f inches", screenSizeInches));

        // Default orientation
        addRowToContainer(containerCard1, "Default Orientation", getOrientationString(config));

        // Refresh rate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addRowToContainer(containerCard1, "Refresh Rate", String.format("%.0fHz", display.getMode().getRefreshRate()));
        }

        // HDR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Display.HdrCapabilities caps = display.getHdrCapabilities();
            addRowToContainer(containerCard1, "HDR Support", getHdrTypesString(caps.getSupportedHdrTypes()));
        }

        // Brightness mode
        addRowToContainer(containerCard1, "Brightness Mode", getBrightnessMode(requireContext()));

        // Screen timeout
        addRowToContainer(containerCard1, "Screen timeout", getScreenTimeout(requireContext()));

        // Display bucket (Density)
        addRowToContainer(containerCard2, "Density Bucket", getDensityBucket(metrics));
        addRowToContainer(containerCard2, "Display Density", metrics.densityDpi + " dpi");
        addRowToContainer(containerCard2, "ppi on x-axis", String.format("%.0f dpi", metrics.xdpi));
        addRowToContainer(containerCard2, "ppi on y-axis", String.format("%.0f dpi", metrics.ydpi));
        addRowToContainer(containerCard2, "Logical Density", String.valueOf(metrics.density));
        addRowToContainer(containerCard2, "Scaled Density", String.valueOf(metrics.scaledDensity));

        // Font scale
        addRowToContainer(containerCard2, "Font Scale", String.valueOf(config.fontScale));
    }


// --- Helper Methods ---

    private String getScreenSizeBucket(Configuration config) {
        int size = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (size) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "Small Screen";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "Normal Screen";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "Large Screen";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "Extra Large Screen";
            default:
                return "Unknown";
        }
    }

    private String getOrientationString(Configuration config) {
        return (config.orientation == Configuration.ORIENTATION_LANDSCAPE) ? "Landscape" : "Portrait";
    }

    private String getBrightnessMode(Context context) {
        try {
            int mode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                return "Automatic";
            }
            return "Manual";
        } catch (Settings.SettingNotFoundException e) {
            return "Unknown";
        }
    }

    private String getScreenTimeout(Context context) {
        try {
            int timeoutMillis = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            return (timeoutMillis / 1000) + " Seconds";
        } catch (Settings.SettingNotFoundException e) {
            return "Unknown";
        }
    }

    private String getDensityBucket(DisplayMetrics metrics) {
        int density = metrics.densityDpi;
        if (density <= DisplayMetrics.DENSITY_LOW) return "ldpi";
        if (density <= DisplayMetrics.DENSITY_MEDIUM) return "mdpi";
        if (density <= DisplayMetrics.DENSITY_HIGH) return "hdpi";
        if (density <= DisplayMetrics.DENSITY_XHIGH) return "xhdpi";
        if (density <= DisplayMetrics.DENSITY_XXHIGH) return "xxhdpi";
        return "xxxhdpi";
    }

    // You also need your addRowToContainer method in this fragment clas
    /**
     * Helper method to convert HDR type constants into a readable string.
     * @param types An array of HDR type constants.
     * @return A formatted string of supported HDR types.
     */
    private String getHdrTypesString(int[] types) {
        if (types == null || types.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int type : types) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            switch (type) {
                case Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION:
                    sb.append("Dolby Vision");
                    break;
                case Display.HdrCapabilities.HDR_TYPE_HDR10:
                    sb.append("HDR10");
                    break;

                case Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS:
                    sb.append("HDR10+");
                    break;
                case Display.HdrCapabilities.HDR_TYPE_HLG:
                    sb.append("HLG");
                    break;
                default:
                    sb.append("Unknown");
            }
        }
        return sb.toString();
    }


}