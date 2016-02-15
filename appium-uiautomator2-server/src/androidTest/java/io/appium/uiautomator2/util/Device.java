package io.appium.uiautomator2.util;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

public abstract class Device {
    private static final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    public static final UiDevice device() {
        return uiDevice;
    }
}
