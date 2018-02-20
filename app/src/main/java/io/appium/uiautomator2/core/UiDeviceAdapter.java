package io.appium.uiautomator2.core;

import android.app.Instrumentation;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiAutomatorBridge;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.ManagedAndroidElement;
import io.appium.uiautomator2.model.UiObject2Element;
import io.appium.uiautomator2.model.UiObjectElement;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoList;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class UiDeviceAdapter {

    private static final String ERR_MSG_UNSUPPOTED_SELECTOR = "Selector of type '%s' not supported";
    private static final String ERR_MSG_CREATE_UIOBJECT2 = "error while creating  UiObject2 object";
    private static final String ERR_MSG_NULL_ROOT_NODE = "Unable to get Root in Active window, " +
            "ERROR: null root node returned by UiTestAutomationBridge.";
    private static final String MSG_SKIP_NULL_ROOT_NODE = "Skipping null root node for window: %s";
    private static final String FIELD_INSTRUMENTATION = "mInstrumentation";
    private static final String FIELD_API_LEVEL_ACTUAL = "API_LEVEL_ACTUAL";
    private static final String FIELD_UI_AUTOMATOR_BRIDGE = "mUiAutomationBridge";
    private static final boolean MULTI_WINDOW = false;

    private final Instrumentation mInstrumentation;
    private final Object API_LEVEL_ACTUAL;
    private final ByMatcherAdapter byMatcherAdapter;
    private final UiAutomatorBridge uiAutomatorBridge;
    private final UiDevice uiDevice;

    /**
     * UiDevice in android open source project will Support multi-window searches for API level 21,
     * which has not been implemented in UiAutomatorViewer capture layout hierarchy, to be in sync
     * with UiAutomatorViewer customizing getWindowRoots() method to skip the multi-window search
     * based user passed property
     */
    public UiDeviceAdapter(UiDevice uiDevice, ByMatcherAdapter byMatcherAdapter, ReflectionUtils
            reflectionUtils) {
        this.uiDevice = uiDevice;
        this.byMatcherAdapter = byMatcherAdapter;
        reflectionUtils.setTargetObject(uiDevice);
        this.mInstrumentation = reflectionUtils.getField(FIELD_INSTRUMENTATION);
        this.API_LEVEL_ACTUAL = reflectionUtils.getField(FIELD_API_LEVEL_ACTUAL);
        this.uiAutomatorBridge = reflectionUtils.getField(FIELD_UI_AUTOMATOR_BRIDGE);
    }

    public UiAutomatorBridge getUiAutomatorBridge() {
        return uiAutomatorBridge;
    }

    public Instrumentation getInstrumentation() {
        return mInstrumentation;
    }

    public @Nullable
    AndroidElement findObject(final UiSelector selector) {
        waitForIdle();
        UiObject uiObject = uiDevice.findObject(selector);
        if (uiObject.exists()) {
            return new UiObjectElement(uiObject);
        }
        return null;
    }

    public @Nullable
    AndroidElement findObject(final BySelector selector) {
        AccessibilityNodeInfo node = byMatcherAdapter.findMatch(selector,
                getWindowRoots());
        if (node == null) {
            return null;
        }
        return createUiObject2Element(selector, node);

    }

    public @Nullable
    AndroidElement findObject(final AccessibilityNodeInfo node) {
        BySelector selector = By.clazz(node.getClassName().toString());
        return createUiObject2Element(selector, node);
    }

    public @Nullable
    AndroidElement findObject(final AccessibilityNodeInfoList nodeList) {
        if (nodeList.isEmpty()) {
            return null;
        }
        return findObject(nodeList.get(0));
    }

    public List<AndroidElement> findObjects(BySelector selector) {
        List<AccessibilityNodeInfo> nodeList = byMatcherAdapter.findMatches((BySelector)
                selector, getWindowRoots());
        return createUiObject2Elements(nodeList);
    }

    public List<AndroidElement> findObjects(AccessibilityNodeInfoList nodeList) {
        return createUiObject2Elements(nodeList);
    }

    /**
     * Returns a list containing the root {@link AccessibilityNodeInfo}s for each active window
     */
    AccessibilityNodeInfo[] getWindowRoots() throws UiAutomator2Exception {
        waitForIdle();
        ArrayList<AccessibilityNodeInfo> ret = new ArrayList<>();
        /**
         * TODO: MULTI_WINDOW is disabled, UIAutomatorViewer captures active window properties and
         * end users always relay on UIAutomatorViewer while writing tests.
         * If we enable MULTI_WINDOW it effects end users.
         * https://code.google.com/p/android/issues/detail?id=207569
         */
        if ((Integer) API_LEVEL_ACTUAL >= Build.VERSION_CODES.LOLLIPOP && MULTI_WINDOW) {
            // Support multi-window searches for API level 21 and up
            for (AccessibilityWindowInfo window : mInstrumentation.getUiAutomation().getWindows()) {
                AccessibilityNodeInfo root = window.getRoot();
                if (root == null) {
                    Logger.debug(String.format(MSG_SKIP_NULL_ROOT_NODE, window.toString()));
                    continue;
                }
                ret.add(root);
            }
            // Prior to API level 21 we can only access the active window
        } else {
            AccessibilityNodeInfo node = mInstrumentation.getUiAutomation().getRootInActiveWindow();
            if (node != null) {
                ret.add(node);
            } else {
                /*
                 TODO: As we can't proceed to find element with out root node,
                 TODO: retrying for 5 times to get the root node if UiTestAutomationBridge reruns
                  null
                 TODO: need to handle gracefully
                 */
                //AccessibilityNodeInfo should not be null.
                int retryCount = 0;
                while (node == null && retryCount < 5) {
                    SystemClock.sleep(1000);
                    waitForIdle();
                    Logger.debug(ERR_MSG_NULL_ROOT_NODE + ". Retrying: " + retryCount);
                    node = mInstrumentation.getUiAutomation().getRootInActiveWindow();
                    if (node != null) {
                        ret.add(node);
                    }
                    retryCount++;
                }
                if (node == null) {
                    throw new UiAutomator2Exception(ERR_MSG_NULL_ROOT_NODE);
                }
            }
        }
        return ret.toArray(new AccessibilityNodeInfo[ret.size()]);
    }

    public void wake() throws RemoteException {
        uiDevice.wakeUp();
    }

    public void scrollTo(String scrollToString) throws UiObjectNotFoundException {
        // TODO This logic needs to be changed according to the request body from the Driver
        UiScrollable uiScrollable = new UiScrollable(new UiSelector().scrollable(true).instance(0));
        uiScrollable.scrollIntoView(new UiSelector().descriptionContains(scrollToString).instance
                (0));
        uiScrollable.scrollIntoView(new UiSelector().textContains(scrollToString).instance(0));
    }

    public boolean back() {
        return uiDevice.pressBack();
    }

    public ManagedAndroidElement createManagedAndroidElement(AndroidElement element, io
            .appium.uiautomator2.model.By by) throws UiAutomator2Exception {
        String id = UUID.randomUUID().toString();
        return new ManagedAndroidElement(id, element, by);
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
            Logger.error(String.format("Unable wait %d for AUT to idle", timeInMS));
        }
    }

    public void pressKeyCode(final int keyCode, final int metaState) {
        uiDevice.pressKeyCode(keyCode, metaState);
    }

    public void pressKeyCode(final int keyCode) {
        uiDevice.pressKeyCode(keyCode);
    }

    private UiObject2Element createUiObject2Element(final BySelector selector, final
    AccessibilityNodeInfo node) {
        Constructor cons = UiObject2.class.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        Object[] constructorParams = {uiDevice, selector, node};
        try {
            UiObject2 uiObject2 = (UiObject2) cons.newInstance(constructorParams);
            return new UiObject2Element(uiObject2);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            final String msg = ERR_MSG_CREATE_UIOBJECT2;
            Logger.error(msg + " " + e);
            throw new UiAutomator2Exception(msg, e);
        }
    }

    private List<AndroidElement> createUiObject2Elements(List<AccessibilityNodeInfo> nodeList) {
        List<AndroidElement> result = new ArrayList<>();
        for (AccessibilityNodeInfo node : nodeList) {
            result.add(createUiObject2Element(By.clazz(node.getClassName().toString()), node));
        }
        return result;
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

    public Boolean swipe(int startX, int startY, int endX, int endY, Integer steps) {
        return uiDevice.swipe(startX, startY, endX, endY, steps);
    }

    public boolean pressEnter() {
        return uiDevice.pressEnter();
    }

    public boolean click(int x, int y) {
        return uiDevice.click(x, y);
    }

    public boolean drag(int startX, int startY, int endX, int endY, Integer steps) {
        return uiDevice.drag(startX, startY, endX, endY, steps);
    }

    public boolean openNotification() {
        return uiDevice.openNotification();
    }

    public void setCompressedLayoutHeirarchy(Boolean compressLayout) {
        uiDevice.setCompressedLayoutHeirarchy(compressLayout);
    }

    public void wakeUp() {
        try {
            uiDevice.wakeUp();
        } catch (RemoteException e) {
            Logger.error("Unable to wake up device", e);
        }
    }
}
