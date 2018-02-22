package io.appium.uiautomator2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public enum ByStrategy {
    SELECTOR_NATIVE_ID("id", "By.id"),
    SELECTOR_XPATH("xpath", "By.xpath"),
    SELECTOR_ACCESSIBILITY_ID("accessibility id", "By.accessibilityId"),
    SELECTOR_CLASS("class name", "By.clazz"),
    SELECTOR_ANDROID_UIAUTOMATOR("-android uiautomator", "By.AndroidUiAutomator");

    private String value;
    private String description;

    ByStrategy(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @Nullable
    public static ByStrategy get(@NonNull final String value) {
        for (ByStrategy strategy : values()) {
            if (strategy.value.equals(value)) {
                return strategy;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }
}
