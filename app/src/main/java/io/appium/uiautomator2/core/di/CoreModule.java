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

import android.app.UiAutomation;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.appium.uiautomator2.App;
import io.appium.uiautomator2.core.AccessibilityInteractionClientAdapter;
import io.appium.uiautomator2.core.ByMatcherAdapter;
import io.appium.uiautomator2.core.CoreFacade;
import io.appium.uiautomator2.core.ElementFinder;
import io.appium.uiautomator2.core.EventRegister;
import io.appium.uiautomator2.core.GesturesAdapter;
import io.appium.uiautomator2.core.InteractionControllerAdapter;
import io.appium.uiautomator2.core.QueryControllerAdapter;
import io.appium.uiautomator2.core.UiAutomatorBridgeAdapter;
import io.appium.uiautomator2.core.UiDeviceAdapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapterFactory;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Level 0:
 * provideApiLevelActual
 * provideAllowMultiWindow
 * provideReflectionUtils
 * provideAccessibilityNodeInfoHelper
 * provideUiDevice
 *
 * Level 1:
 * provideReflectionUtils -> getAccessibilityInteractionClientAdapter
 * provideUiDevice, provideReflectionUtils -> provideGesturesAdapter
 * provideUiDevice, provideReflectionUtils -> provideUiDeviceAdapter
 *
 * Level 2:
 * provideUiDeviceAdapter, provideReflectionUtils -> provideUiAutomatorBridgeAdapter
 *
 * Level 3:
 * provideUiAutomatorBridgeAdapter -> provideUiAutomation
 *
 * Level 4:
 * provideUiAutomation -> provideEventRegister
 * provideUiAutomatorBridgeAdapter, provideReflectionUtils -> provideQueryControllerAdapter
 *
 * Level 5:
 * provideUiAutomatorBridgeAdapter, provideEventRegister, provideReflectionUtils -> provideInteractionControllerAdapter
 * provideUiDevice, provideUiAutomation, provideReflectionUtils, provideApiLevelActual, provideAllowMultiWindow -> provideByMatcherAdapter
 *
 * Level 6:
 * provideUiDeviceAdapter, provideByMatcherAdapter, provideUiObjectAdapterFactory -> provideElementFinder
 */
@Module
public class CoreModule {

    @Provides
    @Named("apiLevelActual")
    @Singleton
    Integer provideApiLevelActual() {
        return Build.VERSION.SDK_INT + ("REL".equals(Build.VERSION.CODENAME) ? 0 : 1);
    }

    @Provides
    @Named("allowMultiWindow")
    @Singleton
    Boolean provideAllowMultiWindow() {
        return false;
    }

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
            @NonNull final ReflectionUtils reflectionUtils,
            @NonNull final UiAutomation uiAutomation,
            @NonNull @Named("apiLevelActual") final Integer apiLevelActual,
            @NonNull @Named("allowMultiWindow") final Boolean allowMultiWindow) {
        return new ByMatcherAdapter(uiDevice, uiAutomation, reflectionUtils, apiLevelActual, allowMultiWindow);
    }

    @Provides
    @NonNull
    @Singleton
    ElementFinder provideElementFinder(
            @NonNull final UiDeviceAdapter uiDeviceAdapter,
            @NonNull final ByMatcherAdapter byMatcherAdapter) {
        return new ElementFinder(uiDeviceAdapter, byMatcherAdapter, App.model.getUiObjectAdapterFactory());
    }

    @Provides
    @NonNull
    @Singleton
    UiDeviceAdapter provideUiDeviceAdapter(
            @NonNull final UiDevice uiDevice,
            @NonNull final ReflectionUtils reflectionUtils) {
        return new UiDeviceAdapter(uiDevice, reflectionUtils);
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
    UiAutomation provideUiAutomation(UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter) {
        return uiAutomatorBridgeAdapter.getUiAutomation();
    }

    @Provides
    @NonNull
    @Singleton
    EventRegister provideEventRegister(@NonNull final UiAutomation uiAutomation) {
        return new EventRegister(uiAutomation);
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

    @Provides
    @NonNull
    @Singleton
    CoreFacade provideCoreFacade(
            @NonNull final ElementFinder elementFinder,
            @NonNull final UiDeviceAdapter uiDeviceAdapter,
            @NonNull final GesturesAdapter gesturesAdapter,
            @NonNull final InteractionControllerAdapter interactionControllerAdapter,
            @NonNull final UiAutomation uiAutomation,
            @NonNull final AccessibilityInteractionClientAdapter
                    accessibilityInteractionClientAdapter,
            @NonNull final EventRegister eventRegister,
            @NonNull final QueryControllerAdapter queryControllerAdapter) {
        return new CoreFacade(elementFinder, uiDeviceAdapter, gesturesAdapter,
                interactionControllerAdapter, uiAutomation, accessibilityInteractionClientAdapter,
                eventRegister, queryControllerAdapter);
    }

}
