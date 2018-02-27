package io.appium.uiautomator2.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
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

    public FindElement(String mappedUri) {
        super(mappedUri);
    }

    /**
     * returns  UiObject2 for an xpath expression
     * TODO: Need to handle contextId based finding
     */
    private AndroidElement getXPathUiObject(final String expression, AndroidElement
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
        return coreFacade.findElement(nodeList);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException {
        try {
            Logger.info("Find element command");
            final JSONObject payload = getPayload(request);
            final String method = payload.getString("strategy");
            final String selector = payload.getString("selector");
            final String contextId = payload.getString("context");
            Logger.info("find element command using '%s' with selector '%s'.", method, selector);
            final By by = new By(method, selector);
            coreFacade.waitForIdle();
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
                result.put("ELEMENT", getCachedElements().add(element));
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
            case SELECTOR_ANDROID_UIAUTOMATOR:
                return coreFacade.findElement(new UiAutomatorParser().parse(by.getElementLocator()).get(0));
            case SELECTOR_XPATH:
                return getXPathUiObject(by.getElementLocator(), null /* AndroidElement */);
            default:
                return coreFacade.findElement(by.toBySelector());
        }
    }

    @Nullable
    private AndroidElement findElement(@NonNull final By by, @NonNull final String contextId) throws InvalidSelectorException,
            ParserConfigurationException, ClassNotFoundException, UiSelectorSyntaxException, UiObjectNotFoundException, NoSuchDriverException {
        AndroidElement element = getCachedElements().getElementFromCache(contextId);
        if (element == null) {
            throw new ElementNotFoundException();
        }
        switch (by.getElementStrategy()) {
            case SELECTOR_ANDROID_UIAUTOMATOR:
                return element.getChild(new UiAutomatorParser().parse(by.getElementLocator()).get(0));
            case SELECTOR_XPATH:
                return getXPathUiObject(by.getElementLocator(), element);
            default:
                return element.getChild(by.toBySelector());
        }
    }
}
