package io.appium.uiautomator2.handler;

import android.support.annotation.Nullable;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
import io.appium.uiautomator2.model.ByStrategy;
import io.appium.uiautomator2.model.XPathFinder;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoList;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.UiAutomatorParser;

import static android.support.test.uiautomator.By.clazz;
import static android.support.test.uiautomator.By.desc;
import static android.support.test.uiautomator.By.res;
import static io.appium.uiautomator2.App.core;
import static io.appium.uiautomator2.App.session;

public class FindElements extends SafeRequestHandler {

    public FindElements(String mappedUri) {
        super(mappedUri);
    }

    /**
     * returns  UiObject2 for an xpath expression
     **/
    private static List<AndroidElement> getXPathUiObjects(final String expression, AndroidElement
            element) throws ElementNotFoundException, ParserConfigurationException,
            InvalidSelectorException, ClassNotFoundException, UiAutomator2Exception {
        AccessibilityNodeInfo nodeInfo = null;
        if(element != null) {
            nodeInfo = element.getAccessibilityNodeInfo();
        }
        final AccessibilityNodeInfoList nodeList = XPathFinder.getNodesList(expression, nodeInfo);
        if (nodeList.isEmpty()) {
            throw new ElementNotFoundException();
        }
        return core.getUiDeviceAdapter().findObjects(nodeList);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        JSONArray result = new JSONArray();
        try {
            Logger.info("Find elements command");
            JSONObject payload = getPayload(request);
            String method = payload.getString("strategy");
            String selector = payload.getString("selector");
            final String contextId = payload.getString("context");
            Logger.info(String.format("find element command using '%s' with selector '%s'.", method, selector));
            By by = new By(method, selector);
            core.getUiDeviceAdapter().waitForIdle();
            List<AndroidElement> elements;
            if(contextId.length() > 0) {
                elements = this.findElements(by, contextId);
            } else {
                elements = this.findElements(by);
            }

            for (AndroidElement element : elements) {
                String id = App.session.getCachedElements().add(element);
                JSONObject jsonElement = new JSONObject();
                jsonElement.put("ELEMENT", id);
                result.put(jsonElement);
            }
            return new AppiumResponse(getSessionId(request), result);
        } catch (ElementNotFoundException ignored) {
            /* For findElements up on no Element. instead of throwing exception unlike in findElement,
               empty array should be return. for more info refer:
               https://github.com/SeleniumHQ/selenium/wiki/JsonWireProtocol#sessionsessionidelements
              */
            return new AppiumResponse(getSessionId(request), result);
        } catch (UnsupportedOperationException e) {
            Logger.error("Unsupported operation: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (InvalidSelectorException e) {
            Logger.error("Invalid selector: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.INVALID_SELECTOR, e);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        } catch (ParserConfigurationException e) {
            Logger.error("Unable to parse configuration: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ClassNotFoundException e) {
            Logger.error("Class not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (UiSelectorSyntaxException e) {
            Logger.error("Unable to parse UiSelector: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch (UiAutomator2Exception e) {
            Logger.error("Exception while finding element: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
    }

    @Nullable
    private List<AndroidElement> findElements(By by) throws ElementNotFoundException,
            ParserConfigurationException, ClassNotFoundException, InvalidSelectorException, UiAutomator2Exception, UiSelectorSyntaxException {
        String locator = by.getElementLocator();
        ByStrategy strategy = by.getElementStrategy();
        switch (strategy) {
            case SELECTOR_NATIVE_ID:
                return core.getUiDeviceAdapter().findObjects(res(locator));
            case SELECTOR_ACCESSIBILITY_ID:
                return core.getUiDeviceAdapter().findObjects(desc(locator));
            case SELECTOR_CLASS:
                return core.getUiDeviceAdapter().findObjects(clazz(locator));
            case SELECTOR_ANDROID_UIAUTOMATOR:
                //TODO: need to handle the context parameter in a smart way
                return getUiObjectsUsingAutomator(parseStringToUiSelector(locator), "");
            case SELECTOR_XPATH:
                //TODO: need to handle the context parameter in a smart way
                return getXPathUiObjects(locator, null /* AndroidElement */);
        }
        return null;
    }

    @Nullable
    private List<AndroidElement> findElements(By by, String contextId) throws
            InvalidSelectorException, ParserConfigurationException, ClassNotFoundException,
            UiSelectorSyntaxException, UiAutomator2Exception, UiObjectNotFoundException {
        AndroidElement element = session.getCachedElements().getElementFromCache(contextId);
        if (element == null) {
            throw new ElementNotFoundException();
        }

        String locator = by.getElementLocator();
        ByStrategy strategy = by.getElementStrategy();
        switch (strategy) {
            case SELECTOR_NATIVE_ID:
                return element.getChildren(res(locator));
            case SELECTOR_XPATH:
                return getXPathUiObjects(locator, element);
            case SELECTOR_ACCESSIBILITY_ID:
                return element.getChildren(desc(locator));
            case SELECTOR_CLASS:
                return element.getChildren(clazz(locator));
            case SELECTOR_ANDROID_UIAUTOMATOR:
                return getUiObjectsUsingAutomator(parseStringToUiSelector(locator), contextId);
        }
        return null;
    }

    public List<UiSelector> parseStringToUiSelector(String expression) throws UiSelectorSyntaxException {
        UiAutomatorParser uiAutomatorParser = new UiAutomatorParser();
        try {
            return uiAutomatorParser.parse(expression);
        } catch (final UiSelectorSyntaxException e) {
            throw new UiSelectorSyntaxException(
                    "Could not parse UiSelector argument: " + e.getMessage());
        }
    }

    /**
     * returns  List<UiObject> using '-android automator' expression
     **/
    private List<AndroidElement> getUiObjectsUsingAutomator(List<UiSelector> selectors, String
            contextId) throws InvalidSelectorException, ClassNotFoundException {
        List<AndroidElement> foundElements = new ArrayList<>();
        for (final UiSelector sel : selectors) {
            // With multiple selectors, we expect that some elements may not
            // exist.
            try {
                Logger.debug("Using: " + sel.toString());
                if (contextId.isEmpty()) {
                    foundElements.addAll(core.getUiDeviceAdapter().findObjects(sel));
                } else {
                    AndroidElement element = session.getCachedElements().getElementFromCache(contextId);
                    final List<AndroidElement> elementsFromSelector = element.getChildren(sel);
                    foundElements.addAll(elementsFromSelector);
                }
            } catch (final UiObjectNotFoundException ignored) {
                //for findElements up on no elements, empty array should return.
            }
        }
        foundElements = ElementHelpers.dedupe(foundElements);
        return foundElements;
    }
}
