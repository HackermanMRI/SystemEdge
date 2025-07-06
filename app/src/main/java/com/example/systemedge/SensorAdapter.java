package com.example.systemedge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private final List<SensorInfo> sensorList;

    public SensorAdapter(List<SensorInfo> sensorList) {
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        SensorInfo sensorInfo = sensorList.get(position);

        // Set the data to the views in the list item
        holder.sensorIcon.setImageResource(sensorInfo.iconResId);
        holder.sensorName.setText(sensorInfo.name);
        holder.sensorIndustrialName.setText(String.format("Name: %s", sensorInfo.industrialName));
        holder.sensorVendor.setText(String.format("Vendor: %s", sensorInfo.vendor));
        holder.sensorWakeup.setText(String.format("Wake Up Sensor: %s", sensorInfo.isWakeUpSensor ? "Yes" : "No"));
        holder.sensorPower.setText(String.format(Locale.US, "Power: %.2fmA", sensorInfo.power));
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    /**
     * ViewHolder class to hold references to the views for each item.
     */
    static class SensorViewHolder extends RecyclerView.ViewHolder {
        ImageView sensorIcon;
        TextView sensorName, sensorIndustrialName, sensorVendor, sensorWakeup, sensorPower;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            sensorIcon = itemView.findViewById(R.id.sensor_icon);
            sensorName = itemView.findViewById(R.id.sensor_name);
            sensorIndustrialName = itemView.findViewById(R.id.sensor_industrial_name);
            sensorVendor = itemView.findViewById(R.id.sensor_vendor);
            sensorWakeup = itemView.findViewById(R.id.sensor_wakeup);
            sensorPower = itemView.findViewById(R.id.sensor_power);
        }
    }
}