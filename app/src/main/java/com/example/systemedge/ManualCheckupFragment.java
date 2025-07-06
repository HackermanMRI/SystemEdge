package com.example.systemedge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManualCheckupFragment extends Fragment implements TestAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private TestAdapter adapter;
    public final List<TestItem> testItems = new ArrayList<>();

    public TestItem flashLight;
    private int clickedPosition = -1;


    private final ActivityResultLauncher<Intent> testLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    String statusString = result.getData().getStringExtra(FlashlightTestActivity.EXTRA_TEST_RESULT_STATUS);

                    if (statusString != null && clickedPosition != -1) {
                        TestItem.Status newStatus = TestItem.Status.valueOf(statusString);
                        // Update the correct item in the list
                        testItems.get(clickedPosition).setStatus(newStatus);
                        // Refresh just that one item in the list
                        adapter.notifyItemChanged(clickedPosition);
                    }
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the correct layout file
        return inflater.inflate(R.layout.fragment_manual_checkup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the RecyclerView using the correct ID
        recyclerView = view.findViewById(R.id.manual_checkup_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Prepare the list of tests and set up the adapter
        prepareTestItems();
        adapter = new TestAdapter(getContext(), testItems, this);
        recyclerView.setAdapter(adapter);
    }


    // This method is called when an item is clicked in your list
    @Override
    public void onItemClick(TestItem testItem, int position) {
        this.clickedPosition = position;
        Intent intent = new Intent(getContext(), testItem.getTargetActivity());
        // Launch the activity using the launcher
        testLauncher.launch(intent);
    }



    /**
     * Creates and populates the list of all hardware tests.
     * Each TestItem is linked to its specific test Activity.
     */
    private void prepareTestItems() {
        // Clear the list to avoid duplicates
        testItems.clear();

        // Add each test to the list.
        // The status is initially NOT_TESTED. The checkmarks in your screenshot
        // suggest some are PASSED, so I have set them accordingly as an example.

        testItems.add(new TestItem("Storage Analyze", R.drawable.ic_test_storage, TestItem.Status.NOT_TESTED, StorageTestActivity.class));
        testItems.add(new TestItem("Display", R.drawable.ic_test_display, TestItem.Status.NOT_TESTED, DisplayTestActivity.class));
        testItems.add(new TestItem("Flashlight", R.drawable.ic_test_flashlight, TestItem.Status.NOT_TESTED, FlashlightTestActivity.class));
        testItems.add(new TestItem("Loudspeaker", R.drawable.ic_test_loudspeaker, TestItem.Status.NOT_TESTED, LoudspeakerTestActivity.class));
        testItems.add(new TestItem("Ear Speaker", R.drawable.ic_test_earspeaker, TestItem.Status.NOT_TESTED, EarSpeakerTestActivity.class));
        testItems.add(new TestItem("Ear Proximity Sensor", R.drawable.ic_test_earproximity, TestItem.Status.NOT_TESTED, EarProximityTestActivity.class));
        testItems.add(new TestItem("Light Sensor", R.drawable.ic_test_lightsensor, TestItem.Status.NOT_TESTED, LightSensorTestActivity.class));
        testItems.add(new TestItem("Vibration", R.drawable.ic_test_vibration, TestItem.Status.NOT_TESTED, VibrationTestActivity.class));
        testItems.add(new TestItem("Bluetooth", R.drawable.ic_test_bluetooth, TestItem.Status.NOT_TESTED, BluetoothTestActivity.class));
        testItems.add(new TestItem("Fingerprint Test", R.drawable.ic_test_fingerprint, TestItem.Status.NOT_TESTED, FingerprintTestActivity.class));
        testItems.add(new TestItem("Volume Up Button", R.drawable.ic_test_volume_down, TestItem.Status.NOT_TESTED, VolumeUpTestActivity.class));
        testItems.add(new TestItem("Volume Down Button", R.drawable.ic_test_volume_up, TestItem.Status.NOT_TESTED, VolumeDownTestActivity.class));

    }




}