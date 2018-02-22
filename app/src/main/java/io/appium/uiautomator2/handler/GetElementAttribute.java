package io.appium.uiautomator2.handler;

import android.graphics.Rect;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoAttributeFoundException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.App.model;
import static io.appium.uiautomator2.App.session;

public class GetElementAttribute extends SafeRequestHandler {

    public GetElementAttribute(String mappedUri) {
        super(mappedUri);
    }

    private static int getScrollableOffset(AndroidElement uiScrollable) throws
            UiObjectNotFoundException, ClassNotFoundException, InvalidSelectorException {
        AccessibilityNodeInfo nodeInfo = null;
        AndroidElement firstChild;
        int offset = 0;
        if (uiScrollable instanceof UiObjectAdapter) {
            firstChild = uiScrollable.getChild(new UiSelector().index(0));
        } else {
            UiObject2 uiObject2 = uiScrollable.getUiObject();
            firstChild = model.getUiObjectElementFactory().create(uiObject2);
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

    private static int getTouchPadding(AndroidElement element) throws UiObjectNotFoundException,
            ReflectiveOperationException {
        int touchPadding = App.core.getGesturesAdapter().getViewConfig().getScaledTouchSlop();
        return touchPadding / 2;
    }

    private Object getAttribute(AndroidElement element, String attributeName) throws
            UiObjectNotFoundException, NoAttributeFoundException, ReflectiveOperationException, InvalidSelectorException {
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
                return element.getAccessibilityNodeInfo() != null;
            case "password":
                return element.getAccessibilityNodeInfo().isPassword();
            case "contentSize":
                Rect boundsRect = element.getBounds();
                ContentSize contentSize = new ContentSize(boundsRect);
                contentSize.touchPadding = getTouchPadding(element);
                contentSize.scrollableOffset = getScrollableOffset(element);
                return contentSize.toString();
            default:
                throw new NoAttributeFoundException(attributeName);
        }
    }
    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("get attribute of element command");
        String id = getElementId(request);
        String attributeName = getNameAttribute(request);
        AndroidElement element = session.getCachedElements().getElementFromCache(id);
        if (element == null) {
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
        try {
            String attributeValue = String.valueOf(getAttribute(element, attributeName));
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, attributeValue);
        } catch (UiObjectNotFoundException e) {
            Logger.error(MessageFormat.format("Element not found while trying to get attribute '{0}'", attributeName), e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR);
        } catch (NoAttributeFoundException e) {
            Logger.error(MessageFormat.format("Requested attribute {0} not supported.", attributeName), e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch(StaleObjectException e){
            Logger.error("Stale Element Exception: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.STALE_ELEMENT_REFERENCE, e);
        } catch (UiAutomator2Exception e) {
            Logger.error(MessageFormat.format("Unable to retrieve attribute {0}", attributeName), e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        } catch (ReflectiveOperationException | InvalidSelectorException e) {
            Logger.error("Can not access to method or field: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }

    }

    private static class ContentSize {
        int width;
        int height;
        int top;
        int left;
        int scrollableOffset;
        int touchPadding;

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
