package io.appium.uiautomator2.model.session;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.utils.Logger;

public class CachedElements {
    private Map<String, AndroidElement> cache = new HashMap<>();

    public CachedElements() {
    }

    private String getCacheKey(AndroidElement element) {
        for (Map.Entry<String, AndroidElement> entry : cache.entrySet()) {
            if (entry.getValue().equals(element)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String getId(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        return null;
    }

    @NonNull
    public AndroidElement getElement(String id) throws StaleElementReferenceException {
        AndroidElement element = cache.get(id);
        if (element != null && element.exists()) {
            return element;
        }
        cache.remove(id);
        throw new StaleElementReferenceException(id);
    }

    public String add(@NonNull final AndroidElement element) {
        if (cache.containsValue(element)) {
            Logger.debug("The element is already in cache: " + element);
            return getCacheKey(element);
        }

        final String elementId = element.getId();
        if (cache.containsKey(elementId)) {
            Logger.debug("The element with id is already in cache: " + elementId);
            return elementId;
        }
        cache.put(elementId, element);
        return elementId;
    }

    public List<String> add(List<AndroidElement> elementList) {
        List<String> ids = new ArrayList<>(elementList.size());
        for (AndroidElement element : elementList) {
            String id = add(element);
            ids.add(id);
        }
        return ids;
    }

    public void clear() {
        if (!cache.isEmpty()) {
            cache.clear();
            System.gc();
        }

    }
}
