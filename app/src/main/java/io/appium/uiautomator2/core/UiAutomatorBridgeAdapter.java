/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.appium.uiautomator2.core;

import android.app.UiAutomation;
import android.support.test.uiautomator.UiAutomatorBridge;
import android.view.Display;
import android.view.InputEvent;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class UiAutomatorBridgeAdapter {

    private static final Class CLASS_UI_AUTOMATOR_BRIDGE = UiAutomatorBridge.class;

    private static final String FIELD_QUERY_CONTROLLER = "mQueryController";
    private static final String FIELD_INTERACTION_CONTROLLER = "mInteractionController";
    private static final String FIELD_UI_AUTOMATOR = "mUiAutomation";

    private static final String METHOD_GET_DEFAULT_DISPLAY = "getDefaultDisplay";
    private static final String METHOD_INJECT_INPUT_EVENT = "injectInputEvent";

    private final UiAutomatorBridge uiAutomatorBridge;
    private final ReflectionUtils reflectionUtils;

    public UiAutomatorBridgeAdapter(UiAutomatorBridge uiAutomatorBridge, ReflectionUtils
            reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
        this.uiAutomatorBridge = uiAutomatorBridge;
        reflectionUtils.setTarget(uiAutomatorBridge);
    }

    public Object getInteractionController() throws UiAutomator2Exception {
        return reflectionUtils.getField(FIELD_INTERACTION_CONTROLLER);
    }

    public Object getQueryController() throws UiAutomator2Exception {
        return reflectionUtils.getField(FIELD_QUERY_CONTROLLER);
    }

    public UiAutomation getUiAutomation() {
        return (UiAutomation) reflectionUtils.getField(FIELD_UI_AUTOMATOR);
    }

    public Display getDefaultDisplay() throws UiAutomator2Exception {
        return (Display) reflectionUtils.invoke(reflectionUtils.method(METHOD_GET_DEFAULT_DISPLAY)
        );
    }

    public boolean injectInputEvent(InputEvent event, boolean sync) throws UiAutomator2Exception {
        return (Boolean) reflectionUtils.invoke(reflectionUtils.method(METHOD_INJECT_INPUT_EVENT,
                InputEvent.class, boolean.class), event, sync);
    }

}
