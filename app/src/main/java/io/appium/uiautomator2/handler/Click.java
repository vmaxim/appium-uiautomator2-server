package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;

public class Click extends io.appium.uiautomator2.handler.RequestHandler {

    public Click(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        JSONObject payload = getPayload(request);
        String id = payload.getString("id");
        AndroidElement element = KnownElements.getElementFromCache(id);
        try {
            element.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return new AppiumResponse(getSessionId(request), "Click element");
    }
}
