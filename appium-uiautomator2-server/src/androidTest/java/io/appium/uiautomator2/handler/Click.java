package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.util.Device;

public class Click extends RequestHandler {
    private static UiDevice uiDevice = Device.getUiDevice();

    public Click(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public String handle(IHttpRequest request) throws JSONException {
        Log.i("Click Click",request.toString());
        JSONObject payload = getPayload(request);
        Log.i("Click Click",payload.toString());

//        String id = getElementId(request);
//        UiObject2 uiObject2 = uiDevice.findObject(By.text(id));
//        uiObject2.click();
//        UiObject2 uiObject2 = new UiObject2(uiDevice, );
        Log.i("Click invoked", "*********** Click Handler ************");
        return "Click element";
    }
}
