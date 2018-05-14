/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.appium.uiautomator2.unittest.test.internal.commands;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.unittest.test.Config;
import io.appium.uiautomator2.unittest.test.internal.Client;
import io.appium.uiautomator2.unittest.test.internal.Response;
import io.appium.uiautomator2.unittest.test.internal.TestUtils;
import io.appium.uiautomator2.utils.Logger;

import static android.os.SystemClock.elapsedRealtime;

@SuppressWarnings("JavaDoc")
public class DeviceCommands {

    /**
     * finds the element using By selector
     *
     * @param by Element locator
     * @return Response from UiAutomator2 server
     */
    public static Response findElement(By by) {
        return findElement(by, "");
    }

    public static Response findElement(By by, Long timeout) {
        return findElement(by, "", timeout);
    }

    /**
     * finds the element using By selector
     *
     * @param by        Element locator
     * @param contextId Context id
     * @param timeout   Explicit timeout
     * @return Response from UiAutomator2 server
     */
    public static Response findElement(By by, String contextId, Long timeout) {
        final long start = elapsedRealtime();
        JSONObject json = TestUtils.convertByToJson(by, contextId);
        Response response;
        do {
            response = Client.post("/element", json);
            Logger.info("Find element response: " + response);
            if (response.isSuccessful()) {
                return response;
            }
            TestUtils.waitForMillis(Config.DEFAULT_POLLING_INTERVAL);
        } while (elapsedRealtime() - start < timeout);
        return response;
    }

    public static Response findElement(By by, String contextId) {
        return findElement(by, contextId, Config.IMPLICIT_TIMEOUT);
    }

    /**
     * finds the elements using By selector
     *
     * @param by Element locator
     * @return Response from UiAutomator2 server
     */
    public static Response findElements(By by) {
        JSONObject json = TestUtils.convertByToJson(by, "");
        return Client.post("/elements", json);
    }

    /**
     * finds the elements using By selector
     *
     * @param by        Element locator
     * @param contextId Context id
     * @return Response from UiAutomator2 server
     */
    public static Response findElements(By by, String contextId) {
        JSONObject json = TestUtils.convertByToJson(by, contextId);
        return Client.post("/elements", json);
    }

    /**
     * Finds the height and width of screen
     *
     * @return Response from UiAutomator2 server
     */
    public static Response getDeviceSize() {
        Response response = Client.get("/window/current/size");
        Logger.info("Device window Size response:" + response);
        return response;
    }

    /**
     * performs screen rotation
     *
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response rotateScreen(String orientation) throws JSONException {
        JSONObject postBody = new JSONObject().put("orientation", orientation);
        return Client.post("/orientation", postBody);
    }

    /**
     * return screen orientation
     *
     * @return Response from UiAutomator2 server
     */
    public static String getScreenOrientation() {
        Response response = Client.get("/orientation");
        return response.getValue();
    }

    /**
     * return rotation
     *
     * @return Response from UiAutomator2 server
     */
    public static JSONObject getRotation() {
        Response response = Client.get("/rotation");
        return response.getValue();
    }

    /**
     * return rotation
     *
     * @return Response from UiAutomator2 server
     */
    public static Response setRotation(JSONObject rotateMap) {
        return Client.post("/rotation", rotateMap);
    }

    public static Response source() {
        return Client.get("/source");
    }

    public static Response createSession() throws JSONException {
        JSONObject caps = new JSONObject();
        caps.put("appPackage", Config.APP_PKG);
        return Client.post(Config.HOST + "/wd/hub", "/session",
                new JSONObject().put("desiredCapabilities", caps));
    }

    public static Response deleteSession() {
        return Client.delete();
    }

    /**
     * return the appStrings
     *
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response appStrings() {
        JSONObject jsonObject = new JSONObject();
        return Client.post("/appium/app/strings", jsonObject);
    }

    /**
     * update setting
     *
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response updateSetting(String settingName, Object settingValue) throws
            JSONException {
        JSONObject postBody = new JSONObject();
        postBody.put("settings", new JSONObject().put(settingName, settingValue));
        return Client.post("/appium/settings", postBody);
    }

    /**
     * update settings
     *
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response updateSettings(JSONObject settings) throws
            JSONException {
        return Client.post("/appium/settings", new JSONObject().put("settings", settings));
    }

    /**
     * return settings
     *
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response getSettings() {
        return Client.get("/appium/settings");
    }

    /**
     * performs scroll to the given text
     *
     * @param scrollToText
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response scrollTo(String scrollToText) throws JSONException {
        // TODO Create JSON object instead of below json string.Once the json is finalised from
        // driver module
        String json = " {\"cmd\":\"action\",\"action\":\"find\"," +
                "\"params\":{\"strategy\":\"-android uiautomator\",\"selector\":\"" +
                "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView" +
                "(new UiSelector().descriptionContains(\\\"" + scrollToText + "\\\").instance(0))" +
                ";" +
                "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView" +
                "(new UiSelector().textContains(\\\"" + scrollToText + "\\\").instance(0));" +
                "\",\"context\":\"\",\"multiple\":false}}";
        JSONObject jsonObject = new JSONObject(json);
        return Client.post("/touch/scroll", jsonObject);
    }

    /**
     * Get device screenshot
     *
     * @return Base64-encoded screenshot string
     */
    public static Response screenshot() {
        return Client.get("/screenshot");
    }

    /**
     * Accepts an on-screen alert
     *
     * @param buttonLabel optional button label to click on
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response acceptAlert(@Nullable String buttonLabel) throws JSONException {
        final JSONObject payload = new JSONObject();
        if (buttonLabel != null) {
            payload.put("buttonLabel", buttonLabel);
        }
        return Client.post("/alert/accept", payload);
    }

    /**
     * Dismisses an on-screen alert
     *
     * @param buttonLabel optional button label to click on
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response dismissAlert(@Nullable String buttonLabel) throws JSONException {
        final JSONObject payload = new JSONObject();
        if (buttonLabel != null) {
            payload.put("buttonLabel", buttonLabel);
        }
        return Client.post("/alert/dismiss", payload);
    }

    /**
     * Gets the text content of an on-screen alert
     *
     * @return Response from UiAutomator2 server
     */
    public static Response getAlertText() {
        return Client.get("/alert/text");
    }

    /**
     * Performs W3C action
     *
     * @param actions valid W3C actions list
     * @return Response from UiAutomator2 server
     * @throws JSONException
     */
    public static Response performActions(JSONArray actions) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("actions", actions);
        return Client.post("/actions", payload);
    }

    /**
     * Press Back button
     *
     * @return Response from UiAutomator2 server
     */
    public static Response pressBack() {
        return Client.post("/back", new JSONObject());
    }
}
