package io.appium.uiautomator2.handler;

import org.json.JSONException;

import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.http.IHttpResponse;
import io.appium.uiautomator2.server.AppiumServlet;
import io.netty.handler.codec.http.HttpRequest;

public abstract class RequestHandler {

    protected String mappedUri = null;

    public RequestHandler(String mappedUri) {
        this.mappedUri = mappedUri;
    }

    public String getMappedUri() {
        return mappedUri;
    }

    public String getElementId(IHttpRequest request) {
        if (request.data().containsKey(AppiumServlet.ELEMENT_ID_KEY)) {
            return (String) request.data().get(AppiumServlet.ELEMENT_ID_KEY);
        }
        return null;
    }

    public abstract String handle(IHttpRequest request) throws JSONException;

    public final String safeHandle(IHttpRequest request) throws JSONException {
        return handle(request);
    }
}
