package io.appium.uiautomator2.model;


import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.utils.Attribute;

import static io.appium.uiautomator2.model.UiAutomationElement.charSequenceToString;
import static io.appium.uiautomator2.utils.Attribute.CHECKABLE;
import static io.appium.uiautomator2.utils.Attribute.CHECKED;
import static io.appium.uiautomator2.utils.Attribute.CLASS;
import static io.appium.uiautomator2.utils.Attribute.CLICKABLE;
import static io.appium.uiautomator2.utils.Attribute.CONTENT_DESC;
import static io.appium.uiautomator2.utils.Attribute.ENABLED;
import static io.appium.uiautomator2.utils.Attribute.FOCUSABLE;
import static io.appium.uiautomator2.utils.Attribute.FOCUSED;
import static io.appium.uiautomator2.utils.Attribute.INDEX;
import static io.appium.uiautomator2.utils.Attribute.LONG_CLICKABLE;
import static io.appium.uiautomator2.utils.Attribute.PACKAGE;
import static io.appium.uiautomator2.utils.Attribute.PASSWORD;
import static io.appium.uiautomator2.utils.Attribute.RESOURCE_ID;
import static io.appium.uiautomator2.utils.Attribute.SCROLLABLE;
import static io.appium.uiautomator2.utils.Attribute.SELECTED;
import static io.appium.uiautomator2.utils.Attribute.TEXT;

public class AccessibilityNodeInfo2UiSelector {

    /**
     * returns UiSelector object, based on UiAutomationElement attributes
     * @param node
     * @return
     */
    public UiSelector generate(AccessibilityNodeInfo node) throws NoSuchDriverException {
        XPathFinder.refreshUiElementTree();
        UiAutomationElement uiAutoEl = UiAutomationElement.map.get(node);

        UiSelector uiSelector = new UiSelector();
        uiSelector = put(uiSelector, PACKAGE, charSequenceToString(uiAutoEl.getPackageName()));
        uiSelector = put(uiSelector, CLASS, charSequenceToString(uiAutoEl.getClassName()));
        uiSelector = put(uiSelector, TEXT, charSequenceToString(uiAutoEl.getText()));
        uiSelector = put(uiSelector, CONTENT_DESC, charSequenceToString(uiAutoEl.getContentDescription()));
        uiSelector = put(uiSelector, RESOURCE_ID, charSequenceToString(uiAutoEl.getResourceId()));
        uiSelector = put(uiSelector, CHECKABLE, uiAutoEl.isCheckable());
        uiSelector = put(uiSelector, CHECKED, uiAutoEl.isChecked());
        uiSelector = put(uiSelector, CLICKABLE, uiAutoEl.isClickable());
        uiSelector = put(uiSelector, ENABLED, uiAutoEl.isEnabled());
        uiSelector = put(uiSelector, FOCUSABLE, uiAutoEl.isFocusable());
        uiSelector = put(uiSelector, FOCUSED, uiAutoEl.isFocused());
        uiSelector = put(uiSelector, LONG_CLICKABLE, uiAutoEl.isLongClickable());
        uiSelector = put(uiSelector, PASSWORD, uiAutoEl.isPassword());
        uiSelector = put(uiSelector, SCROLLABLE, uiAutoEl.isScrollable());
        uiSelector = put(uiSelector, SELECTED, uiAutoEl.isSelected());
        uiSelector = put(uiSelector, INDEX, uiAutoEl.getIndex());
        return uiSelector;
    }

    private UiSelector put(UiSelector uiSelector, Attribute key, Object value) {
        if (value == null) {
            return uiSelector;
        }
        switch (key) {
            case PACKAGE:
                return uiSelector.packageName((String) value);
            case CLASS:
                return uiSelector.className((String) value);
            case TEXT:
                return uiSelector.text((String) value);
            case CONTENT_DESC:
                return uiSelector.descriptionContains((String) value);
            case RESOURCE_ID:
                return uiSelector.resourceId((String) value);
            case CHECKABLE:
                return uiSelector.checkable((Boolean) value);
            case CHECKED:
                return uiSelector.checked((Boolean) value);
            case CLICKABLE:
                return uiSelector.clickable((Boolean) value);
            case ENABLED:
                return uiSelector.enabled((Boolean) value);
            case FOCUSABLE:
                return uiSelector.focusable((Boolean) value);
            case LONG_CLICKABLE:
                return uiSelector.longClickable((Boolean) value);
            case SCROLLABLE:
                return uiSelector.scrollable((Boolean) value);
            case SELECTED:
                return uiSelector.selected((Boolean) value);
            case INDEX:
                return uiSelector.index((Integer) value);
            default: //ignore
                return uiSelector;
        }
    }
}
