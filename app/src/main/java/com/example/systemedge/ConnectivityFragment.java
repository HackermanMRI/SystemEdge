package com.example.systemedge;


import android.os.Bundle;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import android.net.NetworkInfo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ConnectivityFragment extends Fragment {

    //declarations
    private LinearLayout containerCard1, containerCard2;
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


    private String formatIpAddress(int ipAddress) {
        if (ipAddress == 0) return "N/A";
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                ((ipAddress >> 24) & 0xFF);
    }

    private String formatLeaseDuration(int seconds) {
        if (seconds == 0) return "N/A";
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    private void displayIpv6Addresses() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.isUp()) continue;
                // Typically "wlan0" or "wlan1" for Wi-Fi
                if (intf.getName().startsWith("wlan")) {
                    addRowToContainer(containerCard1, "Interface", intf.getName());
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    StringBuilder ipv6Builder = new StringBuilder();
                    for (InetAddress addr : addrs) {
                        if (addr instanceof Inet6Address && !addr.isLinkLocalAddress()) {
                            ipv6Builder.append(addr.getHostAddress()).append("\n");
                        }
                    }
                    if (ipv6Builder.length() > 0) {
                        // remove last newline
                        ipv6Builder.setLength(ipv6Builder.length() - 1);
                        addRowToContainer(containerCard1, "IPv6", ipv6Builder.toString());
                    }
                    return; // Stop after finding the first wlan interface
                }
            }
        } catch (Exception ex) {
            addRowToContainer(containerCard1, "IPv6", "Error fetching");
        }
    }

    private String getWifiSecurityType(WifiInfo info) {
        // This is a simplified check. Real-world security detection can be more complex.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int securityType = info.getCurrentSecurityType();
            switch (securityType) {
                case WifiInfo.SECURITY_TYPE_OPEN:
                    return "Open";
                case WifiInfo.SECURITY_TYPE_WEP:
                    return "WEP";
                case WifiInfo.SECURITY_TYPE_PSK:
                    return "PSK";
                //case WifiInfo.SECURITY_TYPE_WPA_PSK:
                //return "WPA-PSK";
                //case WifiInfo.SECURITY_TYPE_WPA2_PSK:
                //return "WPA2-PSK";
                //case WifiInfo.SECURITY_TYPE_WPA3_PSK:
                // return "WPA3-PSK";
                case WifiInfo.SECURITY_TYPE_EAP:
                    return "EAP";
                //case WifiInfo.SECURITY_TYPE_WPA_EAP:
                //return "WPA-EAP";
                case WifiInfo.SECURITY_TYPE_SAE:
                    return "SAE-WPA3";
                default:
                    return "Unknown";

            }
        }
        // Fallback for older devices can be more complex, checking capabilities.
        return "Unknown";
    }


    private String getNetworkTypeString(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G NR"; // For 5G
            default:
                return "Unknown";
        }
    }


    private void populateSimInfoCards() {
        // Find the main container from your fragment's layout
        LinearLayout simCardsContainer = requireView().findViewById(R.id.sim_cards_container);
        simCardsContainer.removeAllViews(); // Clear old cards before adding new ones

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            // SubscriptionManager is only available on API 22+
            addRowToContainer(simCardsContainer, "SIM Info", "Not supported on this API level");
            return;
        }

        SubscriptionManager subscriptionManager = (SubscriptionManager) requireActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        // This requires READ_PHONE_STATE permission
        try {
            List<SubscriptionInfo> activeSims = subscriptionManager.getActiveSubscriptionInfoList();

            if (activeSims == null || activeSims.isEmpty()) {
                // Inflate a simple card to show no SIMs are found
                View cardView = inflater.inflate(R.layout.layout_sim_card, simCardsContainer, false);
                TextView title = cardView.findViewById(R.id.sim_card_title);
                title.setText("No Active SIM Card Found");
                simCardsContainer.addView(cardView);
                return;
            }

            // Loop through each active SIM and create a card for it
            for (SubscriptionInfo simInfo : activeSims) {
                // Inflate our new card layout
                View cardView = inflater.inflate(R.layout.layout_sim_card, simCardsContainer, false);

                // Get the title and the inner container from the inflated card
                TextView title = cardView.findViewById(R.id.sim_card_title);
                LinearLayout infoContainer = cardView.findViewById(R.id.sim_info_container);

                // Set the title, e.g., "SIM 1", "SIM 2"

                title.setText("SIM " + simInfo.getSimSlotIndex());

                // Use our existing helper to add rows to the *new card's* container
                addRowToContainer(infoContainer, "Name", simInfo.getDisplayName().toString());
                addRowToContainer(infoContainer, "Number", simInfo.getNumber());
                addRowToContainer(infoContainer, "Country ISO", simInfo.getCountryIso());
                addRowToContainer(infoContainer, "MCC", String.valueOf(simInfo.getMcc()));
                addRowToContainer(infoContainer, "MNC", String.valueOf(simInfo.getMnc()));
                addRowToContainer(infoContainer, "Carrier Name", simInfo.getCarrierName().toString());

                String roaming = (simInfo.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE) ? "Yes" : "No";
                addRowToContainer(infoContainer, "Data Roaming", roaming);

                // Only call getCarrierId() on devices that have it (API 24+)
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    addRowToContainer(infoContainer, "Carrier ID", String.valueOf(simInfo.getCarrierId()));
                } else {
                    addRowToContainer(infoContainer, "Carrier ID", "N/A (API < 24)");
                }*/

                // Add the fully populated card to our main screen container
                simCardsContainer.addView(cardView);
            }

        } catch (SecurityException e) {
            // Handle the case where permission is denied
            View cardView = inflater.inflate(R.layout.layout_sim_card, simCardsContainer, false);
            TextView title = cardView.findViewById(R.id.sim_card_title);
            title.setText("Permission Required");
            LinearLayout infoContainer = cardView.findViewById(R.id.sim_info_container);
            addRowToContainer(infoContainer, "Info", "READ_PHONE_STATE permission is needed to access SIM details.");
            simCardsContainer.addView(cardView);
        }
    }


    private void populateMobileInfoCard() {
        // This method is a simplified version of what you had.
        // It should now correctly populate card 2.
        containerCard2.removeAllViews();

        TelephonyManager tm = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (tm == null || connManager == null) {
            addRowToContainer(containerCard2, "Error", "Could not get system service");
            return;
        }

        // --- Status ---
        boolean isMobileConnected = false;
        Network activeNet = connManager.getActiveNetwork();
        if (activeNet != null) {
            NetworkCapabilities caps = connManager.getNetworkCapabilities(activeNet);
            if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isMobileConnected = true;
            }
        }
        addRowToContainer(containerCard2, "Status", isMobileConnected ? "Connected" : "Not Connected");

        // --- Multi SIM ---
        String multiSimStatus = "Unsupported";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (tm.getPhoneCount() > 1) {
                multiSimStatus = "Supported";
            }
        }
        addRowToContainer(containerCard2, "Multi SIM", multiSimStatus);

        // --- Network Type ---
        try {
            int networkType = tm.getDataNetworkType();
            addRowToContainer(containerCard2, "Network Type", getNetworkTypeString(networkType));
        } catch (SecurityException e) {
            addRowToContainer(containerCard2, "Network Type", "Permission needed");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_connectivity, container, false);
    }


    @Override
    @SuppressWarnings("deprecation") // Add this to suppress warnings for the whole method
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Setup Views ---
        containerCard1 = view.findViewById(R.id.container_card_1);
        containerCard2 = view.findViewById(R.id.container_card_2);
        TextView textConnectionType = view.findViewById(R.id.text_connection_type);
        TextView textNetworkSource = view.findViewById(R.id.text_network_source);
        TextView textSignalStrength = view.findViewById(R.id.text_signal_strength);



        // --- Clear Previous Data ---
        containerCard1.removeAllViews();
        containerCard2.removeAllViews();

        // --- Get System Services ---
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);

        // --- Check Network State and Populate Cards ---
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            // --- CASE 1: DEVICE IS CONNECTED TO WI-FI ---
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                textConnectionType.setText("Wi-Fi");

                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

                // Set Header
                String ssid = wifiInfo.getSSID().replace("\"", "");
                textNetworkSource.setText(ssid.contains("<unknown ssid>") ? "Location needed" : ssid);

                //signal strength
                int rssi = wifiInfo.getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 100);
                String signalText = rssi + " dBm, " + level + "%";
                textSignalStrength.setText(signalText);

                // Populate Wi-Fi Details Card (containerCard1)
                addRowToContainer(containerCard1, "Status", "Connected");
                addRowToContainer(containerCard1, "Safety", getWifiSecurityType(wifiInfo));
                addRowToContainer(containerCard1, "BSSID", wifiInfo.getBSSID());
                addRowToContainer(containerCard1, "Gateway", formatIpAddress(dhcpInfo.gateway));
                addRowToContainer(containerCard1, "Netmask", formatIpAddress(dhcpInfo.netmask));
                addRowToContainer(containerCard1, "DNS1", formatIpAddress(dhcpInfo.dns1));
                addRowToContainer(containerCard1, "IP", formatIpAddress(dhcpInfo.ipAddress));
                displayIpv6Addresses(); // This helper also adds rows to containerCard1
                addRowToContainer(containerCard1, "Link Speed", wifiInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS);
                addRowToContainer(containerCard1, "Frequency", wifiInfo.getFrequency() + " " + WifiInfo.FREQUENCY_UNITS);

                // --- CASE 2: DEVICE IS CONNECTED TO MOBILE DATA ---
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                textConnectionType.setText("Mobile Data");
                textNetworkSource.setText(telephonyManager.getNetworkOperatorName());

                // Wi-Fi is not the active network, so show a message in its card
                addRowToContainer(containerCard1, "Status", "Wi-Fi not active");
            }
            // --- CASE 3: DEVICE IS DISCONNECTED ---
        } else {
            textConnectionType.setText("Disconnected");
            textNetworkSource.setText("No network connection");
            addRowToContainer(containerCard1, "Status", "Wi-Fi is disconnected");
        }


//card 3
        populateMobileInfoCard();

//card sims
        populateSimInfoCards();





    }

}