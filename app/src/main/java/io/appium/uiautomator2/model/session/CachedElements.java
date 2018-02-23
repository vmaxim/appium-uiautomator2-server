package io.appium.uiautomator2.model.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.appium.uiautomator2.model.AndroidElement;

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

    public String getIdOfElement(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        return null;
    }

    public AndroidElement getElementFromCache(String id) {
        return cache.get(id);
    }

    public String add(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        String id = UUID.randomUUID().toString();
        cache.put(id, element);
        return id;
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
