package com.example.systemedge;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DisplayTestAdapter extends RecyclerView.Adapter<DisplayTestAdapter.DisplayTestViewHolder> {

    private final List<DisplayTestItem> testList;
    private final Context context;

    public DisplayTestAdapter(Context context, List<DisplayTestItem> testList) {
        this.context = context;
        this.testList = testList;
    }

    @NonNull
    @Override
    public DisplayTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_display_test, parent, false);
        return new DisplayTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayTestViewHolder holder, int position) {
        DisplayTestItem testItem = testList.get(position);

        // Set the data for the current item
        holder.testTitle.setText(testItem.getTitle());
        holder.testDescription.setText(testItem.getDescription());
        holder.testIcon.setImageResource(testItem.getIconResId());

        // Set the click listener to launch the correct activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, testItem.getTargetActivity());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    static class DisplayTestViewHolder extends RecyclerView.ViewHolder {
        ImageView testIcon;
        TextView testTitle;
        TextView testDescription;

        public DisplayTestViewHolder(@NonNull View itemView) {
            super(itemView);
            testIcon = itemView.findViewById(R.id.display_test_icon);
            testTitle = itemView.findViewById(R.id.display_test_title);
            testDescription = itemView.findViewById(R.id.display_test_description);
        }
    }
}