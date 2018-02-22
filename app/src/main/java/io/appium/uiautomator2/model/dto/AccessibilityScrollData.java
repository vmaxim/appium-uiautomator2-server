package io.appium.uiautomator2.model.dto;

import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccessibilityScrollData {

    private int scrollX;
    private int maxScrollX;
    private int scrollY;
    private int maxScrollY;
    private int fromIndex;
    private int toIndex;
    private int itemCount;

    public AccessibilityScrollData(@NonNull final AccessibilityEvent event) {
        this.scrollX = event.getScrollX();
        this.scrollY = event.getScrollY();
        this.maxScrollX = event.getMaxScrollX();
        this.maxScrollY = event.getMaxScrollY();
        this.fromIndex = event.getFromIndex();
        this.toIndex = event.getToIndex();
        this.itemCount = event.getItemCount();
    }


    public Map<String, Integer> getAsMap () {
        HashMap<String, Integer> map = new HashMap<>();

        map.put("scrollX", scrollX);
        map.put("maxScrollX", maxScrollX);
        map.put("scrollY", scrollY);
        map.put("maxScrollY", maxScrollY);
        map.put("fromIndex", fromIndex);
        map.put("toIndex", toIndex);
        map.put("itemCount", itemCount);

        return Collections.unmodifiableMap(map);
    }
}
