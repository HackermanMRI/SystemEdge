package com.example.systemedge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class TemperatureAdapter extends RecyclerView.Adapter<TemperatureAdapter.TempViewHolder> {

    private final List<ThermalInfo> thermalInfoList;

    public TemperatureAdapter(List<ThermalInfo> thermalInfoList) {
        this.thermalInfoList = thermalInfoList;
    }

    @NonNull
    @Override
    public TempViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_temperature, parent, false);
        return new TempViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempViewHolder holder, int position) {
        ThermalInfo thermalInfo = thermalInfoList.get(position);
        holder.componentName.setText(thermalInfo.getComponentName());
        holder.componentTemp.setText(String.format(Locale.US, "%.1f °C", thermalInfo.getTemperatureCelsius()));
    }

    @Override
    public int getItemCount() {
        return thermalInfoList.size();
    }

    static class TempViewHolder extends RecyclerView.ViewHolder {
        TextView componentName;
        TextView componentTemp;

        public TempViewHolder(@NonNull View itemView) {
            super(itemView);
            componentName = itemView.findViewById(R.id.component_name);
            componentTemp = itemView.findViewById(R.id.component_temperature);
        }
    }
}