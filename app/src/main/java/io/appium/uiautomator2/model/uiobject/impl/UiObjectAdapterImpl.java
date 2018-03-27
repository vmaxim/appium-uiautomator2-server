package io.appium.uiautomator2.model.uiobject.impl;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.UUID;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
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

public class UiObjectAdapterImpl implements UiObjectAdapter {

    private final UiObject element;
    private final ReflectionUtils reflectionUtils;
    private final String id;

    //@Inject
    public UiObjectAdapterImpl(@NonNull final UiObject element, @NonNull final ReflectionUtils
            reflectionUtils) {
        this.element = element;
        this.id = UUID.randomUUID().toString();
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
    public String getId() {
        return id;
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
    public void setText(@NonNull final String text, boolean unicodeKeyboard) throws
            UiObjectNotFoundException, ElementNotFoundException, StaleElementReferenceException {
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
    public AndroidElement findElement(@NonNull UiSelector selector) throws
            StaleElementReferenceException, ElementNotFoundException {
        return core.getUiSelectorFinder().findElement(this, selector);
    }

    @Override
    public AndroidElement findElement(@NonNull BySelector selector) throws
            ElementNotFoundException, StaleElementReferenceException {
        return core.getBySelectorFinder().findElement(this, selector);
    }

    @Override
    public List<AndroidElement> findElements(@NonNull UiSelector selector) throws
            StaleElementReferenceException, ElementNotFoundException {
        return core.getUiSelectorFinder().findElements(this, selector);
    }

    @Override
    public List<AndroidElement> findElements(@NonNull BySelector selector) throws
            StaleElementReferenceException, ElementNotFoundException {
        return core.getBySelectorFinder().findElements(this, selector);
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
    public Point getAbsolutePosition(@NonNull final Point point)
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
            throws InvalidCoordinatesException, UiObjectNotFoundException {
        Point coords = new Point(destX, destY);
        coords = PositionHelper.getDeviceAbsPos(coords);
        return element.dragTo(coords.x.intValue(), coords.y.intValue(), steps);
    }

    @Override
    public boolean dragTo(@NonNull final AndroidElement destObj, final int steps) throws
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

    @Nullable
    private AccessibilityNodeInfo getAccessibilityNodeInfo(long timeout) {
        return reflectionUtils.invoke(reflectionUtils.method(
                "findAccessibilityNodeInfo", long.class), timeout);
    }

    @Nullable
    @Override
    public AccessibilityNodeInfo getAccessibilityNodeInfo() throws StaleElementReferenceException {
        AccessibilityNodeInfo accessibilityNodeInfo = getAccessibilityNodeInfo(0);
        if (accessibilityNodeInfo == null) {
            throw new StaleElementReferenceException(this);
        }
        return accessibilityNodeInfo;
    }

    public boolean exists() {
        return element.exists();
    }

    @Override
    public String getContentDescription() throws UiObjectNotFoundException {
        return element.getContentDescription();
    }
}
