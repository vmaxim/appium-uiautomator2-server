package io.appium.uiautomator2.model.uiobject;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.model.AccessibilityNodeInfo2UiSelector;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

import static io.appium.uiautomator2.App.core;
import static io.appium.uiautomator2.App.model;

public class UiObject2Adapter implements AndroidElement {

    private final UiObject2 element;
    private final ReflectionUtils reflectionUtils;

    UiObject2Adapter(UiObject2 element, ReflectionUtils reflectionUtils) {
        this.element = element;
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetObject(element);
    }

    private boolean isToastElement(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.getClassName().toString().equals(Toast.class.getName());
    }

    @Override
    public void click() throws UiObjectNotFoundException {
        element.click();
    }

    @Override
    public boolean longClick() throws UiObjectNotFoundException {
        element.longClick();
        return true;
    }

    @Override
    public String getText() throws UiObjectNotFoundException {
        AccessibilityNodeInfo nodeInfo = reflectionUtils.getField("mCachedNode");
        /*
          If the given element is TOAST element, we can't perform any operation on
          {@link UiObject2} as it
          not formed with valid AccessibilityNodeInfo, Instead we are using custom created
          AccessibilityNodeInfo of
          TOAST Element to retrieve the Text.
         */
        if (isToastElement(nodeInfo)) {
            return nodeInfo.getText().toString();
        }

        if (nodeInfo.getRangeInfo() != null) {
            /* Refresh accessibility node info to get actual state of element */
            nodeInfo = getAccessibilityNodeInfo();
            return Float.toString(nodeInfo.getRangeInfo().getCurrent());
        }
        // on null returning empty string
        return element.getText() != null ? element.getText() : "";
    }

    @Override
    public String getName() throws UiObjectNotFoundException {
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
    public void setText(final String text, boolean unicodeKeyboard) throws UiObjectNotFoundException {
        ElementHelpers.setText(this, text, unicodeKeyboard);
    }

    @Override
    public void clear() throws UiObjectNotFoundException {
        element.clear();
    }

    @Override
    public Rect getBounds() throws UiObjectNotFoundException {
        return element.getVisibleBounds();
    }

    @Nullable
    @Override
    public AndroidElement getChild(final UiSelector selector) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException, NoSuchDriverException {
        /*
          We can't find the child element with UiSelector on UiObject2,
          as an alternative creating UiObject with UiObject2's AccessibilityNodeInfo
          and finding the child element on UiObject.
         */
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();
        UiSelector uiSelector = model.getAccessibilityNodeInfo2UiSelector().generate(nodeInfo);
        AndroidElement elementFromANI = core.getUiDeviceAdapter().findObject(uiSelector);
        /*
            If we can not found current element with ANI,
            then suppose that the current element is stale.
         */
        if (elementFromANI == null) {
            Logger.debug("UiObject2Adapter::getChild(UiSelector): Unable to create UiObject from AccessibilityNodeInfo.");
            throw new StaleObjectException();
        }
        return elementFromANI.getChild(selector);
    }

    @Override
    public AndroidElement getChild(final BySelector selector) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException {
        return model.getUiObjectElementFactory().create(element.findObject(selector));
    }

    @Override
    public List<AndroidElement> getChildren(final UiSelector selector) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException, NoSuchDriverException {
        /*
          We can't find the child elements with UiSelector on UiObject2,
          as an alternative creating UiObject with UiObject2's AccessibilityNodeInfo
          and finding the child elements on UiObject.
         */
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();
        UiSelector uiSelector = new AccessibilityNodeInfo2UiSelector().generate(nodeInfo);
        AndroidElement elementFromANI = core.getUiDeviceAdapter().findObject(uiSelector);
        /*
            If we can not found current element with ANI,
            then suppose that the current element is stale.
         */
        if (elementFromANI == null) {
            Logger.debug("UiObject2Adapter::getChildren(UiSelector): Unable to create UiObject from AccessibilityNodeInfo.");
            throw new StaleObjectException();
        }
        return elementFromANI.getChildren(selector);
    }

    @Override
    public List<AndroidElement> getChildren(final BySelector selector) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        List<UiObject2> uiObject2s = element.findObjects(selector);
        List<AndroidElement> result = new ArrayList<>(uiObject2s.size());
        for(UiObject2 uiObject2 : uiObject2s) {
            AndroidElement androidElement = model.getUiObjectElementFactory().create(uiObject2) ;
            result.add(androidElement);
        }
        return result;
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
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top), false);
    }

    @Override
    public boolean dragTo(final AndroidElement destObj, final int steps) throws UiObjectNotFoundException {
        if (destObj instanceof UiObjectAdapter) {
            int destX = destObj.getBounds().centerX();
            int destY = destObj.getBounds().centerY();
            element.drag(new android.graphics.Point(destX, destY), steps);
            return true;
        }

        if (destObj instanceof UiObject2Adapter) {
            UiObject2 uiObject2 = destObj.getUiObject();
            android.graphics.Point coord = uiObject2.getVisibleCenter();
            element.drag(coord, steps);
            return true;
        }
        Logger.error("Destination should be either UiObject or UiObject2");
        return false;
    }


    @Override
    public boolean dragTo(int destX, int destY, int steps) throws UiObjectNotFoundException, InvalidCoordinatesException {
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

}
