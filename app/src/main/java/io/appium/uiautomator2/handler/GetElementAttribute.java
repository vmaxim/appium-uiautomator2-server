package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoAttributeFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.App.model;

public class GetElementAttribute extends SafeRequestHandler {

    public GetElementAttribute(String mappedUri) {
        super(mappedUri);
    }

    private static int getScrollableOffset(AndroidElement uiScrollable) throws
            ClassNotFoundException, InvalidSelectorException, NoSuchDriverException, ElementNotFoundException, StaleElementReferenceException {
        AccessibilityNodeInfo nodeInfo = null;
        AndroidElement firstChild;
        int offset = 0;
        if (uiScrollable instanceof UiObjectAdapter) {
            firstChild = uiScrollable.findElement(new UiSelector().index(0));
        } else {
            UiObject2 uiObject2 = (UiObject2) uiScrollable.getUiObject();
            firstChild = model.getUiObjectAdapterFactory().create(uiObject2);
        }

        try {
            nodeInfo = firstChild.getAccessibilityNodeInfo();
        } catch (UiAutomator2Exception ignored) {
        }

        if (nodeInfo != null) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInParent(rect);
            offset = rect.height();
        }

        return offset;
    }

    private int getTouchPadding() throws UiObjectNotFoundException,
            ReflectiveOperationException {
        int touchPadding = coreFacade.getScaledTouchSlop();
        return touchPadding / 2;
    }

    private Object getAttribute(AndroidElement element, String attributeName) throws
            UiObjectNotFoundException, NoAttributeFoundException, ReflectiveOperationException, InvalidSelectorException, NoSuchDriverException {
        switch (attributeName) {
            case "name":
            case "text":
                return element.getText();
            case "className":
                return element.getClassName();
            case "contentDescription":
                return element.getContentDescription();
            case "resourceId":
            case "resource-id":
                return element.getResourceId();
            case "enabled":
                return String.valueOf(element.isEnabled());
            case "checkable":
                return String.valueOf(element.isCheckable());
            case "checked":
                return String.valueOf(element.isChecked());
            case "clickable":
                return element.isClickable();
            case "focusable":
                return element.isFocusable();
            case "focused":
                return element.isFocused();
            case "longClickable":
                return element.isLongClickable();
            case "scrollable":
                return element.isScrollable();
            case "selected":
                return element.isSelected();
            case "displayed":
                return element.exists();
            case "password":
                try {
                    return element.getAccessibilityNodeInfo().isPassword();
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            case "contentSize":
                Rect boundsRect = element.getBounds();
                ContentSize contentSize = new ContentSize(boundsRect);
                contentSize.touchPadding = getTouchPadding();
                try {
                    contentSize.scrollableOffset = getScrollableOffset(element);
                } catch (ElementNotFoundException|StaleElementReferenceException e) {
                    Logger.debug("");
                }
                return contentSize.toString();
            default:
                throw new NoAttributeFoundException(attributeName);
        }
    }
    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException, StaleElementReferenceException {
        Logger.info("get attribute of element command");
        String id = getElementId(request);
        String attributeName = getNameAttribute(request);
        AndroidElement element = getCachedElements().getElement(id);
        try {
            String attributeValue = String.valueOf(getAttribute(element, attributeName));
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, attributeValue);
        } catch (UiObjectNotFoundException e) {
            Logger.error(MessageFormat.format("Element not found while trying to get attribute '{0}'", attributeName), e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR);
        } catch (NoAttributeFoundException e) {
            Logger.error(MessageFormat.format("Requested attribute {0} not supported.", attributeName), e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch (UiAutomator2Exception e) {
            Logger.error(MessageFormat.format("Unable to retrieve attribute {0}", attributeName), e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ReflectiveOperationException | InvalidSelectorException e) {
            Logger.error("Can not access to method or field: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }

    }

    private static class ContentSize {
        private int width;
        private int height;
        private int top;
        private int left;
        private int scrollableOffset;
        private int touchPadding;

        ContentSize(Rect rect) {
            width = rect.width();
            height = rect.height();
            top = rect.top;
            left = rect.left;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("width", width);
                jsonObject.put("height", height);
                jsonObject.put("top", top);
                jsonObject.put("left", left);
                jsonObject.put("scrollableOffset", scrollableOffset);
                jsonObject.put("touchPadding", touchPadding);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }
}
