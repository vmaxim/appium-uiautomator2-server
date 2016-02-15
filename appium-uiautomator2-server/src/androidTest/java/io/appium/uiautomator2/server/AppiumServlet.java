package io.appium.uiautomator2.server;


import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.http.IHttpResponse;
import io.appium.uiautomator2.http.IHttpServlet;

public class AppiumServlet implements IHttpServlet {

    static {
//        register(postHandler, new NewSession("/wd/hub/session"));
//        register(getHandler, new ListSessions("/wd/hub/sessions"));
    }

    @Override
    public void handleHttpRequest(IHttpRequest IHttpRequest, IHttpResponse httpResponse) throws Exception {

    }
}
