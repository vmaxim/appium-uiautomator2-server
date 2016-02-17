package io.appium.uiautomator2.server;


import java.util.HashMap;
import java.util.Map;

import io.appium.uiautomator2.handler.Click;
import io.appium.uiautomator2.handler.FindElement;
import io.appium.uiautomator2.handler.RequestHandler;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.http.IHttpResponse;
import io.appium.uiautomator2.http.IHttpServlet;

public class AppiumServlet implements IHttpServlet {

    public static final String SESSION_ID_KEY = "SESSION_ID_KEY";
    public static final String ELEMENT_ID_KEY = "ELEMENT_ID_KEY";
    public static final String COMMAND_NAME_KEY = "COMMAND_KEY";
    public static final String NAME_ID_KEY = "NAME_ID_KEY";
    public static final String DRIVER_KEY = "DRIVER_KEY";
    public static final int INTERNAL_SERVER_ERROR = 500;

    protected static Map<String, RequestHandler> getHandler = new HashMap<String, RequestHandler>();
    protected static Map<String, RequestHandler> postHandler = new HashMap<String, RequestHandler>();

    public AppiumServlet() {
        init();
    }

    private void init() {
        register(postHandler, new FindElement("/wd/hub/session"));
        register(getHandler, new Click("/wd/hub/sessions"));
    }

    protected void register(Map<String, RequestHandler> registerOn, RequestHandler handler) {
        registerOn.put(handler.getMappedUri(), handler);
    }

    @Override
    public void handleHttpRequest(IHttpRequest IHttpRequest, IHttpResponse httpResponse) throws Exception {

    }
}
