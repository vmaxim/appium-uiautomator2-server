package io.appium.uiautomator2.handler.request;

import android.support.annotation.Nullable;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObjectNotFoundException;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.NoSuchContextException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.session.CachedElements;
import io.appium.uiautomator2.model.session.Session;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public abstract class SafeRequestHandler extends BaseRequestHandler {

    protected final String ELEMENT_ID_KEY_NAME = "element";

    public SafeRequestHandler(String mappedUri) {
        super(mappedUri);
    }

    @Nullable
    protected AndroidElement getElementFromCache(String id) throws StaleElementReferenceException, NoSuchDriverException {
        return getCachedElements().getElement(id);
    }

    protected Session getSession() throws NoSuchDriverException {
        return App.getSession();
    }

    protected CachedElements getCachedElements() throws NoSuchDriverException {
        return App.getSession().getCachedElements();
    }

    public abstract AppiumResponse safeHandle(IHttpRequest request) throws ElementNotFoundException, NoSuchDriverException, NoSuchContextException, StaleElementReferenceException, UiObjectNotFoundException, InvalidCoordinatesException;

    @Override
    public final AppiumResponse handle(IHttpRequest request) {
        try {
            return safeHandle(request);
        } catch (ElementNotFoundException e) {
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT, e);
        } catch (NoSuchContextException e) {
            //TODO: update error code when w3c spec gets updated
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_WINDOW, new UiAutomator2Exception("Invalid window handle was used: only 'NATIVE_APP' and 'WEBVIEW' are supported."));
        } catch (StaleElementReferenceException e) {
            Logger.error("Stale Element Reference Exception: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.STALE_ELEMENT_REFERENCE, e.getId());
        } catch (NoSuchDriverException e) {
            Logger.error("Session is either deleted or not started.");
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_DRIVER, e);
        } catch (NoClassDefFoundError e) {
            // This is a potentially interesting class path problem which should be returned to client.
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_COMMAND, e);
        } catch (Exception e) {
            // The advantage of catching general Exception here is that we can propagate the Exception to clients.
            Logger.error("Exception while handling action in: " + this.getClass().getName(), e);
            return AppiumResponse.forCatchAllError(getSessionId(request), e);
        } catch (Throwable e) {
            // Catching Errors seems like a bad idea in general but if we don't catch this, Netty will catch it anyway.
            // The advantage of catching it here is that we can propagate the Error to clients.
            Logger.error("Fatal error while handling action in: " + this.getClass().getName(), e);
            return AppiumResponse.forCatchAllError(getSessionId(request), e);
        }
    }
}
