package io.appium.uiautomator2.model;


import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import io.appium.uiautomator2.utils.Attribute;

import static io.appium.uiautomator2.model.UiAutomationElement.charSequenceToString;

public class CustomUiSelector {

    private UiAutomationElement uiAutomationElement;
    private UiSelector uiSelector = new UiSelector();

    /**
     * returns UiSelector object, based on UiAutomationElement attributes
     * @param node
     * @return
     */
    public UiSelector createFromAccessibilityNodeInfo(AccessibilityNodeInfo node) {
        XPathFinder.refreshUiElementTree();
        uiAutomationElement = UiAutomationElement.map.get(node);
        put(Attribute.PACKAGE, charSequenceToString(uiAutomationElement.getPackageName()));
        put(Attribute.CLASS, charSequenceToString(uiAutomationElement.getClassName()));
        put(Attribute.TEXT, charSequenceToString(uiAutomationElement.getText()));
        put(Attribute.CONTENT_DESC, charSequenceToString(uiAutomationElement.getContentDescription()));
        put(Attribute.RESOURCE_ID, charSequenceToString(uiAutomationElement.getResourceId()));
        put(Attribute.CHECKABLE, uiAutomationElement.isCheckable());
        put(Attribute.CHECKED, uiAutomationElement.isChecked());
        put(Attribute.CLICKABLE, uiAutomationElement.isClickable());
        put(Attribute.ENABLED, uiAutomationElement.isEnabled());
        put(Attribute.FOCUSABLE, uiAutomationElement.isFocusable());
        put(Attribute.FOCUSED, uiAutomationElement.isFocused());
        put(Attribute.LONG_CLICKABLE, uiAutomationElement.isLongClickable());
        put(Attribute.PASSWORD, uiAutomationElement.isPassword());
        put(Attribute.SCROLLABLE, uiAutomationElement.isScrollable());
        put(Attribute.SELECTED, uiAutomationElement.isSelected());
        put(Attribute.INDEX, uiAutomationElement.getIndex());

        return uiSelector;
    }

    private void put(Attribute key, Object value) {
        if (value == null) {
            return ;
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
                uiSelector = uiSelector.focusable((Boolean) value);
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
