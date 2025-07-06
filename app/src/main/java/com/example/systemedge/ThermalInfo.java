package com.example.systemedge;

public class ThermalInfo {
    private final String componentName;
    private final float temperatureCelsius;

    public ThermalInfo(String componentName, float temperatureCelsius) {
        this.componentName = componentName;
        this.temperatureCelsius = temperatureCelsius;
    }

    public String getComponentName() {
        return componentName;
    }

    public float getTemperatureCelsius() {
        return temperatureCelsius;
    }
}