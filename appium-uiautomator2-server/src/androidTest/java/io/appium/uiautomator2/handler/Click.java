package io.appium.uiautomator2.handler;


import android.util.Log;

import org.json.JSONException;

import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.http.IHttpResponse;

public class Click extends RequestHandler{

    public Click(String mappedUri){
        super(mappedUri);
    }

    @Override
    public String handle(IHttpRequest request) throws JSONException {
        String id = getElementId(request);
        Log.i("Click invoked", "*********** Click Handler ************");
        return "Click element";
    }
}
