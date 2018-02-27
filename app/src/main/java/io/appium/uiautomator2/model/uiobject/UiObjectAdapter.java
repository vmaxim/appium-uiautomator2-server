package io.appium.uiautomator2.model.uiobject;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

import static io.appium.uiautomator2.App.core;
import static io.appium.uiautomator2.App.model;

public class UiObjectAdapter implements AndroidElement {

    private static final Pattern ENDS_WITH_INSTANCE_PATTERN = Pattern.compile(".*INSTANCE=\\d+]$");
    private static long TIME_IN_MS = 10000;
    private final UiObject element;
    @NonNull
    ReflectionUtils reflectionUtils;

    @Inject
    UiObjectAdapter(@NonNull final UiObject element, @NonNull final ReflectionUtils reflectionUtils) {
        this.element = element;
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetObject(element);
    }

    @Override
    public void click() throws UiObjectNotFoundException {
        element.click();
    }

    @Override
    public boolean longClick() throws UiObjectNotFoundException {
        return element.longClick();
    }

    @Override
    public String getText() throws UiObjectNotFoundException {
        // on null returning empty string
        return element.getText() != null ? element.getText() : "";
    }

    @Override
    public String getName() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    @Override
    public String getClassName() throws UiObjectNotFoundException {
        return element.getClassName();
    }

    @Override
    public boolean isSelected() throws UiObjectNotFoundException {
        return element.isSelected();
    }

    @Override
    public boolean isScrollable() throws UiObjectNotFoundException {
        return element.isScrollable();
    }

    @Override
    public boolean isLongClickable() throws UiObjectNotFoundException {
        return element.isLongClickable();
    }

    @Override
    public boolean isFocused() throws UiObjectNotFoundException {
        return element.isFocused();
    }

    @Override
    public boolean isFocusable() throws UiObjectNotFoundException {
        return element.isFocusable();
    }

    @Override
    public boolean isClickable() throws UiObjectNotFoundException {
        return element.isClickable();
    }

    @Override
    public boolean isChecked() throws UiObjectNotFoundException {
        return element.isChecked();
    }

    @Override
    public boolean isCheckable() throws UiObjectNotFoundException {
        return element.isCheckable();
    }

    @Override
    public boolean isEnabled() throws UiObjectNotFoundException {
        return element.isEnabled();
    }

    @Override
    public void setText(final String text, boolean unicodeKeyboard) throws
            UiObjectNotFoundException {
        ElementHelpers.setText(this, text, unicodeKeyboard);
    }

    @Override
    public void clear() throws UiObjectNotFoundException {
        element.setText("");
    }

    @Override
    public Rect getBounds() throws UiObjectNotFoundException {
        return element.getVisibleBounds();
    }

    @Override
    public AndroidElement getChild(final BySelector selector) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException {
        /*
          We can't find the child element with BySelector on UiObject,
          as an alternative creating UiObject2 with UiObject's AccessibilityNodeInfo
          and finding the child element on UiObject2.
         */
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();
        AndroidElement elementFromANI = core.getCoreFacade().findElement(nodeInfo);
        /*
            If we can not found current element with ANI,
            then suppose that the current element is stale.
         */
        if (elementFromANI == null) {
            Logger.debug("UiObjectAdapter::getChild(BySelector): Unable to create UiObject from " +
                    "AccessibilityNodeInfo.");
            throw new StaleObjectException();
        }
        return elementFromANI.getChild(selector);
    }

    @Override
    public AndroidElement getChild(@NonNull final UiSelector selector) throws UiObjectNotFoundException {
        return model.getUiObjectAdapterFactory().create(element.getChild(selector));
    }

