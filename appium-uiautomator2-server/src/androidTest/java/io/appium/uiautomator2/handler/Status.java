package io.appium.uiautomator2.handler;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.IHttpRequest;

public class Status extends RequestHandler {

    public Status(String mappedUri){
        super(mappedUri);
    }
    @Override
    public String handle(IHttpRequest request) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", 0);
        jsonObject.put("statusCode", 200);
        jsonObject.put("value", "Status invoked");
        return jsonObject.toString();
    }
}
