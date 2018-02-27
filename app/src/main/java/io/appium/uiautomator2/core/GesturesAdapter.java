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
import android.support.test.uiautomator.UiDevice;
import android.view.ViewConfiguration;

import io.appium.uiautomator2.utils.ReflectionUtils;

public class GesturesAdapter {

    private static final String METHOD_GET_INSTANCE = "getInstance";
    private static final String FIELD_VIEW_CONFIG = "mViewConfig";
    private static final String CLASS_GESTURES = "android.support.test.uiautomator.Gestures";
    @NonNull
    private final ReflectionUtils reflectionUtils;

    public GesturesAdapter(@NonNull final UiDevice uiDevice,
                           @NonNull final ReflectionUtils reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetClass(CLASS_GESTURES);
        Object instance = reflectionUtils.invoke(reflectionUtils.method(METHOD_GET_INSTANCE,
                UiDevice.class), uiDevice);
        reflectionUtils.setTargetObject(instance);
    }

    public ViewConfiguration getViewConfig() {
        return reflectionUtils.getField(FIELD_VIEW_CONFIG);
    }
}
