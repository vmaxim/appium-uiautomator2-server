package io.appium.uiautomator2.handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.dto.AccessibilityScrollData;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.App.session;

public class GetSessionDetails extends SafeRequestHandler {


    public GetSessionDetails(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        try {
            JSONObject result = new JSONObject();
            AccessibilityScrollData scrollData = session.getSession().getLastScrollData();
            Map<String, Integer> scrollDataMap;
            if (scrollData == null) {
                scrollDataMap = null;
            } else {
                scrollDataMap = scrollData.getAsMap();
            }
            JSONObject lastScrollData = new JSONObject(scrollDataMap);
            result.put("lastScrollData", lastScrollData);
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, result);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            Logger.error(WDStatus.JSON_DECODER_ERROR, e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }
}
