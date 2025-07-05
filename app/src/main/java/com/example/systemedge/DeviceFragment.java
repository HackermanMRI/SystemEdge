package com.example.systemedge;
import com.example.systemedge.R;

import android.os.Bundle;
import android.os.Build;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.net.NetworkInterface;


public class DeviceFragment extends Fragment {
    //declarations
    private LinearLayout containerCard1;
    private LinearLayout containerCard2;
    private LayoutInflater inflater;



    //methods defination

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


    private String getGsfId() {
        try {
            Uri URI = Uri.parse("content://com.google.android.gsf.gservices");
            String ID_KEY = "android_id";
            String params[] = {ID_KEY};
            ContentResolver contentResolver = getContext().getContentResolver();
            Cursor c = contentResolver.query(URI, null, null, params, null);
            if (c == null || !c.moveToFirst() || c.getColumnCount() < 2) {
                return "Unknown";
            }
            String gsfId = Long.toHexString(Long.parseLong(c.getString(1)));
            c.close();
            return gsfId.toUpperCase();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    //Converts network type integer constants to human-readable strings.
    private String getNetworkTypeString(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR: return "5G"; // For 5G NSA
            default: return "Unknown (" + type + ")";
        }
    }

    //wifi mac address
    private String getWifiMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "Unknown";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Unknown";
    }


    //buetooth address
    private String getBluetoothMacAddress() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                return "Not supported";
            }
            // The address returned here is likely a randomized MAC value.
            return bluetoothAdapter.getAddress();
        } catch (SecurityException e) {
            // This happens if BLUETOOTH_CONNECT permission is not granted on API 31+
            return "Permission Denied";
        } catch (Exception e) {
            return "Unknown";
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Store the inflater to use it later
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        containerCard1 = view.findViewById(R.id.container_card_1);
        containerCard2 = view.findViewById(R.id.container_card_2);

        TextView textView1 = view.findViewById(R.id.text_device_brand);
        TextView textView2 = view.findViewById(R.id.text_device_model_subtitle);

        String manufacturer = Build.MANUFACTURER;
        textView1.setText(manufacturer.toUpperCase());
        String model = Build.MODEL;
        textView2.setText(model.toUpperCase());


        //card 1
        addRowToContainer(containerCard1, "Manufacturer", Build.MANUFACTURER);
        addRowToContainer(containerCard1, "Brand", Build.BRAND);
        addRowToContainer(containerCard1, "Model", Build.MODEL);
        addRowToContainer(containerCard1, "Device", Build.DEVICE);
        addRowToContainer(containerCard1, "Board", Build.BOARD);
        addRowToContainer(containerCard1, "Hardware Name", Build.HARDWARE);
        addRowToContainer(containerCard1, "Product Name", Build.PRODUCT);




        // card 2

        //DEVICE ID
        String androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        addRowToContainer(containerCard2, "Android Device ID", androidId);


        //HARDWARE SERIAL
        try {
            // This requires the READ_PHONE_STATE permission, which is dangerous.
            // On Android 10 (API 29) and above, this will throw a SecurityException
            // unless the app is a device owner or profile owner.
            String serial = Build.getSerial();
            addRowToContainer(containerCard2, "Hardware Serial", serial);
        } catch (SecurityException e) {
            addRowToContainer(containerCard2, "Hardware Serial", "Permission Denied");
        }


        //FINGERPRINT
        addRowToContainer(containerCard2, "Build Fingerprint", Build.FINGERPRINT);

        //USB DEBUGGING
        int adbEnabled = Settings.Global.getInt(getContext().getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        addRowToContainer(containerCard2, "USB Debugging", adbEnabled == 1 ? "On" : "Off");


        // Phone Type (GSM, CDMA, etc.)
        TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneTypeString = "Unknown";
        int phoneType = tm.getPhoneType();
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneTypeString = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneTypeString = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneTypeString = "SIP";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneTypeString = "None";
                break;
        }
        addRowToContainer(containerCard2, "Phone Type", phoneTypeString);


        // Network Operator
        addRowToContainer(containerCard2, "Network Operator", tm.getNetworkOperatorName());

        // Network Type (e.g., LTE, 5G)
        try {
            int networkType = tm.getDataNetworkType();
            addRowToContainer(containerCard2, "Network Type", getNetworkTypeString(networkType));
        } catch (SecurityException e) {
            addRowToContainer(containerCard2, "Network Type", "Permission Denied");
        }


        // WiFi MAC Address (Subject to privacy restrictions)
        addRowToContainer(containerCard2, "WiFi MAC Address", getWifiMacAddress());

        // Bluetooth MAC Address (Subject to privacy restrictions)
        addRowToContainer(containerCard2, "Bluetooth MAC Address", getBluetoothMacAddress());












        //GOOGLE SERVICE FRAMEWORK
        addRowToContainer(containerCard2, "Google Services Framework ID", getGsfId());

        //GOOGLE ADVERTISING ID
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String adId = "Unknown";
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
                if (adInfo != null) {
                    adId = adInfo.getId();
                }
            } catch (Exception e) {
                // This can happen if Google Play Services is not available or out of date.
                e.printStackTrace();
            }

            final String finalAdId = adId;
            // Update the UI back on the main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    addRowToContainer(containerCard2, "Google Advertising ID", finalAdId);
                });
            }
        });



    }
}
