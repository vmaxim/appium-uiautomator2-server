package io.appium.uiautomator2.handler;

import org.json.JSONException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.ServerInstrumentation;

/**
 * Created by sravanm on 13-03-2016.
 */
public class DeleteSession extends io.appium.uiautomator2.handler.RequestHandler {

    public DeleteSession(String mappedUri) {
        super(mappedUri);
    }
    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        try {
            ServerInstrumentation.getInstance(null, 8080).stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppiumResponse(getSessionId(request), "Session deleted....");
    }

}
