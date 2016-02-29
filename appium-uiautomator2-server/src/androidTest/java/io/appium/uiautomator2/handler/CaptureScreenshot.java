package io.appium.uiautomator2.handler;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;

import java.io.File;

import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.util.Device;

public class CaptureScreenshot extends RequestHandler {

    public CaptureScreenshot(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse handle(IHttpRequest request) throws JSONException {
        final File screenshot = new File(Environment.getExternalStorageDirectory() + File.separator + "screenshot.png");

        Log.i("Path", Environment.getExternalStorageDirectory().toString());
        try {
            screenshot.getParentFile().mkdirs();
        } catch (final Exception e) {
        }

        if (screenshot.exists()) {
            screenshot.delete();
        }
        Device.getUiDevice().takeScreenshot(screenshot);
        return new AppiumResponse(getSessionId(request),"Screnshot taken");
    }
}
