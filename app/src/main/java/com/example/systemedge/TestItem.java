package com.example.systemedge;

/**
 * A data model for a single hardware test item.
 * It holds all the necessary information for display and navigation.
 */
public class TestItem {

    // Enum to represent the verification status of a test.
    public enum Status {
        NOT_TESTED,
        PASSED,
        FAILED
    }

    public final String name;
    public final int iconResId;
    public Status status;
    public final Class<?> targetActivity; // The Activity class to launch on click.

    public TestItem(String name, int iconResId, Status status, Class<?> targetActivity) {
        this.name = name;
        this.iconResId = iconResId;
        this.status = status;
        this.targetActivity = targetActivity;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Status getStatus() {
        return status;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }

    // --- Setter for updating the status later ---
    public void setStatus(Status status) {
        this.status = status;
    }
}