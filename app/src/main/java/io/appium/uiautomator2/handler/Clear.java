package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class Clear extends SafeRequestHandler {
    public Clear(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException {
        try {
            Logger.info("Clear element command");
            JSONObject payload = getPayload(request);
            AndroidElement element;
            if (payload.has("elementId")) {
                String id = payload.getString("elementId");
                element = getCachedElements().getElementFromCache(id);
                if (element == null) {
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
                }
            } else {
                //perform action on focused element
                try {
                    BySelector bySelector = By.focused(true);
                    element = App.core.getUiDeviceAdapter().findObject(bySelector);
                    getCachedElements().add(element);
                } catch (ElementNotFoundException e) {
                    Logger.debug("Error retrieving focused element: " + e);
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
                }  catch ( UiAutomator2Exception  e) {
                    Logger.debug("Error in finding focused element: " + e);
                    return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, "Unable to find a focused element." + e.getStackTrace());
                }
            }
            element.clear();
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, "Element Cleared");
    }
}
