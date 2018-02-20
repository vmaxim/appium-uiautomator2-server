package io.appium.uiautomator2.model;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoAttributeFoundException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Point;

/**
 * Created by max on 19.02.2018.
 */

public class ManagedAndroidElement implements AndroidElement {
    private AndroidElement element;
    private String id;
    private By by;

    public ManagedAndroidElement(String id, AndroidElement element, By by) {
        this.id = id;
        this.element = element;
        this.by = by;
    }

    public AndroidElement getElement() {
        return element;
    }

    public By getBy() {
        return by;
    }

    public String getId() {
        return id;
    }

    @Override
    public void clear() throws UiObjectNotFoundException {
        element.clear();
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
        return element.getText();
    }

    @Override
    public String getName() throws UiObjectNotFoundException {
        return element.getName();
    }

    @Override
    public String getStringAttribute(String attr) throws UiObjectNotFoundException, NoAttributeFoundException {
        return element.getStringAttribute(attr);
    }

    @Override
    public boolean getBoolAttribute(String attr) throws UiObjectNotFoundException,
            NoAttributeFoundException, UiAutomator2Exception {
        return element.getBoolAttribute(attr);
    }

    @Override
    public void setText(String text, boolean unicodeKeyboard) throws UiObjectNotFoundException {
        element.setText(text, unicodeKeyboard);
    }

    @Override
    public Rect getBounds() throws UiObjectNotFoundException {
        return element.getBounds();
    }

    @Override
    public AndroidElement getChild(Object sel) throws UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        return element.getChild(sel);
    }

    @Override
    public List<AndroidElement> getChildren(Object selector, By by) throws UiObjectNotFoundException, InvalidSelectorException, ClassNotFoundException {
        return element.getChildren(selector, by);
    }

    @Override
    public String getContentDesc() throws UiObjectNotFoundException {
        return element.getContentDesc();
    }

    @Override
    public Object getUiObject() {
        return element.getUiObject();
    }

    @Override
    public Point getAbsolutePosition(Point point) throws UiObjectNotFoundException,
            InvalidCoordinatesException {
        return element.getAbsolutePosition(point);
    }

    @Override
    public boolean dragTo(int destX, int destY, int steps) throws UiObjectNotFoundException,
            InvalidCoordinatesException {
        return element.dragTo(destX, destY, steps);
    }

    @Override
    public boolean dragTo(Object destObj, int steps) throws UiObjectNotFoundException,
            InvalidCoordinatesException {
        return element.dragTo(destObj, steps);
    }

    @Override
    public AccessibilityNodeInfo getAccessibilityNodeInfo() {
        return element.getAccessibilityNodeInfo();
    }

    @Override
    public String getClassName() throws UiObjectNotFoundException {
        return element.getClassName();
    }
}
