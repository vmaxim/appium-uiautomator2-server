package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;

import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.SessionRemovedException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class GetText extends SafeRequestHandler {

    public GetText(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException {
        Logger.info("Get Text of element command");
        String id = getElementId(request);
        String text;
        AndroidElement element = getCachedElements().getElementFromCache(id);
        if (element == null) {
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
        try {
            text = element.getText();
            Logger.info("Get Text :" + text);
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, text);
    }

}
