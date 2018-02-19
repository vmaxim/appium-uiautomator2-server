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

import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Method;
import java.util.List;

import io.appium.uiautomator2.utils.ReflectionUtils;

public class ByMatcherAdapter {

    private static final String METHOD_FIND_MATCH = "findMatch";
    private static final String METHOD_FIND_MATCHES = "findMatches";
    private final static String BYMATCHER_CLASS = "android.support.test.uiautomator.ByMatcher";
    private final Method findMatch;
    private final Method findMatches;
    private final ReflectionUtils reflectionUtils;
    private final UiDevice uiDevice;

    public ByMatcherAdapter(UiDevice uiDevice, ReflectionUtils reflectionUtils) {
        this.uiDevice = uiDevice;
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetClass(BYMATCHER_CLASS);
        findMatch = reflectionUtils.method(METHOD_FIND_MATCH, UiDevice.class, BySelector.class,
                AccessibilityNodeInfo[].class);
        findMatches = reflectionUtils.method(METHOD_FIND_MATCHES, UiDevice.class, BySelector.class,
                AccessibilityNodeInfo[].class);
    }

    public AccessibilityNodeInfo findMatch(BySelector selector,
                                           AccessibilityNodeInfo... roots) {
        return reflectionUtils.invoke(findMatch, uiDevice, selector, roots);
    }

    public List<AccessibilityNodeInfo> findMatches(BySelector selector,
                                                   AccessibilityNodeInfo... roots) {
        return reflectionUtils.invoke(findMatches, uiDevice, selector, roots);
    }

    public Class getByMatcher() {
        return reflectionUtils.getTargetClass();
    }

}
