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
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.appium.uiautomator2.model.AccessibilityNodeInfoHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

@Module
public class CoreModule {

    @Provides
    @NonNull
    @Singleton
    public AccessibilityNodeInfoHelper provideAccessibilityNodeInfoHelper() {
        return new AccessibilityNodeInfoHelper();
    }

    @Provides
    @NonNull
    @Singleton
    public UiDevice provideUiDevice() {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Provides
    @NonNull
    public ReflectionUtils provideReflectionUtils() {
        return new ReflectionUtils();
    }

    @Provides
    @NonNull
    @Singleton
    public ByMatcherAdapter provideByMatcherAdapter(UiDevice uiDevice, ReflectionUtils
            reflectionUtils) {
        return new ByMatcherAdapter(uiDevice, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public UiDeviceAdapter provideUiDeviceAdapter(UiDevice uiDevice, ByMatcherAdapter
            byMatcherAdapter,
                                                  ReflectionUtils reflectionUtils) {
        return new UiDeviceAdapter(uiDevice, byMatcherAdapter, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public UiAutomatorBridgeAdapter provideUiAutomatorBridge(UiDeviceAdapter uiDeviceAdapter,
                                                             ReflectionUtils reflectionUtils) {
        return new UiAutomatorBridgeAdapter(uiDeviceAdapter.getUiAutomatorBridge(),
                reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public InteractionControllerAdapter provideInteractionControllerAdapter
            (UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter, EventRegister eventRegister,
             ReflectionUtils reflectionUtils) {
        return new InteractionControllerAdapter(uiAutomatorBridgeAdapter.getInteractionController
                (), eventRegister, reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public QueryControllerAdapter provideQueryControllerAdapter(UiAutomatorBridgeAdapter
                                                                        uiAutomatorBridgeAdapter,
                                                                ReflectionUtils reflectionUtils) {
        return new QueryControllerAdapter(uiAutomatorBridgeAdapter.getQueryController(),
                reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public EventRegister provideEventRegister(UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter) {
        return new EventRegister(uiAutomatorBridgeAdapter.getUiAutomation());
    }

    @Provides
    @NonNull
    @Singleton
    public AccessibilityInteractionClientAdapter getAccessibilityInteractionClientAdapter
            (ReflectionUtils reflectionUtils) {
        return new AccessibilityInteractionClientAdapter(reflectionUtils);
    }
}
