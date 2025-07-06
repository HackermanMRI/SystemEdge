package com.example.systemedge;

public class CameraProperty {
    private final String key;
    private final String value;

    public CameraProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}