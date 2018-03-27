package io.appium.uiautomator2.handler.request;

import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.NoSuchContextException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.SessionRemovedException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.core.CoreFacade;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.AppiumServlet;
import io.appium.uiautomator2.utils.Logger;

public abstract class BaseRequestHandler {

    private final String mappedUri;
    protected final CoreFacade coreFacade;
    public BaseRequestHandler(String mappedUri) {
        this.coreFacade = App.core.getCoreFacade();
        this.mappedUri = mappedUri;
    }

    public String getMappedUri() {
        return mappedUri;
    }

    public String getElementId(IHttpRequest request) {
        return (String) request.data().get(AppiumServlet.ELEMENT_ID_KEY);
    }

    public String getNameAttribute(IHttpRequest request) {
        return (String) request.data().get(AppiumServlet.NAME_ID_KEY);
    }

    public JSONObject getPayload(IHttpRequest request) throws JSONException {
        String json = request.body();
        Logger.debug("payload: " + json);
        if (json != null && !json.isEmpty()) {
            return new JSONObject(json);
        }
        return new JSONObject();
    }

    public Map<String, Object> getPayload(IHttpRequest request, String jsonKey) throws JSONException {
        JSONObject payload = getPayload(request);
        if (jsonKey != null) {
            payload = payload.getJSONObject(jsonKey);
        }

        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = payload.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = payload.get(key);
            map.put(key, value);
        }
        return map;
    }

    public String getSessionId(IHttpRequest request) {
        return (String) request.data().get(AppiumServlet.SESSION_ID_KEY);
    }

    public abstract AppiumResponse handle(IHttpRequest request);

    public AppiumResponse safeHandle(IHttpRequest request) throws SessionRemovedException, NoSuchDriverException, NoSuchContextException, ElementNotFoundException, StaleElementReferenceException, UiObjectNotFoundException, InvalidCoordinatesException, XPathExpressionException {
        return handle(request);
    }
}