    @Override
    public List<AndroidElement> getChildren(final BySelector selector) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        /*
            We can't find the child elements with BySelector on UiObject,
            as an alternative creating UiObject2 with UiObject's AccessibilityNodeInfo
            and finding the child elements on UiObject2.
         */
        AccessibilityNodeInfo nodeInfo = getAccessibilityNodeInfo();
        AndroidElement elementFromANI = core.getCoreFacade().findElement(nodeInfo);
        /*
            If we can not found current element with ANI,
            then suppose that the current element is stale.
         */
        if (elementFromANI == null) {
            Logger.debug("UiObjectAdapter::getChildren(BySelector): Unable to create UiObject " +
                    "from AccessibilityNodeInfo.");
            throw new StaleObjectException();
        }
        return elementFromANI.getChildren(selector);
    }

    @Override
    public List<AndroidElement> getChildren(final UiSelector selector) throws
            UiObjectNotFoundException {
        final String selectorString = selector.toString();
        final boolean endsWithInstance = ENDS_WITH_INSTANCE_PATTERN.matcher(selectorString).matches();
        Logger.debug("getElements selector:" + selectorString);
        final ArrayList<AndroidElement> elements = new ArrayList<>();

        /*
            If sel is UiSelector[CLASS=android.widget.Button, INSTANCE=0]
            then invoking instance with a non-0 argument will corrupt the selector.
            sel.instance(1) will transform the selector into:
            UiSelector[CLASS=android.widget.Button, INSTANCE=1]
            The selector now points to an entirely different element.
         */
        if (endsWithInstance) {
            return core.getCoreFacade().findElements(selector);
        }

        UiObjectAdapter lastFoundObj;
        int counter = 0;
        do {
            Logger.debug("Element is " + element + ", counter: " + counter);
            lastFoundObj = (UiObjectAdapter) getChild(selector.instance(counter));
            counter++;
            if (lastFoundObj.getUiObject() != null && lastFoundObj.exists()) {
                elements.add(lastFoundObj);
            }
        } while (lastFoundObj.exists());
        return elements;
    }

    @Override
    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }

    @SuppressWarnings("unchecked")
    public UiObject getUiObject() {
        return element;
    }

    @Override
    public Point getAbsolutePosition(final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        final Rect rect = this.getBounds();

        Logger.debug("Element bounds: " + rect.toShortString());

        return PositionHelper.getAbsolutePosition(point, rect, new Point(rect.left, rect.top),
                false);
    }

    @Override
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
            AccessibilityNodeInfo node = getAccessibilityNodeInfo(Configurator.getInstance()
                    .getWaitForSelectorTimeout());

            if (node == null) {
                throw new UiObjectNotFoundException(element.getSelector().toString());
            }

            resourceId = node.getViewIdResourceName();
        } catch (final Exception e) {
            Logger.error("Exception: " + e + " (" + e.getMessage() + ")");
        }

        return resourceId;
    }

    @Override
    public boolean dragTo(final int destX, final int destY, final int steps)
            throws UiObjectNotFoundException, InvalidCoordinatesException {
        Point coords = new Point(destX, destY);
        coords = PositionHelper.getDeviceAbsPos(coords);
        return element.dragTo(coords.x.intValue(), coords.y.intValue(), steps);
    }

    @Override
    public boolean dragTo(final AndroidElement destObj, final int steps) throws
            UiObjectNotFoundException, InvalidCoordinatesException {
        if (destObj instanceof UiObjectAdapter) {
            UiObjectAdapter uiObjectAdapter = (UiObjectAdapter) destObj;
            return element.dragTo(uiObjectAdapter.getUiObject(), steps);
        }

        if (destObj instanceof UiObject2Adapter) {
            UiObject2Adapter uiObject2Adapter = (UiObject2Adapter) destObj;
            android.graphics.Point coords = uiObject2Adapter.getUiObject().getVisibleCenter();
            return dragTo(coords.x, coords.y, steps);
        }
        Logger.error("Destination should be either UiObject or UiObject2");
        return false;
    }

    private AccessibilityNodeInfo getAccessibilityNodeInfo(long timeout) {
        return (AccessibilityNodeInfo) reflectionUtils.invoke(reflectionUtils.method(
                "findAccessibilityNodeInfo", long.class), timeout);
    }

    @Override
    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return getAccessibilityNodeInfo(TIME_IN_MS);
    }

    public boolean exists() {
        return element != null && element.exists();
    }

    @Override
    public String getContentDescription() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }
}
