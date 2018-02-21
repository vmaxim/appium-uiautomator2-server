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

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class AccessibilityInteractionClientAdapter {

    public static final String ACCESSIBILITY_INTERACTION_CLIENT = "android.view.accessibility.AccessibilityInteractionClient";
    public static final String METHOD_GET_INSTANCE = "getInstance";
    public static final String METHOD_CLEAR_CACHE = "clearCache";

    private final ReflectionUtils reflectionUtils;

    public AccessibilityInteractionClientAdapter(ReflectionUtils reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetClass(ACCESSIBILITY_INTERACTION_CLIENT);
        final Object instance = reflectionUtils.method(METHOD_GET_INSTANCE);
        reflectionUtils.setTargetObject(instance);
    }

    /**
     * Clears the in-process Accessibility cache, removing any stale references. Because the
     * AccessibilityInteractionClient singleton stores copies of AccessibilityNodeInfo instances,
     * calls to public APIs such as `recycle` do not guarantee cached references get updated. See
     * the android.view.accessibility AIC and ANI source code for more information.
     */
    public boolean clearAccessibilityCache() throws UiAutomator2Exception {
        boolean success = false;
        try {
            reflectionUtils.invoke(reflectionUtils.method(METHOD_CLEAR_CACHE));
            success = true;
        } catch (UiAutomator2Exception e) {
            Logger.error("Failed to clear Accessibility Node cache. ", e);
        }
        return success;
    }
}