package io.appium.uiautomator2.handler;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AppiumUiAutomatorDriver;

public class NewSession extends RequestHandler{

    private AppiumUiAutomatorDriver appiumUiAutomatorDriver;

    public NewSession(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public String handle(IHttpRequest request) throws JSONException {

        JSONObject payload = getPayload(request);
        JSONObject desiredCapabilities = payload.getJSONObject("desiredCapabilities");

        appiumUiAutomatorDriver = new AppiumUiAutomatorDriver();
        String sessionID = null;
        try {
            sessionID = appiumUiAutomatorDriver.initializeSession(desiredCapabilities);
        } catch (Exception e) {
            Log.e("Error creating session ", e.toString());
        }
        return sessionID;
    }
}
