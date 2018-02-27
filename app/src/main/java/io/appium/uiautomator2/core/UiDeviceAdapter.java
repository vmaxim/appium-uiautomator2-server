package io.appium.uiautomator2.core;

import android.app.Instrumentation;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiAutomatorBridge;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class UiDeviceAdapter {

    private static final String FIELD_INSTRUMENTATION = "mInstrumentation";
    private static final String FIELD_API_LEVEL_ACTUAL = "API_LEVEL_ACTUAL";
    private static final String FIELD_UI_AUTOMATOR_BRIDGE = "mUiAutomationBridge";

    private final Instrumentation mInstrumentation;
    private final Integer apiLevelActual;
    private final UiAutomatorBridge uiAutomatorBridge;
    private final UiDevice uiDevice;

    public UiDeviceAdapter(@NonNull final UiDevice uiDevice,
                           @NonNull final ReflectionUtils reflectionUtils) {
        this.uiDevice = uiDevice;
        reflectionUtils.setTargetObject(uiDevice);
        this.mInstrumentation = reflectionUtils.getField(FIELD_INSTRUMENTATION);
        this.apiLevelActual = reflectionUtils.getField(FIELD_API_LEVEL_ACTUAL);
        this.uiAutomatorBridge = reflectionUtils.getField(FIELD_UI_AUTOMATOR_BRIDGE);
    }

    public UiAutomatorBridge getUiAutomatorBridge() {
        return uiAutomatorBridge;
    }

    public Instrumentation getInstrumentation() {
        return mInstrumentation;
    }

    public void wakeUp() throws RemoteException {
        uiDevice.wakeUp();
    }

    public void scrollTo(@NonNull final String scrollToString) throws UiObjectNotFoundException {
        // TODO This logic needs to be changed according to the request body from the Driver
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
        uiScrollable.scrollIntoView(new UiSelector().descriptionContains(scrollToString).instance
                (0));
        uiScrollable.scrollIntoView(new UiSelector().textContains(scrollToString).instance(0));
    }

    public boolean back() {
        return uiDevice.pressBack();
    }

    /**
     * reason for explicit method, in some cases google UiAutomator2 throwing exception
     * while calling waitForIdle() which is causing appium UiAutomator2 server to fall in
     * unexpected behaviour.
     * for more info please refer
     * https://code.google.com/p/android/issues/detail?id=73297
     */
    public void waitForIdle() {
        try {
            uiDevice.waitForIdle();
        } catch (Exception e) {
            Logger.error("Unable wait for AUT to idle");
        }
    }

    public void waitForIdle(final long timeInMS) {
        try {
            uiDevice.waitForIdle(timeInMS);
        } catch (Exception e) {
            Logger.debug("Unable wait %d for AUT to idle", timeInMS);
        }
    }

    public void pressKeyCode(final int keyCode, final int metaState) {
        uiDevice.pressKeyCode(keyCode, metaState);
    }

    public void pressKeyCode(final int keyCode) {
        uiDevice.pressKeyCode(keyCode);
    }

    public int getDisplayRotation() {
        return uiDevice.getDisplayRotation();
    }

    public void setOrientationRight() throws RemoteException {
        uiDevice.setOrientationRight();
    }

    public void setOrientationLeft() throws RemoteException {
        uiDevice.setOrientationLeft();
    }

    public void setOrientationNatural() throws RemoteException {
        uiDevice.setOrientationNatural();
    }

    public int getDisplayHeight() {
        return uiDevice.getDisplayHeight();
    }

    public int getDisplayWidth() {
        return uiDevice.getDisplayWidth();
    }

    public Boolean swipe(final int startX, final int startY, final int endX, final int endY,
                         final Integer steps) {
        return uiDevice.swipe(startX, startY, endX, endY, steps);
    }

    public boolean pressEnter() {
        return uiDevice.pressEnter();
    }

    public boolean click(final int x, final int y) {
        return uiDevice.click(x, y);
    }

    public boolean drag(final int startX, final int startY, final int endX, final int endY, final
    Integer steps) {
        return uiDevice.drag(startX, startY, endX, endY, steps);
    }

    public boolean openNotification() {
        return uiDevice.openNotification();
    }

    public void setCompressedLayoutHeirarchy(@NonNull final Boolean compressLayout) {
        uiDevice.setCompressedLayoutHeirarchy(compressLayout);
    }

    @NonNull
    public UiDevice getUiDevice() {
        return uiDevice;
    }

    public Integer getApiLevelActual() {
        return apiLevelActual;
    }

    public <R> R wait(SearchCondition<R> condition, long timeout) {
        return uiDevice.wait(condition, timeout);
    }
}
