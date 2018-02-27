package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;

public class TouchDown extends TouchEvent {

    public TouchDown(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public boolean executeTouchEvent() throws UiAutomator2Exception {
        printEventDebugLine("TouchDown");
        try {
            return coreFacade.touchDown(clickX, clickY);
        } catch (Exception e) {
            Logger.error("Problem invoking touchDown: " + e);
            return false;
        }
    }
}
