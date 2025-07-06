package com.example.systemedge;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.util.Rational;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CameraFragment extends Fragment {

    private TextView backCameraMp, backCameraResolution, backCameraFocalLength;
    private TextView frontCameraMp, frontCameraResolution, frontCameraFocalLength;
    private View cardBackCamera, cardFrontCamera;
    private RecyclerView recyclerView;
    private CameraPropertiesAdapter adapter;
    private final List<CameraProperty> propertyList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardBackCamera = view.findViewById(R.id.card_back_camera);
        backCameraMp = view.findViewById(R.id.back_camera_mp);
        backCameraResolution = view.findViewById(R.id.back_camera_resolution);
        backCameraFocalLength = view.findViewById(R.id.back_camera_focal_length);
        cardFrontCamera = view.findViewById(R.id.card_front_camera);
        frontCameraMp = view.findViewById(R.id.front_camera_mp);
        frontCameraResolution = view.findViewById(R.id.front_camera_resolution);
        frontCameraFocalLength = view.findViewById(R.id.front_camera_focal_length);
        recyclerView = view.findViewById(R.id.recycler_view_camera_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CameraPropertiesAdapter(propertyList);
        recyclerView.setAdapter(adapter);

        loadCameraInfo();
    }

    private void loadCameraInfo() {
        CameraManager manager = (CameraManager) requireActivity().getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) return;

        try {
            String backCameraId = null;
            String frontCameraId = null;

            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing == null) continue;

                if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    backCameraId = cameraId;
                } else if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = cameraId;
                }
            }

            if (backCameraId != null) {
                populateCameraDetails(manager.getCameraCharacteristics(backCameraId), true);
                populatePropertyList(manager.getCameraCharacteristics(backCameraId));
            } else {
                cardBackCamera.setVisibility(View.GONE);
            }

            if (frontCameraId != null) {
                populateCameraDetails(manager.getCameraCharacteristics(frontCameraId), false);
                if (backCameraId == null) {
                    populatePropertyList(manager.getCameraCharacteristics(frontCameraId));
                }
            } else {
                cardFrontCamera.setVisibility(View.GONE);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void populateCameraDetails(CameraCharacteristics characteristics, boolean isBackCamera) {
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) return;

        Size[] outputSizes = map.getOutputSizes(ImageFormat.JPEG);
        if (outputSizes == null || outputSizes.length == 0) return;

        Size largestSize = Collections.max(Arrays.asList(outputSizes), (s1, s2) -> Long.compare((long) s1.getWidth() * s1.getHeight(), (long) s2.getWidth() * s2.getHeight()));

        double megapixels = (largestSize.getWidth() * largestSize.getHeight()) / 1_000_000.0;
        String resolution = largestSize.getWidth() + "x" + largestSize.getHeight();

        float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        String focalLengthStr = (focalLengths != null && focalLengths.length > 0) ? String.format(Locale.US, "%.2f mm", focalLengths[0]) : "N/A";

        if (isBackCamera) {
            backCameraMp.setText(String.format(Locale.US, "%.1f MP - Rear", megapixels));
            backCameraResolution.setText(resolution);
            backCameraFocalLength.setText(focalLengthStr);
        } else {
            frontCameraMp.setText(String.format(Locale.US, "%.1f MP - Front", megapixels));
            frontCameraResolution.setText(resolution);
            frontCameraFocalLength.setText(focalLengthStr);
        }
    }

    private void populatePropertyList(CameraCharacteristics characteristics) {
        propertyList.clear();

        // --- Properties available on all API levels (since your minSdk is 28) ---
        addIntArrayProperty("Antibanding Modes", characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES, this::formatAntibandingModes);
        addIntArrayProperty("Auto Exposure Modes", characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, this::formatAutoExposureModes);
        addIntArrayProperty("Auto Focus Modes", characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, this::formatAfModes);
        addIntArrayProperty("Effects", characteristics, CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS, this::formatEffectModes);
        addIntArrayProperty("Scene Modes", characteristics, CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES, this::formatSceneModes);
        addIntArrayProperty("Video Stabilization Modes", characteristics, CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES, this::formatVideoStabilizationModes);
        addIntArrayProperty("Auto White Balance Modes", characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, this::formatAwbModes);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addProperty("Compensation Range", characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE));
            addProperty("Compensation Step", characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP));
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Range<Integer>[] fpsRanges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            if (fpsRanges != null && fpsRanges.length > 0) {
                propertyList.add(new CameraProperty("Target FPS Ranges", formatFpsRanges(fpsRanges)));
            }

            addProperty("Maximum Auto Exposure Regions", characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE));
            addProperty("Maximum Auto Focus Regions", characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF));
            addProperty("Maximum Auto White Balance Regions", characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB));
            addIntArrayProperty("Edge Modes", characteristics, CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, this::formatEdgeModes);
            addProperty("Flash Available", characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE), this::formatBoolean);
            addIntArrayProperty("Hot Pixel Modes", characteristics, CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES, this::formatHotPixelModes);
            addProperty("Hardware Level", characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL), this::formatHardwareLevel);
            addProperty("Thumbnail Sizes", characteristics.get(CameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES), this::formatSizeArray);
            addProperty("Lens Placement", characteristics.get(CameraCharacteristics.LENS_FACING), this::formatLensPlacement);
            addProperty("Apertures", characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES));
            addProperty("Filter Densities", characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES));
            addProperty("Focal Lengths", characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS), this::formatFocalLengths);
            addIntArrayProperty("Optical Stabilization", characteristics, CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, this::formatOisModes);
            addProperty("Focus Distance Calibration", characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION), this::formatFocusDistanceCalibration);
        }


        // Lens Info
        addProperty("Hyperfocal Distance", characteristics.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE));
        addProperty("Minimum Focus Distance", characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE));
        addProperty("Apertures", characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES));
        addProperty("Filter Densities", characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES));
        addProperty("Focal Lengths", characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS), this::formatFocalLengths);
        addIntArrayProperty("Optical Stabilization", characteristics, CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, this::formatOisModes);
        addProperty("Focus Distance Calibration", characteristics.get(CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION), this::formatFocusDistanceCalibration);
        addProperty("Lens Placement", characteristics.get(CameraCharacteristics.LENS_FACING), this::formatLensPlacement);

        // Sensor Info
        addProperty("Pixel Array Size", characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE));
        addProperty("Sensor Size", characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE));
        addProperty("Timestamp Source", characteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE), this::formatTimestampSource);
        addProperty("Orientation", characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION), v -> v + " deg");
        addProperty("Color Filter Arrangement", characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT), this::formatColorFilterArrangement);
        addIntArrayProperty("Test Pattern Modes", characteristics, CameraCharacteristics.SENSOR_AVAILABLE_TEST_PATTERN_MODES, this::formatTestPatternModes);

        // Request & Capabilities
        addIntArrayProperty("Camera Capabilities", characteristics, CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES, this::formatCapabilities);
        addProperty("Partial Results", characteristics.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT));

        // Stream & Cropping
        addProperty("Maximum Digital Zoom", characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM));
        addProperty("Cropping Type", characteristics.get(CameraCharacteristics.SCALER_CROPPING_TYPE), this::formatCroppingType);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map != null) {
            addProperty("Supported Resolutions", map.getOutputSizes(ImageFormat.JPEG), this::formatSupportedResolutions);
        }

        // Face Detection
        addIntArrayProperty("Face Detection Modes", characteristics, CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES, this::formatFaceDetectModes);





        // --- Add properties that require Lollipop (API 21) or higher ---
        // This is safe because your minSdk is 28
        adapter.notifyDataSetChanged();
    }





    private String formatAntibandingModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_OFF) list.add("Off");
            if (mode == CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_50HZ) list.add("50Hz");
            if (mode == CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_60HZ) list.add("60Hz");
            if (mode == CameraCharacteristics.CONTROL_AE_ANTIBANDING_MODE_AUTO) list.add("Auto");
        }
        return TextUtils.join(", ", list);
    }

    private String formatAutoExposureModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_AE_MODE_OFF) list.add("Off");
            if (mode == CameraCharacteristics.CONTROL_AE_MODE_ON) list.add("On");
            if (mode == CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH) list.add("Auto Flash");
            if (mode == CameraCharacteristics.CONTROL_AE_MODE_ON_ALWAYS_FLASH) list.add("Always Flash");
            if (mode == CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE) list.add("Auto Flash Red-eye");
        }
        return TextUtils.join(", ", list);
    }

    private String formatAfModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_AF_MODE_OFF) list.add("Off");
            if (mode == CameraCharacteristics.CONTROL_AF_MODE_AUTO) list.add("Auto");
            if (mode == CameraCharacteristics.CONTROL_AF_MODE_MACRO) list.add("Macro");
            if (mode == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_VIDEO) list.add("Continuous Video");
            if (mode == CameraCharacteristics.CONTROL_AF_MODE_CONTINUOUS_PICTURE) list.add("Continuous Picture");
            if (mode == CameraCharacteristics.CONTROL_AF_MODE_EDOF) list.add("EDOF");
        }
        return TextUtils.join(", ", list);
    }

    private String formatEffectModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_OFF) list.add("Off");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_MONO) list.add("Mono");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_NEGATIVE) list.add("Negative");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_SOLARIZE) list.add("Solarize");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_SEPIA) list.add("Sepia");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_POSTERIZE) list.add("Posterize");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_WHITEBOARD) list.add("Whiteboard");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_BLACKBOARD) list.add("Blackboard");
            if (mode == CameraCharacteristics.CONTROL_EFFECT_MODE_AQUA) list.add("Aqua");
        }
        return TextUtils.join(", ", list);
    }

    private String formatSceneModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED) list.add("Disabled");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_FACE_PRIORITY) list.add("Face Priority");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_ACTION) list.add("Action");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_PORTRAIT) list.add("Portrait");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_LANDSCAPE) list.add("Landscape");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT) list.add("Night");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT_PORTRAIT) list.add("Night Portrait");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_THEATRE) list.add("Theatre");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_BEACH) list.add("Beach");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_SNOW) list.add("Snow");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_SUNSET) list.add("Sunset");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_STEADYPHOTO) list.add("Steady Photo");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_FIREWORKS) list.add("Fireworks");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_SPORTS) list.add("Sports");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_PARTY) list.add("Party");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_CANDLELIGHT) list.add("Candlelight");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_BARCODE) list.add("Barcode");
            if (mode == CameraCharacteristics.CONTROL_SCENE_MODE_HDR) list.add("HDR");
        }
        return TextUtils.join(", ", list);
    }

    private String formatVideoStabilizationModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_OFF) list.add("Off");
            if (mode == CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_ON) list.add("On");
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatFpsRanges(Range<Integer>[] ranges) {
        List<String> list = new ArrayList<>();
        for (Range<Integer> range : ranges) {
            list.add(range.toString());
        }
        return TextUtils.join(", ", list);
    }




    private String formatFocalLengths(float[] lengths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lengths.length; i++) {
            sb.append(String.format(Locale.US, "%.2fmm", lengths[i]));
            if (i < lengths.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatAwbModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.CONTROL_AWB_MODE_OFF) list.add("Off");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_AUTO) list.add("Auto");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_INCANDESCENT) list.add("Incandescent");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_FLUORESCENT) list.add("Fluorescent");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_WARM_FLUORESCENT) list.add("Warm Fluorescent");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_DAYLIGHT) list.add("Daylight");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT) list.add("Cloudy Daylight");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_TWILIGHT) list.add("Twilight");
            else if (mode == CameraCharacteristics.CONTROL_AWB_MODE_SHADE) list.add("Shade");
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatEdgeModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.EDGE_MODE_OFF) list.add("Off");
            else if (mode == CameraCharacteristics.EDGE_MODE_FAST) list.add("Fast");
            else if (mode == CameraCharacteristics.EDGE_MODE_HIGH_QUALITY) list.add("High Quality");
            else if (mode == CameraCharacteristics.EDGE_MODE_ZERO_SHUTTER_LAG) list.add("Zero Shutter Lag");
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatHotPixelModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.HOT_PIXEL_MODE_OFF) list.add("Off");
            else if (mode == CameraCharacteristics.HOT_PIXEL_MODE_FAST) list.add("Fast");
            else if (mode == CameraCharacteristics.HOT_PIXEL_MODE_HIGH_QUALITY) list.add("High Quality");
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatHardwareLevel(int level) {
        switch (level) {
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY: return "Legacy";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED: return "Limited";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL: return "Full";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3: return "Level 3";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL: return "External";
            default: return "Unknown";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatLensPlacement(int placement) {
        switch (placement) {
            case CameraCharacteristics.LENS_FACING_FRONT: return "Front";
            case CameraCharacteristics.LENS_FACING_BACK: return "Back";
            case CameraCharacteristics.LENS_FACING_EXTERNAL: return "External";
            default: return "Unknown";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatOisModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            if (mode == CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_OFF) list.add("Off");
            else if (mode == CameraCharacteristics.LENS_OPTICAL_STABILIZATION_MODE_ON) list.add("On");
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatFocusDistanceCalibration(int cal) {
        switch (cal) {
            case CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED: return "Uncalibrated";
            case CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE: return "Approximate";
            case CameraCharacteristics.LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED: return "Calibrated";
            default: return "Unknown";
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatCapabilities(int[] capabilities) {
        List<String> list = new ArrayList<>();
        for (int cap : capabilities) {
            switch (cap) {
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE: list.add("Backward Compatible"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR: list.add("Manual Sensor"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING: list.add("Manual Post Processing"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW: list.add("RAW"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING: list.add("Private Reprocessing"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS: list.add("Read Sensor Settings"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE: list.add("Burst Capture"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING: list.add("YUV Reprocessing"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT: list.add("Depth Output"); break;
                case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO: list.add("Constrained High Speed Video"); break;
            }
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatCroppingType(int type) {
        switch (type) {
            case CameraCharacteristics.SCALER_CROPPING_TYPE_CENTER_ONLY: return "Center Only";
            case CameraCharacteristics.SCALER_CROPPING_TYPE_FREEFORM: return "Freeform";
            default: return "Unknown";
        }
    }

    private String formatSupportedResolutions(Size[] sizes) {
        // Sort resolutions from largest to smallest
        Arrays.sort(sizes, (a, b) -> Long.compare((long) b.getWidth() * b.getHeight(), (long) a.getWidth() * a.getHeight()));
        StringBuilder sb = new StringBuilder();
        for (Size size : sizes) {
            double megapixels = (size.getWidth() * size.getHeight()) / 1_000_000.0;
            sb.append(String.format(Locale.US, "%.2f MP - %d x %d\n", megapixels, size.getWidth(), size.getHeight()));
        }
        // Remove the last newline character
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatTestPatternModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            switch (mode) {
                case CameraCharacteristics.SENSOR_TEST_PATTERN_MODE_OFF: list.add("Off"); break;
                case CameraCharacteristics.SENSOR_TEST_PATTERN_MODE_SOLID_COLOR: list.add("Solid Color"); break;
                case CameraCharacteristics.SENSOR_TEST_PATTERN_MODE_COLOR_BARS: list.add("Color Bars"); break;
                case CameraCharacteristics.SENSOR_TEST_PATTERN_MODE_COLOR_BARS_FADE_TO_GRAY: list.add("Color Bars Fade to Gray"); break;
                case CameraCharacteristics.SENSOR_TEST_PATTERN_MODE_PN9: list.add("PN9"); break;
            }
        }
        return TextUtils.join(", ", list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatColorFilterArrangement(int arrangement) {
        switch (arrangement) {
            case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB: return "RGGB";
            case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG: return "GRBG";
            case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG: return "GBRG";
            case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR: return "BGGR";
            case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGB: return "RGB";
            default: return "Unknown";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatTimestampSource(int source) {
        switch (source) {
            case CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME: return "Realtime";
            case CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN:
            default: return "Unknown";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String formatFaceDetectModes(int[] modes) {
        List<String> list = new ArrayList<>();
        for (int mode : modes) {
            // Note the corrected constant names from CameraMetadata
            if (mode == CameraMetadata.STATISTICS_FACE_DETECT_MODE_OFF) list.add("Off");
            else if (mode == CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE) list.add("Simple");
            else if (mode == CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL) list.add("Full");
        }
        return TextUtils.join(", ", list);
    }







    //format methods

    private String formatValue(Object value) {
        if (value instanceof Object[]) {
            return Arrays.deepToString((Object[]) value);
        }
        if (value instanceof float[]) {
            return Arrays.toString((float[]) value);
        }
        return value.toString();
    }

    private String formatBoolean(boolean b) {
        return b ? "Yes" : "No";
    }

    private String formatSizeArray(Size[] sizes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sizes.length; i++) {
            sb.append(sizes[i].toString());
            // Append a newline for all but the last item
            if (i < sizes.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }




    // --- Generic helpers to add properties to the list ---
    private <T> void addProperty(String key, T value) {
        if (value != null) {
            propertyList.add(new CameraProperty(key, formatValue(value)));
        }
    }

    private <T> void addProperty(String key, T value, ValueFormatter<T> formatter) {
        if (value != null) {
            propertyList.add(new CameraProperty(key, formatter.format(value)));
        }
    }

    private void addIntArrayProperty(String key, CameraCharacteristics c, CameraCharacteristics.Key<int[]> cKey, IntArrayFormatter formatter) {
        int[] values = c.get(cKey);
        if (values != null && values.length > 0) {
            propertyList.add(new CameraProperty(key, formatter.format(values)));
        }
    }



//interface
    @FunctionalInterface
    interface IntArrayFormatter {
        String format(int[] array);
    }

    @FunctionalInterface
    interface ValueFormatter<T> {
        String format(T value);
    }

}