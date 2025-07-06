package com.example.systemedge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CameraPropertiesAdapter extends RecyclerView.Adapter<CameraPropertiesAdapter.PropertyViewHolder> {

    private final List<CameraProperty> properties;

    public CameraPropertiesAdapter(List<CameraProperty> properties) {
        this.properties = properties;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_camera_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        CameraProperty property = properties.get(position);
        holder.keyTextView.setText(property.getKey());
        holder.valueTextView.setText(property.getValue());
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        final TextView keyTextView;
        final TextView valueTextView;

        PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.text_property_key);
            valueTextView = itemView.findViewById(R.id.text_property_value);
        }
    }
}
