package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class Location extends SafeRequestHandler {
    public Location(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException, StaleElementReferenceException {
        final JSONObject response = new JSONObject();
        String id = getElementId(request);
        AndroidElement element = getCachedElements().getElement(id);
        try {
            final Rect bounds = element.getBounds();
            response.put("x", bounds.left);
            response.put("y", bounds.top);
            Logger.info("Element found at location " + "(" + bounds.left + "," + bounds.top + ")");
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element Location not found", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, response);

    }
}


