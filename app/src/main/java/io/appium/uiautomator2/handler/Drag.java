package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidCoordinatesException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;
import io.appium.uiautomator2.utils.PositionHelper;

public class Drag extends SafeRequestHandler {
    public Drag(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException, StaleElementReferenceException, InvalidCoordinatesException, UiObjectNotFoundException {
        // DragArguments is created on each execute which prevents leaking state
        // across executions.
        final DragArguments dragArgs;
        try {
            dragArgs = new DragArguments(request);
            if (getPayload(request).has("elementId")) {
                return dragElement(dragArgs, request);
            } else {
                return drag(dragArgs, request);
            }
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
        }
    }

    private AppiumResponse drag(final DragArguments dragArgs, final IHttpRequest request) {
        Point absStartPos;
        Point absEndPos;

        try {
            absStartPos = PositionHelper.getDeviceAbsPos(dragArgs.start);
            absEndPos = PositionHelper.getDeviceAbsPos(dragArgs.end);
        } catch (final InvalidCoordinatesException e) {
            Logger.error("The coordinates provided to an interactions operation are invalid. ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.INVALID_ELEMENT_COORDINATES, e);
        }

        Logger.debug("Dragging from " + absStartPos.toString() + " to "
                + absEndPos.toString() + " with steps: " + dragArgs.steps.toString());
        final boolean res = coreFacade.drag(absStartPos.x.intValue(),
                absStartPos.y.intValue(), absEndPos.x.intValue(),
                absEndPos.y.intValue(), dragArgs.steps);
        if (!res) {
            return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, "Drag did not complete successfully");
        }
        return new AppiumResponse(getSessionId(request), res);
    }

    private AppiumResponse dragElement(final DragArguments dragArgs, final IHttpRequest request) throws UiObjectNotFoundException, InvalidCoordinatesException {
        Point absEndPos;

        if (dragArgs.destEl == null) {
            try {
                absEndPos = PositionHelper.getDeviceAbsPos(dragArgs.end);
            } catch (final InvalidCoordinatesException e) {
                return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, e);
            }

            Logger.debug("Dragging the element with id " + dragArgs.elId
                    + " to " + absEndPos.toString() + " with steps: "
                    + dragArgs.steps.toString());
                final boolean res = dragArgs.el.dragTo(absEndPos.x.intValue(),
                        absEndPos.y.intValue(), dragArgs.steps);
                if (!res) {
                    return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, "Drag did not complete successfully");
                } else {
                    return new AppiumResponse(getSessionId(request), res);
                }

        } else {

            Logger.debug("Dragging the element with id " + dragArgs.elId
                    + " to destination element with id " + dragArgs.destElId
                    + " with steps: " + dragArgs.steps);
                final boolean res = dragArgs.el.dragTo(dragArgs.destEl, dragArgs.steps);
                if (!res) {
                    return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, "Drag did not complete successfully");
                } else {
                    return new AppiumResponse(getSessionId(request), res);
                }
        }

    }

    private class DragArguments {

        public final Point start;
        public final Point end;
        public final Integer steps;
        public AndroidElement el;
        public AndroidElement destEl;
        public String elId;
        public String destElId;

        public DragArguments(final IHttpRequest request) throws JSONException, NoSuchDriverException, StaleElementReferenceException {

            JSONObject payload = getPayload(request);

            if (payload.has("elementId")) {
                elId = payload.getString("elementId");
                el = getCachedElements().getElement(elId);
            }
            if (payload.has("destElId")) {
                destElId = payload.getString("destElId");
                destEl = getCachedElements().getElement(destElId);
            }

            start = new Point(payload.get("startX"), payload.get("startY"));
            end = new Point(payload.get("endX"), payload.get("endY"));
            steps = (Integer) payload.get("steps");
        }
    }
}
