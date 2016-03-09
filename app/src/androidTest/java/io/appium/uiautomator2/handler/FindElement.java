package io.appium.uiautomator2.handler;


import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.UUID;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.util.Device;

public class FindElement extends RequestHandler {
    private static UiDevice uiDevice = Device.getUiDevice();

    public FindElement(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        KnownElements ke = new KnownElements();
        JSONObject payload = getPayload(request);
        String selector = payload.getString("value");

        UiObject2 element = uiDevice.findObject(By.text(selector));
        Random random = new Random();
        String id = new UUID(random.nextLong(), random.nextLong()).toString();
        AndroidElement androidElement = new AndroidElement(id, element);
        ke.add(androidElement);

        JSONObject result = new JSONObject();
        result.put("ELEMENT", id);

        return new AppiumResponse(getSessionId(request), result);
    }
}
