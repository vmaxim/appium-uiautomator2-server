package io.appium.uiautomator2.server;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import io.appium.uiautomator2.handler.CaptureScreenshot;
import io.appium.uiautomator2.handler.Click;
import io.appium.uiautomator2.handler.FindElement;
import io.appium.uiautomator2.handler.NewSession;
import io.appium.uiautomator2.handler.RequestHandler;
import io.appium.uiautomator2.handler.Status;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.http.IHttpResponse;
import io.appium.uiautomator2.http.IHttpServlet;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class AppiumServlet implements IHttpServlet {

    public static final String SESSION_ID_KEY = "SESSION_ID_KEY";
    public static final String ELEMENT_ID_KEY = "id";
    public static final String COMMAND_NAME_KEY = "COMMAND_KEY";
    public static final String NAME_ID_KEY = "NAME_ID_KEY";
    public static final String DRIVER_KEY = "DRIVER_KEY";
    public static final int INTERNAL_SERVER_ERROR = 500;
    private Map<String, String[]> mapperUrlSectionsCache = new HashMap<String, String[]>();

    protected static Map<String, RequestHandler> getHandler = new HashMap<String, RequestHandler>();
    protected static Map<String, RequestHandler> postHandler = new HashMap<String, RequestHandler>();

    public AppiumServlet() {
        init();
    }

    private void init() {
        register(getHandler, new Status("/wd/hub/status"));
        register(postHandler, new NewSession("/wd/hub/session"));
        register(postHandler, new FindElement("/wd/hub/session/:sessionId/element"));
        register(postHandler, new Click("/wd/hub/session/:sessionId/element/:id/click"));
        register(getHandler, new CaptureScreenshot("/wd/hub/session/:sessionId/screenshot"));
    }

    protected void register(Map<String, RequestHandler> registerOn, RequestHandler handler) {
        registerOn.put(handler.getMappedUri(), handler);
    }

    protected RequestHandler findMatcher(IHttpRequest request,
                                         Map<String, RequestHandler> handler) {
        String[] urlToMatchSections = getRequestUrlSections(request.uri());
        for (Map.Entry<String, ? extends RequestHandler> entry : handler.entrySet()) {
            String[] mapperUrlSections = getMapperUrlSectionsCached(entry.getKey());
            if (isFor(mapperUrlSections, urlToMatchSections)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String[] getRequestUrlSections(String urlToMatch) {
        if (urlToMatch == null) {
            return null;
        }
        int qPos = urlToMatch.indexOf('?');
        if (qPos != -1) {
            urlToMatch = urlToMatch.substring(0, urlToMatch.indexOf("?"));
        }
        return urlToMatch.split("/");
    }

    private String[] getMapperUrlSectionsCached(String mapperUrl) {
        String[] sections = mapperUrlSectionsCache.get(mapperUrl);
        if (sections == null) {
            sections = mapperUrl.split("/");
            for (int i = 0; i < sections.length; i++) {
                String section = sections[i];
                // To work around a but in Selenium Grid 2.31.0.
                int qPos = section.indexOf('?');
                if (qPos != -1) {
                    sections[i] = section.substring(0, qPos);
                }
            }
            mapperUrlSectionsCache.put(mapperUrl, sections);
        }
        return sections;
    }

    protected boolean isFor(String[] mapperUrlSections, String[] urlToMatchSections) {
        if (urlToMatchSections == null) {
            return mapperUrlSections.length == 0;
        }
        if (mapperUrlSections.length != urlToMatchSections.length) {
            return false;
        }
        for (int i = 0; i < mapperUrlSections.length; i++) {
            if (!(mapperUrlSections[i].startsWith(":") || mapperUrlSections[i]
                    .equals(urlToMatchSections[i]))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void handleHttpRequest(IHttpRequest request, IHttpResponse response) throws Exception {
        RequestHandler handler = null;
        if ("GET".equals(request.method())) {
            handler = findMatcher(request, getHandler);
        } else if ("POST".equals(request.method())) {
            handler = findMatcher(request, postHandler);
        }

        handleRequest(request, response, handler);
    }

    public void handleRequest(IHttpRequest request, IHttpResponse response, RequestHandler handler) throws Exception {
        if ("/favicon.ico".equals(request.uri()) && handler == null) {
            response.setStatus(404).end();
            return;
        } else if (handler == null) {
            response.setStatus(404).end();
            return;
        }
        AppiumResponse result;
        result = handler.safeHandle(request);
        handleResponse(request, response, result);
    }

    protected void handleResponse(IHttpRequest request, IHttpResponse response,
                                  AppiumResponse result) {
        if (result != null) {
            String resultString = result.render();
            response.setContentType("application/json");
            response.setEncoding(Charset.forName("UTF-8"));
            response.setContent(resultString);
            response.setStatus(200);
        }
        response.end();
    }
}
