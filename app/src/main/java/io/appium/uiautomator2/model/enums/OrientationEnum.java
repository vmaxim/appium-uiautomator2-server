package io.appium.uiautomator2.model.enums;

/**
 * Represents possible screen orientations.
 */
public enum OrientationEnum {
    LANDSCAPE("landscape"), PORTRAIT("portrait");

    private final String value;

    OrientationEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
