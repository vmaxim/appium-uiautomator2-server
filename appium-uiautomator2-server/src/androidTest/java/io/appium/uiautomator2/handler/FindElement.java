package io.appium.uiautomator2.handler;


import android.util.Log;

import org.json.JSONException;

import io.appium.uiautomator2.http.IHttpRequest;

public class FindElement extends RequestHandler{

    public FindElement(String mappedUri){
        super(mappedUri);
    }

    @Override
    public String handle(IHttpRequest request) throws JSONException {
        String id = getElementId(request);
        Log.i("Find invoked", "*********** Find Handler ************");
        return "Find element";
    }
}
