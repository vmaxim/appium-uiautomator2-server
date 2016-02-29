package io.appium.uiautomator2.handler;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AppiumUiAutomatorDriver;

public class NewSession extends RequestHandler{

    private AppiumUiAutomatorDriver appiumUiAutomatorDriver;

    public NewSession(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {

        appiumUiAutomatorDriver = new AppiumUiAutomatorDriver();
        String sessionID = null;
        try {
            sessionID = appiumUiAutomatorDriver.initializeSession();
        } catch (Exception e) {
            Log.e("Error creating session ", e.toString());
        }
        return new AppiumResponse(sessionID,"");
    }
}
