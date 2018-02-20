package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoAttributeFoundException;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class UiObjectElement implements AndroidElement {

    private static final Pattern endsWithInstancePattern = Pattern.compile(".*INSTANCE=\\d+]$");
    private static long TIME_IN_MS = 10000;
    private final UiObject element;
    private final ReflectionUtils reflectionUtils;

    public UiObjectElement(UiObject element) {
        this.element = element;
        this.reflectionUtils = new ReflectionUtils();
        reflectionUtils.setTargetObject(element);
    }

    public void click() throws UiObjectNotFoundException {
        element.click();
    }

    public boolean longClick() throws UiObjectNotFoundException {
        return element.longClick();
    }

    public String getText() throws UiObjectNotFoundException {
        // on null returning empty string
        return element.getText() != null ? element.getText() : "";
    }

    public String getName() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public String getClassName() throws UiObjectNotFoundException {
            return element.getClassName();
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
            res = getResourceId();
        } else {
            throw new NoAttributeFoundException(attr);
        }
        return res;
    }

    public boolean getBoolAttribute(final String attr)
            throws UiObjectNotFoundException, NoAttributeFoundException {
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
            res = element.exists();
        } else if ("password".equals(attr)) {
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
        element.setText("");
    }

    public Rect getBounds() throws UiObjectNotFoundException {
        Rect rectangle = element.getVisibleBounds();
        return rectangle;
    }

    public AndroidElement getChild(final Object selector) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException {
        if (selector instanceof BySelector) {
            /**
             * We can't find the child element with BySelector on UiObject,
             * as an alternative creating UiObject2 with UiObject's AccessibilityNodeInfo
             * and finding the child element on UiObject2.
             */
            AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();
            AndroidElement element = App.core.getUiDeviceAdapter().findObject(nodeInfo);
            return element.getChild((BySelector) selector);
        }
        return new UiObjectElement(element.getChild((UiSelector) selector));
    }

    public List<AndroidElement> getChildren(final Object selector, final By by) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        if (selector instanceof BySelector) {
            /**
             * We can't find the child elements with BySelector on UiObject,
             * as an alternative creating UiObject2 with UiObject's AccessibilityNodeInfo
             * and finding the child elements on UiObject2.
             */
            AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();
            UiObject2 uiObject2 = (UiObject2) App.core.getUiDeviceAdapter().findObject(nodeInfo);
            return (List)uiObject2.findObjects((BySelector) selector);
        }
        return (List)this.getChildElements((UiSelector) selector);
    }


    public ArrayList<AndroidElement> getChildElements(final UiSelector sel) throws UiObjectNotFoundException {
        boolean keepSearching = true;
        final String selectorString = sel.toString();
        final boolean useIndex = selectorString.contains("CLASS_REGEX=");
        final boolean endsWithInstance = endsWithInstancePattern.matcher(selectorString).matches();
        Logger.debug("getElements selector:" + selectorString);
        final ArrayList<AndroidElement> elements = new ArrayList<>();

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
            AndroidElement instanceObj = App.core.getUiDeviceAdapter().findObject(sel);
            if (instanceObj != null && ((UiObject) instanceObj.getUiObject()).exists()) {
                elements.add(instanceObj);
            }
            return elements;
        }

        AndroidElement lastFoundObj;

        UiSelector tmp;
        int counter = 0;
        while (keepSearching) {
            if (element == null) {
                Logger.debug("Element] is null: (" + counter + ")");

                if (useIndex) {
                    Logger.debug("  using index...");
                    tmp = sel.index(counter);
                } else {
                    tmp = sel.instance(counter);
                }

                Logger.debug("getElements tmp selector:" + tmp.toString());
                lastFoundObj = App.core.getUiDeviceAdapter().findObject(tmp);
            } else {
                Logger.debug("Element is " + element + ", counter: " + counter);
                lastFoundObj = new UiObjectElement(element.getChild(sel.instance(counter)));
            }
            counter++;
            if (lastFoundObj != null && ((UiObject) lastFoundObj.getUiObject()).exists()) {
                elements.add(lastFoundObj);
            } else {
                keepSearching = false;
            }
        }
        return elements;
    }

    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    public UiObject getUiObject() {
        return element;
    }

    public Point getAbsolutePosition(final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top), false);
    }

    public String getResourceId() throws UiObjectNotFoundException {
        String resourceId = "";

        try {
      /*
       * Unfortunately UiObject does not implement a getResourceId method.
       * There is currently no way to determine the resource-id of a given
       * element represented by UiObject. Until this support is added to
       * UiAutomater, we try to match the implementation pattern that is
       * already used by UiObject for getting attributes using reflection.
       * The returned string matches exactly what is displayed in the
       * UiAutomater inspector.
       */
            AccessibilityNodeInfo node = getAccessibilityNodeInfo(Configurator.getInstance().getWaitForSelectorTimeout());

            if (node == null) {
                throw new UiObjectNotFoundException(element.getSelector().toString());
            }

            resourceId = node.getViewIdResourceName();
        } catch (final Exception e) {
            Logger.error("Exception: " + e + " (" + e.getMessage() + ")");
        }

        return resourceId;
    }

    public boolean dragTo(final int destX, final int destY, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        Point coords = new Point(destX, destY);
        coords = PositionHelper.getDeviceAbsPos(coords);
        return element.dragTo(coords.x.intValue(), coords.y.intValue(), steps);
    }

    public boolean dragTo(final Object destObj, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        if (destObj instanceof UiObject) {
            return element.dragTo((UiObject) destObj, steps);
        }

        if (destObj instanceof UiObject2) {
            android.graphics.Point coords = ((UiObject2) destObj).getVisibleCenter();
            return dragTo(coords.x, coords.y, steps);
        }
        Logger.error("Destination should be either UiObject or UiObject2");
        return false;
    }

    public AccessibilityNodeInfo getAccessibilityNodeInfo(long timeout) {
        return (AccessibilityNodeInfo) reflectionUtils.invoke(reflectionUtils.method(
                "findAccessibilityNodeInfo", long.class), timeout);
    }

    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return getAccessibilityNodeInfo(TIME_IN_MS);
    }
}
