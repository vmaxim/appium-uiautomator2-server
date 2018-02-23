package io.appium.uiautomator2.core;

import android.app.Instrumentation;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import java.util.regex.Pattern;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoList;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

import static io.appium.uiautomator2.App.model;

public class UiDeviceAdapter {

    private static final String ERR_MSG_CREATE_UIOBJECT2 = "error while creating  UiObject2 object";
    private static final String ERR_MSG_NULL_ROOT_NODE = "Unable to get Root in Active window, " +
            "ERROR: null root node returned by UiTestAutomationBridge.";
    private static final String MSG_SKIP_NULL_ROOT_NODE = "Skipping null root node for window: %s";
    private static final String FIELD_INSTRUMENTATION = "mInstrumentation";
    private static final String FIELD_API_LEVEL_ACTUAL = "API_LEVEL_ACTUAL";
    private static final String FIELD_UI_AUTOMATOR_BRIDGE = "mUiAutomationBridge";
    private static final boolean MULTI_WINDOW = false;
    private static final Pattern UI_SELECTOR_ENDS_WITH_INSTANCE = Pattern.compile("" +
            ".*INSTANCE=\\d+]$");
    private final Instrumentation mInstrumentation;
    private final Integer API_LEVEL_ACTUAL;
    @NonNull
    private final ByMatcherAdapter byMatcherAdapter;
    private final UiAutomatorBridge uiAutomatorBridge;
    @NonNull
    private final UiDevice uiDevice;

