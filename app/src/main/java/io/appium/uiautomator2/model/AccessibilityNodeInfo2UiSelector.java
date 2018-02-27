package io.appium.uiautomator2.model;


import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Map;

import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.utils.Attribute;

import static io.appium.uiautomator2.utils.Attribute.INDEX;

public class AccessibilityNodeInfo2UiSelector {

    /**
     * returns UiSelector object, based on UiElementANISnapshot attributes
     * @param node
     * @return UiSelector
     */
    public UiSelector generate(AccessibilityNodeInfo node) throws NoSuchDriverException {
        XPathFinder.refreshUiElementTree();
        UiElementANISnapshot uiAutoEl = UiElementANISnapshot.map.get(node);
        UiSelector uiSelector = new UiSelector();
        for(Map.Entry<Attribute, Object> entry : uiAutoEl.getAttributes().entrySet()) {
            Attribute attribute = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof Boolean) {
                Boolean booleanValue = Boolean.class.cast(value);
                switch (attribute) {
                    case CHECKABLE:
                        uiSelector = uiSelector.checkable(booleanValue);
                        break;
                    case CHECKED:
                        uiSelector = uiSelector.checked(booleanValue);
                        break;
                    case CLICKABLE:
                        uiSelector = uiSelector.clickable(booleanValue);
                        break;
                    case ENABLED:
                        uiSelector = uiSelector.enabled(booleanValue);
                        break;
                    case FOCUSABLE:
                        uiSelector = uiSelector.focusable(booleanValue);
                        break;
                    case LONG_CLICKABLE:
                        uiSelector = uiSelector.longClickable(booleanValue);
                        break;
                    case SCROLLABLE:
                        uiSelector = uiSelector.scrollable((Boolean) value);
                        break;
                    case SELECTED:
                        uiSelector = uiSelector.selected(booleanValue);
                        break;
                }
            }
            if (value instanceof Integer && attribute == INDEX) {
                uiSelector = uiSelector.index((Integer) value);
            }
            if (value instanceof String) {
                String stringValue = String.class.cast(value);
                switch (attribute) {
                    case PACKAGE:
                        uiSelector = uiSelector.packageName(stringValue);
                        break;
                    case CLASS:
                        uiSelector = uiSelector.className(stringValue);
                        break;
                    case TEXT:
                        uiSelector = uiSelector.text(stringValue);
                        break;
                    case CONTENT_DESC:
                        uiSelector = uiSelector.descriptionContains(stringValue);
                        break;
                    case RESOURCE_ID:
                        uiSelector = uiSelector.resourceId(stringValue);
                        break;
                }
            }
        }
        return uiSelector;
    }

}
