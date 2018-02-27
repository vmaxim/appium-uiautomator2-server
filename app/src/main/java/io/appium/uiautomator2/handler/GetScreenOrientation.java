package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.enums.OrientationEnum;
import io.appium.uiautomator2.server.WDStatus;

public class GetScreenOrientation extends SafeRequestHandler {

    public GetScreenOrientation(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        OrientationEnum orientation;
        int rotation = coreFacade.getDisplayRotation();
        if (rotation == 1 || rotation == 3) {
            orientation = OrientationEnum.LANDSCAPE;
        } else {
            orientation = OrientationEnum.PORTRAIT;
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, orientation);
    }
}
