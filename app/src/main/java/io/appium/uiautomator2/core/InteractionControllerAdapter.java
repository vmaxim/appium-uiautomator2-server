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
import android.view.InputEvent;
import android.view.MotionEvent.PointerCoords;

import javax.inject.Inject;
import javax.inject.Named;

import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Wrapper for {@link android.support.test.uiautomator.InteractionController}
 */
public class InteractionControllerAdapter {

    private static final String METHOD_PERFORM_MULTI_POINTER_GESTURE = "performMultiPointerGesture";
    private static final String METHOD_SEND_KEY = "sendKey";
    private static final String METHOD_INJECT_EVENT_SYNC = "injectEventSync";
    private static final String METHOD_TOUCH_DOWN = "touchDown";
    private static final String METHOD_TOUCH_UP = "touchUp";
    private static final String METHOD_TOUCH_MOVE = "touchMove";

    @NonNull
    private final ScrollEventRegister scrollEventRegister;
    @NonNull
    private final ReflectionUtils reflectionUtils;

    @Inject
    public InteractionControllerAdapter(
            @NonNull @Named("InteractionController") final Object interactionController,
            @NonNull final ScrollEventRegister scrollEventRegister,
            @NonNull final ReflectionUtils reflectionUtils) {
        this.scrollEventRegister = scrollEventRegister;
        this.reflectionUtils = reflectionUtils;
        reflectionUtils.setTargetObject(interactionController);
    }

    public boolean sendKey(final int keyCode, final int metaState) throws UiAutomator2Exception {
        return reflectionUtils.invoke(reflectionUtils.method(METHOD_SEND_KEY, int.class,
                int.class), keyCode, metaState);
    }

    @Nullable
    public Boolean injectEventSync(@NonNull final InputEvent event) throws UiAutomator2Exception, NoSuchDriverException {
        return scrollEventRegister.runAndRegisterScrollEvents(new ReturningRunnable<Boolean>() {
            @Override
            public void run() {
                final Boolean result = reflectionUtils.invoke(reflectionUtils.method(
                        METHOD_INJECT_EVENT_SYNC, InputEvent.class), event);
                setResult(result);
            }
        });
    }

    @Nullable
    public Boolean touchDown(final int x, final int y) throws UiAutomator2Exception, NoSuchDriverException {
        return scrollEventRegister.runAndRegisterScrollEvents(new ReturningRunnable<Boolean>() {
            @Override
            public void run() {
                final Boolean result = reflectionUtils.invoke(reflectionUtils.method(
                        METHOD_TOUCH_DOWN, int.class, int.class), x, y);
                setResult(result);
            }
        });
    }

    @Nullable
    public Boolean touchUp(final int x, final int y) throws UiAutomator2Exception, NoSuchDriverException {
        return scrollEventRegister.runAndRegisterScrollEvents(new ReturningRunnable<Boolean>() {
            @Override
            public void run() {
                final Boolean result = reflectionUtils.invoke(reflectionUtils.method(
                        METHOD_TOUCH_UP,
                        int.class, int.class), x, y);
                setResult(result);
            }
        });
    }

    @Nullable
    public Boolean touchMove(final int x, final int y) throws UiAutomator2Exception, NoSuchDriverException {
        return scrollEventRegister.runAndRegisterScrollEvents(new ReturningRunnable<Boolean>() {
            @Override
            public void run() {
                final Boolean result = reflectionUtils.invoke(reflectionUtils.method(
                        METHOD_TOUCH_MOVE, int.class, int.class), x, y);
                setResult(result);
            }
        });
    }

    @Nullable
    public Boolean performMultiPointerGesture(final PointerCoords[][] pcs) throws
            UiAutomator2Exception, NoSuchDriverException {
        return scrollEventRegister.runAndRegisterScrollEvents(new ReturningRunnable<Boolean>() {
            @Override
            public void run() {
                final Boolean result = reflectionUtils.invoke(reflectionUtils.method(
                        METHOD_PERFORM_MULTI_POINTER_GESTURE, PointerCoords[][].class),
                        (Object) pcs);
                setResult(result);
            }
        });
    }
}
