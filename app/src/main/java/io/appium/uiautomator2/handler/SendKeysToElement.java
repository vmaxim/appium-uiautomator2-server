package io.appium.uiautomator2.handler;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

/**
 * Send keys to a given element.
 */
public class SendKeysToElement extends SafeRequestHandler {

    public SendKeysToElement(String mappedUri) {
        super(mappedUri);
    }

    private static boolean isTextFieldClear(AndroidElement element) throws
            UiObjectNotFoundException {
        return element.getText() == null || element.getText().isEmpty();
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) throws NoSuchDriverException {
        try {
            Logger.info("send keys to element command");
            JSONObject payload = getPayload(request);
            AndroidElement element;
            if (payload.has("elementId")) {
                String id = payload.getString("elementId");
                element = getCachedElements().getElementFromCache(id);
                if (element == null) {
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
                }
            } else {
                //perform action on focused element
                try {
                    BySelector bySelector = By.focused(true);
                    element = coreFacade.findElement(bySelector);
                    getCachedElements().add(element);
                } catch (ElementNotFoundException e) {
                    Logger.debug("Error retrieving focused element: " + e);
                    return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
                } catch (UiAutomator2Exception e) {
                    Logger.debug("Error in finding focused element: " + e);
                    return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR,
                            "Unable to find a focused element." + Arrays.toString(e.getStackTrace()));
                }
            }
            boolean replace = Boolean.parseBoolean(payload.getString("replace"));
            String text = payload.getString("text");

            boolean pressEnter = false;
            if (text.endsWith("\\n")) {
                pressEnter = true;
                text = text.replace("\\n", "");
                Logger.debug("Will press enter after setting text");
            }

            boolean unicodeKeyboard = payload.has("unicodeKeyboard") &&
                    Boolean.parseBoolean(payload.getString("unicodeKeyboard"));

            String currText = element.getText();
            if (!isTextFieldClear(element)) {
                new Clear("/wd/hub/session/:sessionId/element/:id/clear").handle(request);
            }
            if (!isTextFieldClear(element)) {
                // clear could have failed, or we could have a hint in the field
                // we'll assume it is the latter
                Logger.debug("Text not cleared. Assuming remainder is hint text.");
                currText = "";
            }
            if (!replace && currText != null) {
                text = currText + text;
            }
            element.setText(text, unicodeKeyboard);

            String actionMsg = "";
            if (pressEnter) {
                actionMsg = coreFacade.pressEnter() ?
                        "Sent keys to the device" :
                        "Unable to send keys to the device";
            }
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, actionMsg);
        } catch (final UiObjectNotFoundException e) {
            Logger.error("Unable to Send Keys", e);
            return new AppiumResponse(getSessionId(request), WDStatus.NO_SUCH_ELEMENT);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }
}


