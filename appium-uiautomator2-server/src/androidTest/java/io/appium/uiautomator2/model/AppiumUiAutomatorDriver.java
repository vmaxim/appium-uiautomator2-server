package io.appium.uiautomator2.model;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.UUID;


public class AppiumUiAutomatorDriver {

    private Session session = null;

    public String initializeSession() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        if (this.session != null) {
            return session.getSessionId();
        }
        Random random = new Random();
        this.session = new Session(new UUID(random.nextLong(), random.nextLong()).toString());
        Log.i("::::::::::session Id:::", session.getSessionId());
        jsonObject.put("sessionId", session.getSessionId());
        return session.getSessionId();
    }
}
