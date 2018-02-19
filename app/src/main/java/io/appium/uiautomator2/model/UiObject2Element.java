package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoAttributeFoundException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class UiObject2Element implements AndroidElement {

    private final UiObject2 element;
    private final ReflectionUtils reflectionUtils;

    public UiObject2Element(UiObject2 element) {
        this.element = element;
        this.reflectionUtils = new ReflectionUtils();
        reflectionUtils.setTargetObject(element);
    }

    static boolean isToastElement(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.getClassName().toString().equals(Toast.class.getName());
    }

    public void click() throws UiObjectNotFoundException {
        element.click();
    }

    public boolean longClick() throws UiObjectNotFoundException {
        element.longClick();
        return true;
    }

    public String getText() throws UiObjectNotFoundException {
        AccessibilityNodeInfo nodeInfo = reflectionUtils.getField("mCachedNode");
        /**
         * If the given element is TOAST element, we can't perform any operation on {@link UiObject2} as it
         * not formed with valid AccessibilityNodeInfo, Instead we are using custom created AccessibilityNodeInfo of
         * TOAST Element to retrieve the Text.
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

    public String getName() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public String getStringAttribute(final String attr) throws UiObjectNotFoundException, NoAttributeFoundException {
        String res;
        if ("name".equalsIgnoreCase(attr)) {
            res = getText();
        } else if ("contentDescription".equalsIgnoreCase(attr)) {
            res = element.getContentDescription();
        } else if ("text".equalsIgnoreCase(attr)) {
            res = getText();
        } else if ("className".equalsIgnoreCase(attr)) {
            res = element.getClassName();
        } else if ("resourceId".equalsIgnoreCase(attr) || "resource-id".equalsIgnoreCase(attr)) {
            res = element.getResourceName();
        } else {
            throw new NoAttributeFoundException(attr);
        }
        return res;
    }

    public boolean getBoolAttribute(final String attr)
            throws UiObjectNotFoundException, NoAttributeFoundException, UiAutomator2Exception {
        boolean res;
        if ("enabled".equals(attr)) {
            res = element.isEnabled();
        } else if ("checkable".equals(attr)) {
            res = element.isCheckable();
        } else if ("checked".equals(attr)) {
            res = element.isChecked();
        } else if ("clickable".equals(attr)) {
            res = element.isClickable();
        } else if ("focusable".equals(attr)) {
            res = element.isFocusable();
        } else if ("focused".equals(attr)) {
            res = element.isFocused();
        } else if ("longClickable".equals(attr)) {
            res = element.isLongClickable();
        } else if ("scrollable".equals(attr)) {
            res = element.isScrollable();
        } else if ("selected".equals(attr)) {
            res = element.isSelected();
        } else if ("displayed".equals(attr)) {
            res = getAccessibilityNodeInfo() != null ? true : false;
        }  else if ("password".equals(attr)) {
            res = getAccessibilityNodeInfo().isPassword();
        }  else {
            throw new NoAttributeFoundException(attr);
        }
        return res;
    }

    public void setText(final String text, boolean unicodeKeyboard) throws UiObjectNotFoundException {
        ElementHelpers.setText(this, text, unicodeKeyboard);
    }

    public void clear() throws UiObjectNotFoundException {
        element.clear();
    }

    public Rect getBounds() throws UiObjectNotFoundException {
        Rect rectangle = element.getVisibleBounds();
        return rectangle;
    }

    public AndroidElement getChild(final Object selector) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException {
        if (selector instanceof UiSelector) {
            /**
             * We can't find the child element with UiSelector on UiObject2,
             * as an alternative creating UiObject with UiObject2's AccessibilityNodeInfo
             * and finding the child element on UiObject.
             */
            AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();

            UiSelector uiSelector = new UiSelector();
            CustomUiSelector customUiSelector = new CustomUiSelector(uiSelector);
            uiSelector = customUiSelector.getUiSelector(nodeInfo);
            AndroidElement element = App.core.getUiDeviceAdapter().findObject(uiSelector);
            AccessibilityNodeInfo uiObject_nodeInfo = getAccessibilityNodeInfo();
            return element.getChild((UiSelector) selector);
        }
        return new UiObject2Element(element.findObject((BySelector) selector));
    }

    public List<AndroidElement> getChildren(final Object selector, final By by) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        if (selector instanceof UiSelector) {
            /**
             * We can't find the child elements with UiSelector on UiObject2,
             * as an alternative creating UiObject with UiObject2's AccessibilityNodeInfo
             * and finding the child elements on UiObject.
             */
            AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();

            UiSelector uiSelector = new UiSelector();
            CustomUiSelector customUiSelector = new CustomUiSelector(uiSelector);
            uiSelector = customUiSelector.getUiSelector(nodeInfo);
            AndroidElement element = App.core.getUiDeviceAdapter().findObject(uiSelector);
            String id = UUID.randomUUID().toString();
            AndroidElement androidElement = App.core.getUiDeviceAdapter().getManagedAndroidElement(id, element, by);
            return androidElement.getChildren(selector, by);
        }
        return (List)element.findObjects((BySelector) selector);
    }

    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public UiObject2 getUiObject() {
        return element;
    }

    public Point getAbsolutePosition(final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top), false);
    }

    @Override
    public boolean dragTo(Object destObj, int steps) throws UiObjectNotFoundException {
        if (destObj instanceof UiObject) {
            int destX = ((UiObject) destObj).getBounds().centerX();
            int destY = ((UiObject) destObj).getBounds().centerY();
            element.drag(new android.graphics.Point(destX, destY), steps);
            return true;
        } else if (destObj instanceof UiObject2) {
            android.graphics.Point coord = ((UiObject2) destObj).getVisibleCenter();
            element.drag(coord, steps);
            return true;
        } else {
            Logger.error("Destination should be either UiObject or UiObject2");
            return false;
        }
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
