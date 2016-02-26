package io.appium.uiautomator2.http;


import org.json.JSONException;
import org.json.JSONObject;

public class AppiumResponse {
    private int status;
    private Object value;

    private AppiumResponse(int status, Object value) {
        this.status = status;
        this.value = value;
    }

    public AppiumResponse(Object value) {
        this(0, value);
    }

    public String render() {
        JSONObject o = new JSONObject();
        try {
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
