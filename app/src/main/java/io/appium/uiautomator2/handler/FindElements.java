package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.common.exceptions.UiSelectorSyntaxException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AccessibilityNodeInfoList;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.ByStrategy;
import io.appium.uiautomator2.model.XPathFinder;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.ElementHelpers;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.UiAutomatorParser;

public class FindElements extends SafeRequestHandler {

    public FindElements(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException {
        JSONArray result = new JSONArray();
        try {
            Logger.info("Find elements command");
            JSONObject payload = getPayload(request);
            String method = payload.getString("strategy");
            String selector = payload.getString("selector");
            final String contextId = payload.getString("context");
            Logger.info("find element command using '%s' with selector '%s'.", method, selector);
            By by = new By(method, selector);
            coreFacade.waitForIdle();
            List<AndroidElement> elements;
            if(contextId.length() > 0) {
                elements = this.findElements(by, contextId);
            } else {
                elements = this.findElements(by);
            }

            for (AndroidElement element : elements) {
                String id = getCachedElements().add(element);
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

    private List<AndroidElement> findElements(By by) throws ElementNotFoundException,
            ParserConfigurationException, ClassNotFoundException, InvalidSelectorException, UiSelectorSyntaxException {
        String locator = by.getElementLocator();
        ByStrategy strategy = by.getElementStrategy();
        switch (strategy) {
            case SELECTOR_ANDROID_UIAUTOMATOR:
                //TODO: need to handle the context parameter in a smart way
                return getUiObjectsUsingAutomator(new UiAutomatorParser().parse(locator), "");
            case SELECTOR_XPATH:
                //TODO: need to handle the context parameter in a smart way
                return getXPathUiObjects(locator, null /* AndroidElement */);
            default:
                return coreFacade.findElements(by.toBySelector());
        }
    }

    private List<AndroidElement> findElements(By by, String contextId) throws
            InvalidSelectorException, ParserConfigurationException, ClassNotFoundException,
            UiSelectorSyntaxException, UiAutomator2Exception, UiObjectNotFoundException {
        AndroidElement element = getCachedElements().getElementFromCache(contextId);
        if (element == null) {
            throw new ElementNotFoundException();
        }

        String locator = by.getElementLocator();
        ByStrategy strategy = by.getElementStrategy();
        switch (strategy) {
            case SELECTOR_ANDROID_UIAUTOMATOR:
                return getUiObjectsUsingAutomator(new UiAutomatorParser().parse(locator), contextId);
            case SELECTOR_XPATH:
                return getXPathUiObjects(locator, element);
            default:
                return element.getChildren(by.toBySelector());
        }
    }

    /**
     * returns  List<UiObject> using '-android automator' expression
     **/
    private List<AndroidElement> getUiObjectsUsingAutomator(List<UiSelector> selectors, String
            contextId) throws InvalidSelectorException, ClassNotFoundException, NoSuchDriverException {
        List<AndroidElement> foundElements = new ArrayList<>();
        for (final UiSelector sel : selectors) {
            // With multiple selectors, we expect that some elements may not
            // exist.
            try {
                Logger.debug("Using: " + sel.toString());
                if (contextId.isEmpty()) {
                    foundElements.addAll(coreFacade.findElements(sel));
                } else {
                    AndroidElement element = getCachedElements().getElementFromCache(contextId);
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

    /**
     * returns  UiObject2 for an xpath expression
     **/
    private List<AndroidElement> getXPathUiObjects(final String expression, AndroidElement
            element) throws ParserConfigurationException,
            InvalidSelectorException, ClassNotFoundException, UiAutomator2Exception {
        AccessibilityNodeInfo nodeInfo = null;
        if(element != null) {
            nodeInfo = element.getAccessibilityNodeInfo();
        }
        final AccessibilityNodeInfoList nodeList = XPathFinder.getNodesList(expression, nodeInfo);
        if (nodeList.isEmpty()) {
            throw new ElementNotFoundException();
        }
        return coreFacade.findElements(nodeList);
    }
}
