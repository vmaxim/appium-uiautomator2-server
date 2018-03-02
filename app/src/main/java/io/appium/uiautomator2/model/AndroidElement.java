package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.utils.Point;

public interface AndroidElement<T> {

    void clear() throws UiObjectNotFoundException;

    void click() throws UiObjectNotFoundException;

    boolean longClick() throws UiObjectNotFoundException;

    String getText() throws UiObjectNotFoundException;

    String getName() throws UiObjectNotFoundException;

    String getResourceId() throws UiObjectNotFoundException;

    String getContentDescription() throws UiObjectNotFoundException;

    boolean isSelected() throws UiObjectNotFoundException;

    boolean isScrollable() throws UiObjectNotFoundException;

    boolean isLongClickable() throws UiObjectNotFoundException;

    boolean isFocused() throws UiObjectNotFoundException;

    boolean isFocusable() throws UiObjectNotFoundException;

    boolean isClickable() throws UiObjectNotFoundException;

    boolean isChecked() throws UiObjectNotFoundException;

    boolean isCheckable() throws UiObjectNotFoundException;

    boolean isEnabled() throws UiObjectNotFoundException;

    void setText(@NonNull final String text, boolean unicodeKeyboard) throws
            UiObjectNotFoundException, ElementNotFoundException, StaleElementReferenceException;

    Rect getBounds() throws UiObjectNotFoundException;

    AndroidElement findElement(@NonNull final UiSelector sel) throws ElementNotFoundException, NoSuchDriverException, StaleElementReferenceException;

    AndroidElement findElement(@NonNull final BySelector sel) throws ElementNotFoundException, StaleElementReferenceException;

    List<AndroidElement> findElements(@NonNull final UiSelector selector) throws
            ElementNotFoundException, NoSuchDriverException, StaleElementReferenceException;

    List<AndroidElement> findElements(@NonNull final BySelector selector) throws
            StaleElementReferenceException, ElementNotFoundException;

    String getContentDesc() throws UiObjectNotFoundException;

    T getUiObject();

    Point getAbsolutePosition(@NonNull final Point point) throws InvalidCoordinatesException,
            UiObjectNotFoundException;

    boolean dragTo(@NonNull final AndroidElement destObj, final int steps) throws
            UiObjectNotFoundException, InvalidCoordinatesException;

    boolean dragTo(final int destX, final int destY, final int steps) throws InvalidCoordinatesException, UiObjectNotFoundException;

    AccessibilityNodeInfo getAccessibilityNodeInfo() throws StaleElementReferenceException;

    String getClassName() throws UiObjectNotFoundException;

    String getId();

    boolean exists();

}
