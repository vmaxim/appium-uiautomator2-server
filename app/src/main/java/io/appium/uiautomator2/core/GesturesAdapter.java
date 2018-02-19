package io.appium.uiautomator2.core;

import android.support.test.uiautomator.UiDevice;

import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Created by max on 19.02.2018.
 */

public class GesturesAdapter {

    private final UiDevice uiDevice;
    private final ReflectionUtils reflectionUtils;

    public GesturesAdapter(UiDevice uiDevice, ReflectionUtils reflectionUtils) {
        this.uiDevice = uiDevice;
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetClass("android.support.test.uiautomator.Gestures");
    }
}
