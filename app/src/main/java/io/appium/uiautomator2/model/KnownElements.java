package io.appium.uiautomator2.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KnownElements {
    private static Map<String, AndroidElement> cache = new HashMap<>();

    private static String getCacheKey(AndroidElement element) {
        for (Map.Entry<String, AndroidElement> entry : cache.entrySet()) {
            if (entry.getValue().equals(element)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getIdOfElement(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        return null;
    }

    public static AndroidElement getElementFromCache(String id) {
        return cache.get(id);
    }

    public static String add(AndroidElement element) {
        if (cache.containsValue(element)) {
            return getCacheKey(element);
        }
        String id = UUID.randomUUID().toString();
        cache.put(id, element);
        return id;
    }

    public static List<String> add(List<AndroidElement> elementList) {
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
