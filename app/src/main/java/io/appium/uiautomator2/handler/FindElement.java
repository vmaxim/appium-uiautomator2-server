package io.appium.uiautomator2.handler;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.common.exceptions.UiSelectorSyntaxException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.XPathFinder;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoList;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.UiAutomatorParser;

import static android.support.test.uiautomator.By.clazz;
import static android.support.test.uiautomator.By.desc;
import static android.support.test.uiautomator.By.res;
import static io.appium.uiautomator2.App.core;
import static io.appium.uiautomator2.App.session;

public class FindElement extends SafeRequestHandler {

    /**
     * java_package : type / name
     *
     * com.example.Test:id/enter
     *
     * ^[a-zA-Z_] - Java package must start with letter or underscore
     * [a-zA-Z0-9\._]* - Java package may contain letters, numbers, periods and
     * underscores : - : ends the package and starts the type [^\/]+ - type is
     * made up of at least one non-/ characters \\/ - / ends the type and starts
     * the name [\S]+$ - the name contains at least one non-space character and
     * then the line is ended
     *
     * Example:
     * http://java-regex-tester.appspot.com/regex/5f04ac92-f9aa-45a6-b1dc-e2c25fd3cc6b
     *
     */
    static final Pattern RESOURCE_ID_REGEX = Pattern
            .compile("^[a-zA-Z_][a-zA-Z0-9\\._]*:[^\\/]+\\/[\\S]+$");

    public FindElement(String mappedUri) {
        super(mappedUri);
    }

    /**
     * returns  UiObject2 for an xpath expression
     * TODO: Need to handle contextId based finding
     */
    private static AndroidElement getXPathUiObject(final String expression, AndroidElement
            element) throws ParserConfigurationException, InvalidSelectorException,
            ClassNotFoundException, UiAutomator2Exception {
        AccessibilityNodeInfo nodeInfo = null;
        if(element != null) {
            nodeInfo = element.getAccessibilityNodeInfo();
        }
        final AccessibilityNodeInfoList nodeList = XPathFinder.getNodesList(expression, nodeInfo
                /* AccessibilityNodeInfo */);
        if (nodeList.isEmpty()) {
                throw new ElementNotFoundException();
        }
        return core.getUiDeviceAdapter().findObject(nodeList);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        try {
            Logger.info("Find element command");
            final JSONObject payload = getPayload(request);
            final String method = payload.getString("strategy");
            final String selector = payload.getString("selector");
            final String contextId = payload.getString("context");
            Logger.info(String.format("find element command using '%s' with selector '%s'.", method, selector));
            final By by = new By(method, selector);
            core.getUiDeviceAdapter().waitForIdle();
            AndroidElement element;
            if(contextId.length() > 0) {
                element = this.findElement(by, contextId);
            } else {
                element = this.findElement(by);
            }
            if (element == null) {
                return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
            } else {
                JSONObject result = new JSONObject();
                result.put("ELEMENT", App.session.getCachedElements().add(element));
                return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, result);
            }
        } catch (UnsupportedOperationException e) {
            Logger.error("Unsupported operation: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (InvalidSelectorException e) {
            Logger.error("Invalid selector: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.INVALID_SELECTOR, e);
        } catch (ElementNotFoundException | UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch (ParserConfigurationException e) {
            Logger.error("Unable to parse configuration: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ClassNotFoundException e) {
            Logger.error("Class not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        } catch (UiSelectorSyntaxException e) {
            Logger.error("Unable to parse UiSelector: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch (UiAutomator2Exception e) {
            Logger.error("Exception while finding element: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
    }

    @Nullable
    private AndroidElement findElement(@NonNull final By by) throws InvalidSelectorException,
             ParserConfigurationException, ClassNotFoundException, UiSelectorSyntaxException, UiAutomator2Exception {
        switch (by.getElementStrategy()) {
            case SELECTOR_NATIVE_ID:
                return core.getUiDeviceAdapter().findObject(res(by.getElementLocator()));
            case SELECTOR_ACCESSIBILITY_ID:
                return core.getUiDeviceAdapter().findObject(desc(by.getElementLocator()));
            case SELECTOR_CLASS:
                return core.getUiDeviceAdapter().findObject(clazz(by.getElementLocator()));
            case SELECTOR_ANDROID_UIAUTOMATOR:
                return core.getUiDeviceAdapter().findObject(findByUiAutomator(by.getElementLocator()));
            case SELECTOR_XPATH:
                return getXPathUiObject(by.getElementLocator(), null /* AndroidElement */);
        }
        return null;
    }

    @Nullable
    private AndroidElement findElement(@NonNull final By by, @NonNull final String contextId) throws InvalidSelectorException,
            ParserConfigurationException, ClassNotFoundException, UiSelectorSyntaxException, UiAutomator2Exception, UiObjectNotFoundException {
        AndroidElement element = session.getCachedElements().getElementFromCache(contextId);
        if (element == null) {
            throw new ElementNotFoundException();
        }
        switch (by.getElementStrategy()) {
            case SELECTOR_NATIVE_ID:
                return element.getChild(res(by.getElementLocator()));
            case SELECTOR_ACCESSIBILITY_ID:
                return element.getChild(desc(by.getElementLocator()));
            case SELECTOR_CLASS:
                return element.getChild(clazz(by.getElementLocator()));
            case SELECTOR_ANDROID_UIAUTOMATOR:
                return element.getChild(findByUiAutomator(by.getElementLocator()));
            case SELECTOR_XPATH:
                return getXPathUiObject(by.getElementLocator(), element);
        }
        return null;
    }

    /**
     * finds the UiSelector for given expression
     */
    public UiSelector findByUiAutomator(String expression) throws UiSelectorSyntaxException {
        List<UiSelector> parsedSelectors = null;
        UiAutomatorParser uiAutomatorParser = new UiAutomatorParser();
        final List<UiSelector> selectors = new ArrayList<UiSelector>();
        try {
            parsedSelectors = uiAutomatorParser.parse(expression);
        } catch (final UiSelectorSyntaxException e) {
            throw new UiSelectorSyntaxException(
                    "Could not parse UiSelector argument: " + e.getMessage());
        }

        for (final UiSelector selector : parsedSelectors) {
            selectors.add(selector);
        }
        return selectors.get(0);
    }
}
