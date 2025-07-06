package com.example.systemedge;

/**
 * A data model for a single Display Test item.
 */
public class DisplayTestItem {

    private final String title;
    private final String description;
    private final int iconResId;
    private final Class<?> targetActivity; // The Activity class to launch on click

    public DisplayTestItem(String title, String description, int iconResId, Class<?> targetActivity) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.targetActivity = targetActivity;
    }

    // --- Getters ---
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }
}