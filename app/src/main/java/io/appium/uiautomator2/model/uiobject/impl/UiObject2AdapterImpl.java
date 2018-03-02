package io.appium.uiautomator2.model.uiobject.impl;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

import static io.appium.uiautomator2.App.core;

public class UiObject2AdapterImpl implements UiObject2Adapter {

    private final UiObject2 element;
    private final ReflectionUtils reflectionUtils;
    private final String id;

    public UiObject2AdapterImpl(UiObject2 element, ReflectionUtils reflectionUtils) {
        this.element = element;
        this.id = UUID.randomUUID().toString();
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetObject(element);
    }

    private boolean isToastElement(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.getClassName().toString().equals(Toast.class.getName());
    }

    @Override
    public void click() {
        element.click();
    }

    @Override
    public boolean longClick() {
        element.longClick();
        return true;
    }

    @Override
    public String getText() {
        AccessibilityNodeInfo nodeInfo = reflectionUtils.getField("mCachedNode");
        /*
          If the given element is TOAST element, we can't perform any operation on
          {@link UiObject2} as it
          not formed with valid AccessibilityNodeInfo, Instead we are using custom created
          AccessibilityNodeInfo of
          TOAST Element to retrieve the Text.
         */
        if (nodeInfo!= null && isToastElement(nodeInfo)) {
            return nodeInfo.getText().toString();
        }

        if (nodeInfo != null && nodeInfo.getRangeInfo() != null) {
            /* Refresh accessibility node info to get actual state of element */
            nodeInfo = getAccessibilityNodeInfo();
            return Float.toString(nodeInfo.getRangeInfo().getCurrent());
        }
        // on null returning empty string
        return element.getText() != null ? element.getText() : "";
    }

    @Override
    public String getName() {
        return element.getContentDescription();
    }

    @Override
    public String getResourceId() {
        return element.getResourceName();
    }

    @Override
    public String getContentDescription() {
        return element.getContentDescription();
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isScrollable() {
        return element.isScrollable();
    }

    @Override
    public boolean isLongClickable() {
        return element.isLongClickable();
    }

    @Override
    public boolean isFocused() {
        return element.isFocused();
    }

    @Override
    public boolean isFocusable() {
        return element.isFocusable();
    }

    @Override
    public boolean isClickable() {
        return element.isClickable();
    }

    @Override
    public boolean isChecked() {
        return element.isChecked();
    }

    @Override
    public boolean isCheckable() {
        return element.isCheckable();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public void setText(@NonNull final String text, boolean unicodeKeyboard) throws ElementNotFoundException, StaleElementReferenceException, UiObjectNotFoundException {
        ElementHelpers.setText(this, text, unicodeKeyboard);
    }

    @Override
    public void clear() {
        element.clear();
    }

    @Override
    public Rect getBounds() {
        return element.getVisibleBounds();
    }

    @Override
    public AndroidElement findElement(@NonNull UiSelector sel) throws ElementNotFoundException, StaleElementReferenceException, NoSuchDriverException {
        return core.getUiObjectFinder().findElement(this, sel);
    }

    @Override
    public AndroidElement findElement(@NonNull BySelector sel) throws ElementNotFoundException {
        return core.getUiObject2Finder().findElement(this, sel);
    }

    @Override
    public List<AndroidElement> findElements(@NonNull UiSelector selector) throws ElementNotFoundException, StaleElementReferenceException, NoSuchDriverException {
        return core.getUiObjectFinder().findElements(this, selector);
    }

    @Override
    public List<AndroidElement> findElements(@NonNull BySelector selector) {
        return core.getUiObject2Finder().findElements(this, selector);
    }

    @Override
    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    @Override
    @SuppressWarnings("unchecked")
    public UiObject2 getUiObject() {
        return element;
    }

    @Override
    public Point getAbsolutePosition(final Point point)
            throws InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top), false);
    }

    @Override
    public boolean dragTo(@NonNull final AndroidElement destObj, final int steps) throws
            UiObjectNotFoundException {
        if (destObj instanceof UiObjectAdapter) {
            int destX = destObj.getBounds().centerX();
            int destY = destObj.getBounds().centerY();
            element.drag(new android.graphics.Point(destX, destY), steps);
            return true;
        }

        if (destObj instanceof UiObject2Adapter) {
            UiObject2 uiObject2 = ((UiObject2Adapter) destObj).getUiObject();
            android.graphics.Point coord = uiObject2.getVisibleCenter();
            element.drag(coord, steps);
            return true;
        }
        Logger.error("Destination should be either UiObject or UiObject2");
        return false;
    }


    @Override
    public boolean dragTo(int destX, int destY, int steps) throws InvalidCoordinatesException {
        Point coords = new Point(destX, destY);
        coords = PositionHelper.getDeviceAbsPos(coords);
        element.drag(new android.graphics.Point(coords.x.intValue(), coords.y.intValue()), steps);
        return true;
    }

    @Override
    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return (AccessibilityNodeInfo) reflectionUtils.invoke(reflectionUtils.method(
                "getAccessibilityNodeInfo"));
    }

    @Override
    public String getClassName() {
        return element.getClassName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean exists() {
        try {
            getAccessibilityNodeInfo();
            return true;
        } catch (StaleObjectException|IllegalStateException e) {
            return false;
        }
    }

}
