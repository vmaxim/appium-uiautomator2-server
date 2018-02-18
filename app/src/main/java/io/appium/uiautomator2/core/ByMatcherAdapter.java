package io.appium.uiautomator2.core;

import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Method;

import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Created by max on 18.02.2018.
 */

public class ByMatcherAdapter {

    private final static String BYMATCHER_CLASS = "android.support.test.uiautomator.ByMatcher";
    private final Method findMatch;
    private final Method findMatches;
    private ReflectionUtils reflectionUtils;

    public ByMatcherAdapter(ReflectionUtils reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetClass(BYMATCHER_CLASS);
        findMatch = getFindMatchMethod();
        findMatches = getFindMatchesMethod();
    }

    private Method getFindMatchMethod() {
        return reflectionUtils.method("findMatch", UiDevice.class, BySelector.class,
                AccessibilityNodeInfo[].class);
    }

    private Method getFindMatchesMethod() {
        return reflectionUtils.method("findMatches", UiDevice.class, BySelector.class,
                AccessibilityNodeInfo[].class);
    }

    public AccessibilityNodeInfo findMatch(UiDevice uiDevice, BySelector selector,
                                           AccessibilityNodeInfo... roots) {
        return (AccessibilityNodeInfo) reflectionUtils.invoke(findMatch, uiDevice, selector, roots);
    }

    public AccessibilityNodeInfo findMatches(UiDevice uiDevice, BySelector selector,
                                             AccessibilityNodeInfo... roots) {
        return (AccessibilityNodeInfo) reflectionUtils.invoke(findMatches, uiDevice, selector,
                roots);
    }

    public Class getByMatcher() {
        return reflectionUtils.getTargetClass();
    }

}
