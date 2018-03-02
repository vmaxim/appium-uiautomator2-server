package io.appium.uiautomator2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;

public enum ByStrategy {

    SELECTOR_NATIVE_ID("id", "By.id"),
    SELECTOR_XPATH("xpath", "By.xpath"),
    SELECTOR_ACCESSIBILITY_ID("accessibility id", "By.accessibilityId"),
    SELECTOR_CLASS("class name", "By.clazz"),
    SELECTOR_ANDROID_UIAUTOMATOR("-android uiautomator", "By.AndroidUiAutomator");

    private static final String ERR_MSG_UNSUPPORTED_STRATEGY = "Locator strategy '%s' is currently not supported!";

    private String value;
    private String description;

    ByStrategy(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @NonNull
    public static ByStrategy get(@NonNull final String value) throws InvalidSelectorException {
        for (ByStrategy strategy : values()) {
            if (strategy.value.equals(value)) {
                return strategy;
            }
        }
        String msg = String.format(ERR_MSG_UNSUPPORTED_STRATEGY, value);
        throw new InvalidSelectorException(msg);
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }
}
