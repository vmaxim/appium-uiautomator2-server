package io.appium.uiautomator2.core;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.appium.uiautomator2.utils.ReflectionUtils;

/**
 * Created by max on 15.02.2018.
 */

@Module
public class CoreModule {

    @Provides
    @NonNull
    public ReflectionUtils provideReflectionUtils() {
        return new ReflectionUtils();
    }

    @Provides
    @NonNull
    @Singleton
    public ByMatcherAdapter provideByMatcherAdapter(ReflectionUtils reflectionUtils) {
        return new ByMatcherAdapter(reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public UiDeviceAdapter provideUiDeviceAdapter(ByMatcherAdapter byMatcherAdapter,
                                                  ReflectionUtils reflectionUtils) {
        return new UiDeviceAdapter(byMatcherAdapter, reflectionUtils);
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
        return new QueryControllerAdapter(uiAutomatorBridgeAdapter.getInteractionController(),
                reflectionUtils);
    }

    @Provides
    @NonNull
    @Singleton
    public EventRegister provideEventRegister(UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter) {
        return new EventRegister(uiAutomatorBridgeAdapter.getUiAutomation());
    }
}
