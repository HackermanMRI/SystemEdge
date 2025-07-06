package com.example.systemedge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    // 1. Define an interface for click events right inside the adapter
    public interface OnItemClickListener {
        void onItemClick(TestItem testItem, int position);
    }

    private final Context context;
    private final List<TestItem> testList;
    private final OnItemClickListener listener; // 2. Add a listener member variable

    // 3. Update the constructor to accept the listener instead of the Context
    public TestAdapter(Context context, List<TestItem> testList, OnItemClickListener listener) {
        this.context = context;
        this.testList = testList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_test, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        TestItem testItem = testList.get(position);
        // 4. Pass the item and listener to the ViewHolder to handle binding and clicks
        holder.bind(testItem, listener);
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    static class TestViewHolder extends RecyclerView.ViewHolder {
        ImageView testIcon;
        TextView testName;
        ImageView testStatusIcon;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            testIcon = itemView.findViewById(R.id.test_icon);
            testName = itemView.findViewById(R.id.test_name);
            testStatusIcon = itemView.findViewById(R.id.test_status_icon);
        }

        // 5. The bind method now sets the data and the click listener
        public void bind(final TestItem testItem, final OnItemClickListener listener) {
            testName.setText(testItem.getName());
            testIcon.setImageResource(testItem.getIconResId());

            switch (testItem.getStatus()) {
                case PASSED:
                    testStatusIcon.setImageResource(R.drawable.ic_status_passed);
                    break;
                case FAILED:
                    testStatusIcon.setImageResource(R.drawable.ic_status_failed);
                    break;
                case NOT_TESTED:
                default:
                    testStatusIcon.setImageResource(R.drawable.ic_status_pending);
                    break;
            }

            // The click listener now calls the interface method, sending the click event
            // back to the fragment that is listening.
            itemView.setOnClickListener(v -> listener.onItemClick(testItem, getAdapterPosition()));
        }
    }
}