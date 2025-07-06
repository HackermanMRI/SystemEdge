package com.example.systemedge;

/**
 * A simple data model class to hold all the properties of a single sensor.
 */
public class SensorInfo {
    // The user-friendly name, e.g., "ACCELEROMETER"
    public String name;

    // The official name provided by the sensor hardware, e.g., "bmi160 Accelerometer"
    public String industrialName;

    // The manufacturer of the sensor, e.g., "BOSCH"
    public String vendor;

    // The power consumption in mA
    public float power;

    // Whether the sensor can wake up the device from sleep
    public boolean isWakeUpSensor;

    // The resource ID for the sensor's icon
    public int iconResId;
}