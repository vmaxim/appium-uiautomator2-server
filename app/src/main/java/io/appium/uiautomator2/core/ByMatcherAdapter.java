/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.appium.uiautomator2.core;

import android.app.UiAutomation;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Wrapper for {@link android.support.test.uiautomator.ByMatcher}
 */
public class ByMatcherAdapter {

    private static final String MSG_SKIP_NULL_ROOT_NODE = "Skipping null root node for window: %s";
    private static final String ERR_MSG_NULL_ROOT_NODE = "Unable to get Root in Active window, " +
            "ERROR: null root node returned by UiTestAutomationBridge.";
    private static final int ROOT_NODE_POLLING_INTERVAL = 1000;
    private static final int ROOT_NODE_RETRIES_COUNT = 5;
    private static final String METHOD_FIND_MATCH = "findMatch";
    private static final String METHOD_FIND_MATCHES = "findMatches";
    private final static String BYMATCHER_CLASS = "android.support.test.uiautomator.ByMatcher";

    private final UiAutomation uiAutomation;
    private final UiDevice uiDevice;
    private final ReflectionUtils reflectionUtils;
    private final Integer apiLevelActual;
    private final Boolean allowMultiWindow;
    private final Method findMatch;
    private final Method findMatches;

    public ByMatcherAdapter(@NonNull final UiDevice uiDevice,
                            @NonNull final UiAutomation uiAutomation,
                            @NonNull final ReflectionUtils reflectionUtils,
                            @NonNull @Named("apiLevelActual") final Integer apiLevelActual,
                            @NonNull @Named("allowMultiWindow") final Boolean allowMultiWindow) {
        this.reflectionUtils = reflectionUtils;
        this.uiDevice = uiDevice;
        this.uiAutomation = uiAutomation;
        this.apiLevelActual = apiLevelActual;
        this.allowMultiWindow = allowMultiWindow;
        reflectionUtils.setTargetClass(BYMATCHER_CLASS);
        findMatch = reflectionUtils.method(METHOD_FIND_MATCH, UiDevice.class, BySelector.class,
                AccessibilityNodeInfo[].class);
        findMatches = reflectionUtils.method(METHOD_FIND_MATCHES, UiDevice.class, BySelector.class,
                AccessibilityNodeInfo[].class);
    }

    /**
     * Find {@link AccessibilityNodeInfo} via
     * {@link android.support.test.uiautomator.ByMatcher#findMatch(UiDevice, BySelector, AccessibilityNodeInfo...)}
     * method
     *
     * @param selector The {@link BySelector} criteria used to determine if a node is a match.
     * @return The first {@link AccessibilityNodeInfo} which matched the search criteria.
     */
    AccessibilityNodeInfo findMatch(@NonNull final BySelector selector) {
        return reflectionUtils.invoke(findMatch, uiDevice, selector, getWindowRoots());
    }

    /**
     * Find {@link AccessibilityNodeInfo} via
     * {@link android.support.test.uiautomator.ByMatcher#findMatches(UiDevice, BySelector, AccessibilityNodeInfo...)}
     * method
     *
     * @param selector The {@link BySelector} criteria used to determine if a node is a match.
     * @return A list containing all of the nodes which matched the search criteria.
     */
    List<AccessibilityNodeInfo> findMatches(@NonNull final BySelector selector) {
        return reflectionUtils.invoke(findMatches, uiDevice, selector, getWindowRoots());
    }

    /**
     * UiAutomation in android open source project will Support multi-window searches for API level 21,
     * which has not been implemented in UiAutomatorViewer capture layout hierarchy, to be in sync
     * with UiAutomatorViewer customizing getWindowRoots() method to skip the multi-window search
     * based user passed property
     *
     * @return List of window roots
     */
    @NonNull
    private AccessibilityNodeInfo[] getWindowRoots() {
        final List<AccessibilityNodeInfo> ret;
        /*
          TODO: MULTI_WINDOW is disabled, UIAutomatorViewer captures active window properties and
          end users always relay on UIAutomatorViewer while writing tests.
          If we enable MULTI_WINDOW it effects end users.
          https://code.google.com/p/android/issues/detail?id=207569
         */
        if (apiLevelActual >= Build.VERSION_CODES.LOLLIPOP && allowMultiWindow) {
            ret = getMultiWindowRoots();
        } else {
            ret = getActiveWindowRoot();
        }
        return ret.toArray(new AccessibilityNodeInfo[ret.size()]);
    }

    /**
     *
     * @return List of roots of all interactable windows
     */
    @RequiresApi(21)
    private List<AccessibilityNodeInfo> getMultiWindowRoots() {
        assert uiAutomation != null;
        final List<AccessibilityNodeInfo> result = new ArrayList<>();
        AccessibilityNodeInfo root;
        // Support multi-window searches for API level 21 and up
        for (final AccessibilityWindowInfo window : uiAutomation.getWindows()) {
            root = window.getRoot();
            if (root == null) {
                Logger.debug(MSG_SKIP_NULL_ROOT_NODE, window.toString());
                continue;
            }
            result.add(root);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     *
     * @return Active window root
     */
    private List<AccessibilityNodeInfo> getActiveWindowRoot() {
        assert uiAutomation != null;
        AccessibilityNodeInfo root = uiAutomation.getRootInActiveWindow();
        if (root == null) {
            /*
             TODO: As we can't proceed to find element with out root node,
             TODO: retrying for 5 times to get the root node if UiTestAutomationBridge reruns null
             TODO: need to handle gracefully
             */
            //AccessibilityNodeInfo should not be null.
            int retryCount = 0;
            while (root == null && retryCount < ROOT_NODE_RETRIES_COUNT) {
                SystemClock.sleep(ROOT_NODE_POLLING_INTERVAL);
                Logger.debug(ERR_MSG_NULL_ROOT_NODE + ". Retrying: " + retryCount);
                root = uiAutomation.getRootInActiveWindow();
                retryCount++;
            }
            if (root == null) {
                throw new UiAutomator2Exception(ERR_MSG_NULL_ROOT_NODE);
            }
        }
        return Collections.singletonList(root);
    }

}
