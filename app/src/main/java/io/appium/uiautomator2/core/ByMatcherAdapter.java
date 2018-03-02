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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Method;
import java.util.List;

import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Wrapper for {@link android.support.test.uiautomator.ByMatcher}
 */
public class ByMatcherAdapter {

    private static final String METHOD_FIND_MATCH = "findMatch";
    private static final String METHOD_FIND_MATCHES = "findMatches";
    private final static String BYMATCHER_CLASS = "android.support.test.uiautomator.ByMatcher";

    private final UiDevice uiDevice;
    private final RootNodesFinder rootNodesFinder;
    private final ReflectionUtils reflectionUtils;
    private final Method findMatch;
    private final Method findMatches;

    public ByMatcherAdapter(@NonNull final UiDevice uiDevice,
                            @NonNull final RootNodesFinder rootNodesFinder,
                            @NonNull final ReflectionUtils reflectionUtils) {
        this.uiDevice = uiDevice;
        this.rootNodesFinder = rootNodesFinder;
        this.reflectionUtils = reflectionUtils;
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
     * @return The first {@link AccessibilityNodeInfo} which matched the search criteria or
     * {@code null} otherwise.
     */
    @Nullable
    public AccessibilityNodeInfo findMatch(@NonNull final BySelector selector) {
        return reflectionUtils.invoke(findMatch, uiDevice, selector, rootNodesFinder
                .getWindowRoots());
    }

    /**
     * Find {@link AccessibilityNodeInfo} via
     * {@link android.support.test.uiautomator.ByMatcher#findMatches(UiDevice, BySelector, AccessibilityNodeInfo...)}
     * method
     *
     * @param selector The {@link BySelector} criteria used to determine if a node is a match.
     * @return A list containing all of the nodes which matched the search criteria.
     */
    @SuppressWarnings("ConstantConditions")
    @NonNull
    public List<AccessibilityNodeInfo> findMatches(@NonNull final BySelector selector) {
        return reflectionUtils.invoke(findMatches, uiDevice, selector, rootNodesFinder
                .getWindowRoots());
    }
}
