package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.KnownElements;
import io.appium.uiautomator2.util.Device;

public class Click extends RequestHandler {
    private static UiDevice uiDevice = Device.getUiDevice();

    public Click(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        Log.i("Click Click",request.toString());
        JSONObject payload = getPayload(request);
        Log.i("Click Click", payload.toString());

        String id = getElementId(request);

        Log.i("Prakash", "************Received the id************");

        AndroidElement element = KnownElements.getElementFromCache(id);
        Log.i("Prakash", "************Found the element************");
        try {
            element.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

//        UiObject2 uiObject2 = new UiObject2(uiDevice, );
        Log.i("Click invoked", "*********** Click Handler ************");
        return new AppiumResponse(getSessionId(request),"Click element");
    }
}
