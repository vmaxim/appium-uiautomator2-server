package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;

public class TouchUp extends TouchEvent {

    public TouchUp(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public boolean executeTouchEvent() throws UiAutomator2Exception {
        printEventDebugLine("TouchUp");
        try {
            return coreFacade.touchUp(clickX, clickY);
        } catch (Exception e) {
            Logger.error("Problem invoking touchUp: " + e);
            return false;
        }
    }
}
