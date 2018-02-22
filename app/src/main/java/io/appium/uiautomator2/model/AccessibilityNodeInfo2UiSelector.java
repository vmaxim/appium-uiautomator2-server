package io.appium.uiautomator2.model;


import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

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
    public UiSelector generate(AccessibilityNodeInfo node) {
        XPathFinder.refreshUiElementTree();
        UiAutomationElement uiAutoEl = UiAutomationElement.map.get(node);

        UiSelector uiSelector = new UiSelector();
        put(uiSelector, PACKAGE, charSequenceToString(uiAutoEl.getPackageName()));
        put(uiSelector, CLASS, charSequenceToString(uiAutoEl.getClassName()));
        put(uiSelector, TEXT, charSequenceToString(uiAutoEl.getText()));
        put(uiSelector, CONTENT_DESC, charSequenceToString(uiAutoEl.getContentDescription()));
        put(uiSelector, RESOURCE_ID, charSequenceToString(uiAutoEl.getResourceId()));
        put(uiSelector, CHECKABLE, uiAutoEl.isCheckable());
        put(uiSelector, CHECKED, uiAutoEl.isChecked());
        put(uiSelector, CLICKABLE, uiAutoEl.isClickable());
        put(uiSelector, ENABLED, uiAutoEl.isEnabled());
        put(uiSelector, FOCUSABLE, uiAutoEl.isFocusable());
        put(uiSelector, FOCUSED, uiAutoEl.isFocused());
        put(uiSelector, LONG_CLICKABLE, uiAutoEl.isLongClickable());
        put(uiSelector, PASSWORD, uiAutoEl.isPassword());
        put(uiSelector, SCROLLABLE, uiAutoEl.isScrollable());
        put(uiSelector, SELECTED, uiAutoEl.isSelected());
        put(uiSelector, INDEX, uiAutoEl.getIndex());
        return uiSelector;
    }

    private void put(UiSelector uiSelector, Attribute key, Object value) {
        if (value == null) {
            return;
        }
        switch (key) {
            case PACKAGE:
                uiSelector = uiSelector.packageName((String) value);
                break;
            case CLASS:
                uiSelector = uiSelector.className((String) value);
                break;
            case TEXT:
                uiSelector = uiSelector.text((String) value);
                break;
            case CONTENT_DESC:
                uiSelector = uiSelector.descriptionContains((String) value);
                break;
            case RESOURCE_ID:
                uiSelector = uiSelector.resourceId((String) value);
                break;
            case CHECKABLE:
                uiSelector = uiSelector.checkable((Boolean) value);
                break;
            case CHECKED:
                uiSelector = uiSelector.checked((Boolean) value);
                break;
            case CLICKABLE:
                uiSelector = uiSelector.clickable((Boolean) value);
                break;
            case ENABLED:
                uiSelector = uiSelector.enabled((Boolean) value);
                break;
            case FOCUSABLE:
                uiSelector =  uiSelector.focusable((Boolean) value);
                break;
            case LONG_CLICKABLE:
                uiSelector = uiSelector.longClickable((Boolean) value);
                break;
            case SCROLLABLE:
                uiSelector = uiSelector.scrollable((Boolean) value);
                break;
            case SELECTED:
                uiSelector = uiSelector.selected((Boolean) value);
                break;
            case INDEX:
                uiSelector = uiSelector.index((Integer) value);
                break;
            default: //ignore
        }
    }
}
