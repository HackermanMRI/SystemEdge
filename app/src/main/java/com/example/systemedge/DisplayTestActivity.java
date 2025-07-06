package com.example.systemedge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// The imports for Toolbar and Objects have been removed
import java.util.ArrayList;
import java.util.List;

public class DisplayTestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DisplayTestAdapter adapter;
    private final List<DisplayTestItem> testItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_test);

        // The code for setting up the Toolbar has been removed from here.

        // Setup the RecyclerView
        recyclerView = findViewById(R.id.display_test_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Prepare the list of tests and set up the adapter
        prepareDisplayTestItems();
        adapter = new DisplayTestAdapter(this, testItems);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Creates and populates the list of all display tests.
     */
    private void prepareDisplayTestItems() {
        testItems.clear();

        // You will need to create these drawable icons
        testItems.add(new DisplayTestItem("Screen Grayscale Test", "The more grayscale line you can see, the better the screen effect", R.drawable.ic_test_display, GrayscaleTestActivity.class));
        testItems.add(new DisplayTestItem("Saturation Test", "The saturation of color display", R.drawable.ic_test_display, SaturationTestActivity.class));
        testItems.add(new DisplayTestItem("Screen Color Test", "Detect the color performance of the screen, dead pixels, highlights", R.drawable.ic_test_display, ScreenColorTestActivity.class));
        testItems.add(new DisplayTestItem("Screen White Balance", "Black and white colors, differentiate between pure black and white", R.drawable.ic_test_display, WhiteBalanceTestActivity.class));
        testItems.add(new DisplayTestItem("Touch Test", "Detect whether the full range of touch screen is normal", R.drawable.ic_test_display, TouchTestActivity.class));
        testItems.add(new DisplayTestItem("MultiTouch Support", "Detects the maximum number of touch points supported by the screen", R.drawable.ic_test_display, MultiTouchTestActivity.class));
    }
}