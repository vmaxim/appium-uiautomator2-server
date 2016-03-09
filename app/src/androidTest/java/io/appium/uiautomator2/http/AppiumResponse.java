package io.appium.uiautomator2.http;


import org.json.JSONException;
import org.json.JSONObject;

public class AppiumResponse {
    private int status;
    private Object value;
    private String sessionId;

    private AppiumResponse(String sessionId,int status, Object value) {
        this.sessionId = sessionId;
        this.status = status;
        this.value = value;
    }

    public AppiumResponse(String sessionId, Object value) {
        this(sessionId,0, value);
    }

    public String render() {
        JSONObject o = new JSONObject();
        try {
            if (sessionId != null) {
                o.put("sessionId", sessionId);
            }
            o.put("status", status);
            if (value != null) {
                o.put("value", value);
            }
        } catch (JSONException e) {
            System.out.println("Cannot render response: " + e.getMessage());
        }
        return o.toString();
    }
}
