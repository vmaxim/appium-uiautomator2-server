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

package io.appium.uiautomator2.utils;

import android.support.annotation.Nullable;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import io.appium.uiautomator2.common.exceptions.InvalidElementStateException;
import io.appium.uiautomator2.common.exceptions.NoAlertOpenException;

import static android.text.TextUtils.join;
import static io.appium.uiautomator2.utils.Device.getUiDevice;
import static org.apache.commons.lang.StringUtils.isBlank;

public class AlertHelpers {
    private static final String TAG = AlertHelpers.class.getSimpleName();

    private static final String regularAlertButtonResIdPrefix = "android:id/button";
    private static final Pattern regularAlertButtonResIdPattern =
            Pattern.compile("^" + regularAlertButtonResIdPrefix + "\\d+$");
    private static final String alertContentResId = "android:id/content";
    private static final Pattern regularAlertTitleResIdPattern =
            Pattern.compile(".+:id/(alertTitle|custom)$");
    private static final Pattern permissionAlertTitleResIdPattern =
            Pattern.compile(".+:id/permission_message$");
    private static final Pattern permissionAlertButtonResIdPattern =
            Pattern.compile(".+:id/permission_\\w+_button$");
    private static final Pattern alertElementsResIdPattern = Pattern.compile(".+:id/.+");

    private static String buttonResIdByIdx(int index) {
        return String.format("%s%s", regularAlertButtonResIdPrefix, index);
    }

    private static AlertType getAlertType() {
        Device.waitForIdle();

        if (!getUiDevice().findObjects(By.res(regularAlertTitleResIdPattern)).isEmpty()) {
            return AlertType.REGULAR;
        }
        if (!getUiDevice().findObjects(By.res(permissionAlertTitleResIdPattern)).isEmpty()) {
            return AlertType.PERMISSION;
        }

        throw new NoAlertOpenException();
    }

    @Nullable
    private static UiObject2 filterButtonByLabel(Collection<UiObject2> buttons, String label) {
        for (UiObject2 button : buttons) {
            if (Objects.equals(button.getText(), label)) {
                return button;
            }
        }
        return null;
    }

    @Nullable
    private static UiObject2 getRegularAlertButton(AlertAction action, @Nullable String buttonLabel) {
        final Map<String, UiObject2> alertButtonsMapping = new HashMap<>();
        final List<Integer> buttonIndexes = new ArrayList<>();
        for (final UiObject2 button : getUiDevice().findObjects(By.res(regularAlertButtonResIdPattern))) {
            final String resId = button.getResourceName();
            alertButtonsMapping.put(resId, button);
            buttonIndexes.add(Integer.parseInt(resId.substring(regularAlertButtonResIdPrefix.length())));
        }

        if (buttonLabel == null) {
            final int minButtonId = Collections.min(buttonIndexes);
            return action == AlertAction.ACCEPT
                    ? alertButtonsMapping.get(buttonResIdByIdx(minButtonId))
                    : alertButtonsMapping.get(buttonResIdByIdx(alertButtonsMapping.size() > 1
                    ? minButtonId + 1
                    : minButtonId));
        }
        return filterButtonByLabel(alertButtonsMapping.values(), buttonLabel);
    }

    @Nullable
    private static UiObject2 getPermissionAlertButton(AlertAction action, @Nullable String buttonLabel) {
        final List<UiObject2> buttons = getUiDevice()
                .findObjects(By.res(permissionAlertButtonResIdPattern));
        if (buttonLabel == null) {
            if (action == AlertAction.ACCEPT) {
                return buttons.size() > 1 ? buttons.get(1) : buttons.get(0);
            }
            if (action == AlertAction.DISMISS && buttons.size() > 0) {
                return buttons.get(0);
            }
        } else {
            return filterButtonByLabel(buttons, buttonLabel);
        }
        return null;
    }

    /**
     * Accept or dismiss on-screen alert.
     *
     * @param action      either ACCEPT or DISMISS
     * @param buttonLabel if this parameter is set then the method
     *                    will look for the dialog button with this particular
     *                    text instead of the default one (usually it is the first button
     *                    for ACCEPT and the last one for DISMISS action)
     * @return the actual label of the clicked button
     * @throws NoAlertOpenException         if no dialog is present on the screen
     * @throws InvalidElementStateException if no matching button can be found
     */
    public static String handle(AlertAction action, @Nullable String buttonLabel) {
        final AlertType alertType = getAlertType();

        final UiObject2 dstButton = alertType == AlertType.REGULAR
                ? getRegularAlertButton(action, buttonLabel)
                : getPermissionAlertButton(action, buttonLabel);
        if (dstButton == null) {
            throw new InvalidElementStateException("The expected button cannot be detected on the alert");
        }

        final String actualLabel = dstButton.getText();
        Logger.info(String.format("Clicking dialog button '%s' in order to %s it",
                actualLabel, action.name().toLowerCase()));
        dstButton.click();
        return actualLabel;
    }

    /**
     * @return The actual text of the on-screen dialog. An empty
     * string is going to be returned if the dialog contains no text.
     * @throws NoAlertOpenException if no dialog is present on the screen
     */
    public static String getText() {
        final AlertType alertType = getAlertType();

        final List<UiObject2> alertRoots = getUiDevice().findObjects(By.res(alertContentResId));
        if (alertRoots.isEmpty()) {
            throw new NoAlertOpenException();
        }

        final List<String> result = new ArrayList<>();
        final List<UiObject2> alertElements = alertRoots.get(0).findObjects(By.res(alertElementsResIdPattern));
        Log.d(TAG, String.format("Got %d alert elements", alertElements.size()));
        final String alertButtonsResIdPattern = alertType == AlertType.REGULAR
                ? regularAlertButtonResIdPattern.toString()
                : permissionAlertButtonResIdPattern.toString();
        for (final UiObject2 element : alertElements) {
            final String resName = element.getResourceName();
            if (resName == null || resName.matches(alertButtonsResIdPattern)) {
                continue;
            }

            final String text = element.getText();
            if (isBlank(text)) {
                continue;
            }

            result.add(text);
        }
        return join("\n", result);
    }

    public enum AlertAction {
        ACCEPT, DISMISS
    }

    public enum AlertType {
        REGULAR, PERMISSION
    }
}
