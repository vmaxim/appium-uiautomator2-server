package io.appium.uiautomator2.model;

import android.support.test.uiautomator.BySelector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;

public class KnownElements {
    private static Map<String, ManagedAndroidElement> cache = new HashMap<>();

    private static String getCacheKey(ManagedAndroidElement element) {
        for (Map.Entry<String, ManagedAndroidElement> entry : cache.entrySet()) {
            if (entry.getValue().equals(element)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getIdOfElement(ManagedAndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        return null;
    }

    public static ManagedAndroidElement getElementFromCache(String id) {
        return cache.get(id);
    }

//    /**
//     *
//     * @param ui2BySelector, for finding {@link android.support.test.uiautomator.UiObject2} element derived using {@link By}
//     * @param by, user provided selector criteria from appium client.
//     * @return
//     */
//    public static ManagedAndroidElement geElement(final BySelector ui2BySelector, By by) throws
//            ElementNotFoundException, InvalidSelectorException, UiAutomator2Exception,
//            ClassNotFoundException {
//        AndroidElement element = App.core.getUiDeviceAdapter().findObject(ui2BySelector);
//        if (element == null) {
//            throw new ElementNotFoundException();
//        }
//        String id = UUID.randomUUID().toString();
//        ManagedAndroidElement androidElement = App.core.getUiDeviceAdapter().createManagedAndroidElement(id, element, by);
//        cache.put(androidElement.getId(), androidElement);
//        return androidElement;
//    }

    public static String add(ManagedAndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        cache.put(element.getId(), element);
        return element.getId();
    }

    public void clear() {
        if (!cache.isEmpty()) {
            cache.clear();
            System.gc();
        }

    }
}
