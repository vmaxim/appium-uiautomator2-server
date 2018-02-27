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
import android.support.annotation.NonNull;
import android.support.test.uiautomator.UiAutomatorBridge;
import android.view.Display;
import android.view.InputEvent;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.ReflectionUtils;

public class UiAutomatorBridgeAdapter {

    private static final String FIELD_QUERY_CONTROLLER = "mQueryController";
    private static final String FIELD_INTERACTION_CONTROLLER = "mInteractionController";
    private static final String FIELD_UI_AUTOMATOR = "mUiAutomation";

    private static final String METHOD_GET_DEFAULT_DISPLAY = "getDefaultDisplay";
    private static final String METHOD_INJECT_INPUT_EVENT = "injectInputEvent";

    private final Object queryController;
    private final Object interactionController;
    private final UiAutomation uiAutomation;
    private final ReflectionUtils reflectionUtils;
    private final Display defaultDisplay;

    public UiAutomatorBridgeAdapter(@NonNull final UiAutomatorBridge uiAutomatorBridge,
                                    @NonNull final ReflectionUtils reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetObject(uiAutomatorBridge);
        queryController = reflectionUtils.getField(FIELD_QUERY_CONTROLLER);
        interactionController = reflectionUtils.getField(FIELD_INTERACTION_CONTROLLER);
        uiAutomation = reflectionUtils.getField(FIELD_UI_AUTOMATOR);
        defaultDisplay = reflectionUtils.invoke(reflectionUtils.method(METHOD_GET_DEFAULT_DISPLAY));
    }

    public Object getInteractionController() throws UiAutomator2Exception {
        return interactionController;
    }

    public Object getQueryController() throws UiAutomator2Exception {
        return queryController;
    }

    Display getDefaultDisplay() throws UiAutomator2Exception {
        return defaultDisplay;
    }

    public boolean injectInputEvent(@NonNull final InputEvent event, boolean sync) throws
            UiAutomator2Exception {
        return reflectionUtils.invoke(reflectionUtils.method(METHOD_INJECT_INPUT_EVENT,
                InputEvent.class, boolean.class), event, sync);
    }

    public UiAutomation getUiAutomation() {
        return uiAutomation;
    }

}
