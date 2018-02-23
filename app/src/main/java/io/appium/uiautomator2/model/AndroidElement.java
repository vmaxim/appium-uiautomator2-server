package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoAttributeFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.SessionRemovedException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Point;

public interface AndroidElement {

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

    void setText(@NonNull final String text, boolean unicodeKeyboard) throws UiObjectNotFoundException;

    Rect getBounds() throws UiObjectNotFoundException;

    AndroidElement getChild(@NonNull final UiSelector sel) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException, NoSuchDriverException;

    AndroidElement getChild(@NonNull final BySelector sel) throws UiObjectNotFoundException,
            InvalidSelectorException, ClassNotFoundException;

    List<AndroidElement> getChildren(@NonNull final UiSelector selector) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException, NoSuchDriverException;

    List<AndroidElement> getChildren(@NonNull final BySelector selector) throws
            UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException;

    String getContentDesc() throws UiObjectNotFoundException;

    <T> T getUiObject();

    Point getAbsolutePosition(@NonNull final Point point)
            throws UiObjectNotFoundException, InvalidCoordinatesException;

    boolean dragTo(@NonNull final AndroidElement destObj, final int steps) throws UiObjectNotFoundException, InvalidCoordinatesException;

    boolean dragTo(final int destX, final int destY, final int steps) throws UiObjectNotFoundException, InvalidCoordinatesException;

    AccessibilityNodeInfo getAccessibilityNodeInfo();

    String getClassName() throws UiObjectNotFoundException;

}
