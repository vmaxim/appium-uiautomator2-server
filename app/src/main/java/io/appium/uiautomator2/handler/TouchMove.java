package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;

public class TouchMove extends TouchEvent {

    public TouchMove(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public boolean executeTouchEvent() throws UiAutomator2Exception {
        printEventDebugLine("TouchMove");
        try {
            return coreFacade.touchMove(clickX, clickY);
        } catch (Exception e) {
            Logger.error("Problem invoking touchMove: " + e);
            return false;
        }
    }
}
