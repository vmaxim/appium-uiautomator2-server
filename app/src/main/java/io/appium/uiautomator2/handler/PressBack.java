package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;

public class PressBack extends SafeRequestHandler {

    public PressBack(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        coreFacade.back();
        // Press back returns false even when back was successfully pressed.
        // Always return true.
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, true);
    }
}
