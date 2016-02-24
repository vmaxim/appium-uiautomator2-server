package io.appium.uiautomator2.handler;

import org.json.JSONException;

import io.appium.uiautomator2.http.IHttpRequest;

public class Status extends RequestHandler {

    public Status(String mappedUri){
        super(mappedUri);
    }
    @Override
    public String handle(IHttpRequest request) throws JSONException {
        return "Status invoked";
    }
}
