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
package io.appium.uiautomator2.core.di;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.appium.uiautomator2.core.AccessibilityInteractionClientAdapter;
import io.appium.uiautomator2.core.ByMatcherAdapter;
import io.appium.uiautomator2.core.EventRegister;
import io.appium.uiautomator2.core.GesturesAdapter;
import io.appium.uiautomator2.core.InteractionControllerAdapter;
import io.appium.uiautomator2.core.QueryControllerAdapter;
import io.appium.uiautomator2.core.UiAutomatorBridgeAdapter;
import io.appium.uiautomator2.core.UiDeviceAdapter;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

@Module
public class CoreModule {

    @Provides
    @NonNull
    ReflectionUtils provideReflectionUtils() {
        return new ReflectionUtils();
    }

    @Provides
    @NonNull
    @Singleton
    AccessibilityNodeInfoHelper provideAccessibilityNodeInfoHelper() {
        return new AccessibilityNodeInfoHelper();
    }

    @Provides
    @NonNull
    @Singleton
    UiDevice provideUiDevice() {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Provides
    @NonNull
    @Singleton
    AccessibilityInteractionClientAdapter getAccessibilityInteractionClientAdapter(
            @NonNull final ReflectionUtils reflectionUtils) {
        return new AccessibilityInteractionClientAdapter(reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    GesturesAdapter provideGesturesAdapter(
            @NonNull final UiDevice uiDevice,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new GesturesAdapter(uiDevice, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    ByMatcherAdapter provideByMatcherAdapter(
            @NonNull final UiDevice uiDevice,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new ByMatcherAdapter(uiDevice, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    UiDeviceAdapter provideUiDeviceAdapter(
            @NonNull final UiDevice uiDevice,
            @NonNull final ByMatcherAdapter byMatcherAdapter,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new UiDeviceAdapter(uiDevice, byMatcherAdapter, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    UiAutomatorBridgeAdapter provideUiAutomatorBridgeAdapter(
            @NonNull final UiDeviceAdapter uiDeviceAdapter,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new UiAutomatorBridgeAdapter(uiDeviceAdapter.getUiAutomatorBridge(),
                reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    EventRegister provideEventRegister(
            @NonNull final UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter) {
        return new EventRegister(uiAutomatorBridgeAdapter.getUiAutomation());
    }

    @Provides
    @NonNull
    @Singleton
    InteractionControllerAdapter provideInteractionControllerAdapter(
            @NonNull final UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter,
            @NonNull final EventRegister eventRegister,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new InteractionControllerAdapter(uiAutomatorBridgeAdapter.getInteractionController
                (), eventRegister, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    QueryControllerAdapter provideQueryControllerAdapter(
            @NonNull final UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new QueryControllerAdapter(uiAutomatorBridgeAdapter.getQueryController(),
                reflectionUtils);
    }

}
