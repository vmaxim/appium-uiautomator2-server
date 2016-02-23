package io.appium.uiautomator2.model;


import org.json.JSONObject;

import java.util.Random;
import java.util.UUID;

import io.appium.uiautomator2.util.Log;

public class AppiumUiAutomatorDriver {

    private Session session = null;

    public String initializeSession(JSONObject desiredCapabilities) {
        if (this.session != null) {
//            session.getKnownElements().clear();
            return session.getSessionId();
        }
        Random random = new Random();
        this.session =
                new Session(desiredCapabilities, new UUID(random.nextLong(), random.nextLong()).toString());

        Log.i(":::::::::::::::::session Id::::::::::::::::" + session.getSessionId());

        return session.getSessionId();
    }
}