    /**
     * UiDevice in android open source project will Support multi-window searches for API level 21,
     * which has not been implemented in UiAutomatorViewer capture layout hierarchy, to be in sync
     * with UiAutomatorViewer customizing getWindowRoots() method to skip the multi-window search
     * based user passed property
     */
    public UiDeviceAdapter(@NonNull final UiDevice uiDevice, @NonNull final ByMatcherAdapter
            byMatcherAdapter, @NonNull final ReflectionUtils
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

    @Nullable
    public AndroidElement findObject(@NonNull final UiSelector selector) {
        waitForIdle();
        final UiObject uiObject = uiDevice.findObject(selector);
        if (uiObject.exists()) {
            return model.getUiObjectElementFactory().create(uiObject);
        }
        return null;
    }

    @Nullable
    public AndroidElement findObject(@NonNull final BySelector selector) {
        waitForIdle();
        final AccessibilityNodeInfo node = byMatcherAdapter.findMatch(selector,
                getWindowRoots());
        if (node == null) {
            return null;
        }
        return createUiObject2Element(selector, node);

    }

    @NonNull
    public AndroidElement findObject(@NonNull final AccessibilityNodeInfo node) {
        waitForIdle();
        final BySelector selector = By.clazz(node.getClassName().toString());
        return createUiObject2Element(selector, node);
    }

    @Nullable
    public AndroidElement findObject(@NonNull final AccessibilityNodeInfoList nodeList) {
        if (nodeList.isEmpty()) {
            return null;
        }
        return findObject(nodeList.get(0));
    }

    @NonNull
    public List<AndroidElement> findObjects(@NonNull final BySelector selector) {
        final List<AccessibilityNodeInfo> nodeList = byMatcherAdapter.findMatches(selector,
                getWindowRoots());
        return createUiObject2Elements(nodeList);
    }

    @NonNull
    public List<AndroidElement> findObjects(@NonNull final UiSelector selector) {
        final List<AndroidElement> elements = new ArrayList<>();
        final String selectorString = selector.toString();
        final boolean useIndex = selectorString.contains("CLASS_REGEX=");
        final boolean endsWithInstance = UI_SELECTOR_ENDS_WITH_INSTANCE.matcher(selectorString)
                .matches();
        Logger.debug("findObjects selector:" + selectorString);

        // If sel is UiSelector[CLASS=android.widget.Button, INSTANCE=0]
        // then invoking instance with a non-0 argument will corrupt the selector.
        //
        // sel.instance(1) will transform the selector into:
        // UiSelector[CLASS=android.widget.Button, INSTANCE=1]
        //
        // The selector now points to an entirely different element.
        if (endsWithInstance) {
            Logger.debug("Selector ends with instance.");
            // There's exactly one element when using instance.
            final AndroidElement instanceObj = findObject(selector);
            if (instanceObj != null && ((UiObject) instanceObj).exists()) {
                elements.add(instanceObj);
            }
            return elements;
        }
        UiObjectAdapter lastFoundObj;
        UiSelector tmp;
        int counter = 0;
        do {
            if (useIndex) {
                Logger.debug("Using index...");
                tmp = selector.index(counter);
            } else {
                Logger.debug("Using instance...");
                tmp = selector.instance(counter);
            }
            Logger.debug("findObjects tmp selector:" + tmp.toString());
            lastFoundObj = (UiObjectAdapter) App.core.getUiDeviceAdapter().findObject(tmp);
            counter++;
            if (lastFoundObj != null && lastFoundObj.exists()) {
                elements.add(lastFoundObj);
            }
        } while (lastFoundObj != null);
        return elements;
    }

    @NonNull
    public List<AndroidElement> findObjects(@NonNull final AccessibilityNodeInfoList nodeList) {
        return createUiObject2Elements(nodeList);
    }

    @NonNull
    private UiObject2Adapter createUiObject2Element(@NonNull final BySelector selector, @NonNull
    final
    AccessibilityNodeInfo node) {
        final Constructor cons = UiObject2.class.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        final Object[] constructorParams = {uiDevice, selector, node};
        try {
            final UiObject2 uiObject2 = (UiObject2) cons.newInstance(constructorParams);
            return model.getUiObjectElementFactory().create(uiObject2);
        } catch (@NonNull InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            Logger.error(ERR_MSG_CREATE_UIOBJECT2 + " " + e);
            throw new UiAutomator2Exception(ERR_MSG_CREATE_UIOBJECT2, e);
        }
    }

    @NonNull
    private List<AndroidElement> createUiObject2Elements(@NonNull final
                                                         List<AccessibilityNodeInfo> nodeList) {
        final List<AndroidElement> result = new ArrayList<>();
        for (final AccessibilityNodeInfo node : nodeList) {
            result.add(createUiObject2Element(By.clazz(node.getClassName().toString()), node));
        }
        return result;
    }

    /**
     * Returns a list containing the root {@link AccessibilityNodeInfo}s for each active window
     */
    @NonNull
    private AccessibilityNodeInfo[] getWindowRoots() throws UiAutomator2Exception {
        waitForIdle();
        final ArrayList<AccessibilityNodeInfo> ret = new ArrayList<>();
        AccessibilityNodeInfo root;
        /*
          TODO: MULTI_WINDOW is disabled, UIAutomatorViewer captures active window properties and
          end users always relay on UIAutomatorViewer while writing tests.
          If we enable MULTI_WINDOW it effects end users.
          https://code.google.com/p/android/issues/detail?id=207569
         */
        if (API_LEVEL_ACTUAL >= Build.VERSION_CODES.LOLLIPOP && MULTI_WINDOW) {
            // Support multi-window searches for API level 21 and up
            for (final AccessibilityWindowInfo window : mInstrumentation.getUiAutomation()
                    .getWindows()) {
                root = window.getRoot();
                if (root == null) {
                    Logger.debug(MSG_SKIP_NULL_ROOT_NODE, window.toString());
                    continue;
                }
                ret.add(root);
            }
            // Prior to API level 21 we can only access the active window
        } else {
            root = mInstrumentation.getUiAutomation().getRootInActiveWindow();
            if (root == null) {
                /*
                 TODO: As we can't proceed to find element with out root node,
                 TODO: retrying for 5 times to get the root node if UiTestAutomationBridge reruns
                  null
                 TODO: need to handle gracefully
                 */
                //AccessibilityNodeInfo should not be null.
                int retryCount = 0;
                while (root == null && retryCount < 5) {
                    SystemClock.sleep(1000);
                    waitForIdle();
                    Logger.debug(ERR_MSG_NULL_ROOT_NODE + ". Retrying: " + retryCount);
                    root = mInstrumentation.getUiAutomation().getRootInActiveWindow();
                    retryCount++;
                }
                if (root == null) {
                    throw new UiAutomator2Exception(ERR_MSG_NULL_ROOT_NODE);
                }
            }
            ret.add(root);
        }
        return ret.toArray(new AccessibilityNodeInfo[ret.size()]);
    }

    public void wake() throws RemoteException {
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

    public void wakeUp() {
        try {
            uiDevice.wakeUp();
        } catch (RemoteException e) {
            Logger.error("RemoeException while wake up device", e);
        }
    }

    @NonNull
    public UiDevice getUiDevice() {
        return uiDevice;
    }
}
