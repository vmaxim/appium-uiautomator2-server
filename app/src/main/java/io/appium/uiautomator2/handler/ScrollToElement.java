package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.AppiumServlet;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.App.session;

public class ScrollToElement extends SafeRequestHandler {

    public ScrollToElement(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Scroll into view command");
        String id = getElementId(request);
        String scrollToId = getElementNextId(request);
        StringBuilder errorMsg = new StringBuilder();
        UiObject elementUiObject = null;
        UiObject scrollElementUiObject = null;

        AndroidElement element = session.getCachedElements().getElementFromCache(id);
        if (element == null) {
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }
        AndroidElement scrollToElement = session.getCachedElements().getElementFromCache(scrollToId);
        if (scrollToElement == null) {
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        }

        // attempt to get UiObjects from the container and scroll to element
        // if we can't, we have to error out, since scrollIntoView only works with UiObjects
        // (and not for example UiObject2)
        // TODO make an equivalent implementation of this method for UiObject2 if possible
        try {
            elementUiObject = (UiObject) element.getUiObject();
            try {
                scrollElementUiObject = (UiObject) scrollToElement.getUiObject();
            } catch (Exception e) {
                errorMsg.append("Scroll to Element");
            }
        } catch (Exception e) {
            errorMsg.append("Element");
        }

        if (!errorMsg.toString().isEmpty()) {
            errorMsg.append(" was not an instance of UiObject; only UiSelector is supported. " +
                            "Ensure you use the '-android uiautomator' locator strategy when " +
                            "finding elements for use with ScrollToElement");
            Logger.error(errorMsg.toString());
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, errorMsg);
        }

        try {
            UiScrollable uiScrollable = new UiScrollable(elementUiObject.getSelector());

            boolean elementIsFound = uiScrollable.scrollIntoView(scrollElementUiObject);

            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, elementIsFound);
        } catch (UiObjectNotFoundException e) {
            Logger.error("Element not found: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch(StaleObjectException e){
            Logger.error("Stale Element Exception: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.STALE_ELEMENT_REFERENCE, e);
        }
    }

    private class UiScrollable extends android.support.test.uiautomator.UiScrollable {

        /**
         * Constructor.
         *
         * @param container a {@link UiSelector} selector to identify the scrollable
         *                  layout element.
         * @since API Level 16
         */
        public UiScrollable(UiSelector container) {
            super(container);
        }

        @Override
        public boolean scrollIntoView(UiObject obj) throws UiObjectNotFoundException {
            if (obj.exists()) {
                return true;
            }

            // we will need to reset the search from the beginning to start search
            flingToBeginning(getMaxSearchSwipes());
            if (obj.exists()) {
                return true;
            }


            for (int x = 0; x < getMaxSearchSwipes(); x++) {
                if (!scrollForward()) {
                    return false;
                }

                if (obj.exists()) {
                    return true;
                }
            }

            return false;
        }

    }

    private static String getElementNextId(IHttpRequest request) {
        return (String) request.data().get(AppiumServlet.ELEMENT_ID_NEXT_KEY);
    }
}
