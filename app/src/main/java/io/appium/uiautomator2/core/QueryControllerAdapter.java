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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;

import javax.inject.Inject;
import javax.inject.Named;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Wrapper for {@link android.support.test.uiautomator.QueryController}
 */
public class QueryControllerAdapter {

    private static final String METHOD_GET_ROOT_NODE = "getRootNode";
    @NonNull
    private final ReflectionUtils reflectionUtils;

    @Inject
    public QueryControllerAdapter(@NonNull @Named("QueryController") final Object queryController,
                                  @NonNull final ReflectionUtils reflectionUtils) {
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetObject(queryController);
    }

    @Nullable
    public AccessibilityNodeInfo getRootNode() throws UiAutomator2Exception {
        return reflectionUtils.invoke(reflectionUtils.method(
                METHOD_GET_ROOT_NODE));
    }
}
