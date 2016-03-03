package io.appium.uiautomator2.handler;

import android.os.RemoteException;

import org.json.JSONException;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.util.Device;

public class RotateScreen extends RequestHandler {

    public RotateScreen(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        try {
            Device.getUiDevice().setOrientationRight();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new AppiumResponse(getSessionId(request), "Orientation has been changed");
    }
}
