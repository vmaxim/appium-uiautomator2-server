package io.appium.uiautomator2.core;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {CoreModule.class})
public interface CoreComponent {

    UiAutomatorBridgeAdapter getUiAutomatorBridge();

    InteractionControllerAdapter getInteractionControllerAdapter();

    QueryControllerAdapter getQueryControllerAdapter();

    EventRegister getEventRegister();

    AccessibilityNodeInfoDumper getAccessibilityNodeInfoDumper();

    ByMatcherAdapter getByMatcherAdapter();

    UiDeviceAdapter getUiDeviceAdapter();
}
