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
package io.appium.uiautomator2.core;

import android.app.UiAutomation;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;

/**
 * UiAutomation in android open source project will Support multi-window searches for API
 * level 21, which has not been implemented in UiAutomatorViewer capture layout hierarchy, to
 * be in sync with UiAutomatorViewer customizing getWindowRoots() method to skip the
 * multi-window search based user passed property
 */
public class RootNodesFinder {
    private static final String MSG_SKIP_NULL_ROOT_NODE = "Skipping null root node for window: %s";
    private static final String ERR_MSG_NULL_ROOT_NODE = "Unable to get Root in Active window, " +
            "ERROR: null root node returned by UiTestAutomationBridge.";
    private static final int ROOT_NODE_POLLING_INTERVAL = 1000;
    private static final int ROOT_NODE_RETRIES_COUNT = 5;

    private final UiAutomation uiAutomation;
    private final Integer apiLevelActual;
    private final Boolean allowMultiWindow;

    @Inject
    public RootNodesFinder(@NonNull final UiAutomation uiAutomation,
                           @NonNull @Named("apiLevelActual") final Integer apiLevelActual,
                           @NonNull @Named("allowMultiWindow") final Boolean allowMultiWindow) {
        this.uiAutomation = uiAutomation;
        this.apiLevelActual = apiLevelActual;
        this.allowMultiWindow = allowMultiWindow;

    }

    /**
     * @return List of window roots
     */
    @NonNull
    public AccessibilityNodeInfo[] getWindowRoots() {
        final List<AccessibilityNodeInfo> ret;
        /*
          TODO: MULTI_WINDOW is disabled
          UIAutomatorViewer captures active window properties and
          end users always relay on UIAutomatorViewer while writing tests.
          If we enable MULTI_WINDOW it effects end users.
          https://code.google.com/p/android/issues/detail?id=207569
         */
        if (apiLevelActual >= Build.VERSION_CODES.LOLLIPOP && allowMultiWindow) {
            ret = getMultiWindowRoots();
        } else {
            ret = getActiveWindowRoot();
        }
        return ret.toArray(new AccessibilityNodeInfo[ret.size()]);
    }

    /**
     * @return List of roots of all interactable windows
     */
    @RequiresApi(21)
    private List<AccessibilityNodeInfo> getMultiWindowRoots() {
        final List<AccessibilityNodeInfo> result = new ArrayList<>();
        AccessibilityNodeInfo root;
        // Support multi-window searches for API level 21 and up
        for (final AccessibilityWindowInfo window : uiAutomation.getWindows()) {
            root = window.getRoot();
            if (root == null) {
                Logger.debug(MSG_SKIP_NULL_ROOT_NODE, window.toString());
                continue;
            }
            result.add(root);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * @return Active window root
     */
    private List<AccessibilityNodeInfo> getActiveWindowRoot() {
        /*
         TODO: As we can't proceed to find element with out root node,
         TODO: retrying for 5 times to get the root node if UiAutomation reruns null
         TODO: need to handle gracefully
         */
        //AccessibilityNodeInfo should not be null.
        int retryCount = 0;
        do {
            AccessibilityNodeInfo root = uiAutomation.getRootInActiveWindow();
            if (root != null) {
                return Collections.singletonList(root);
            }
            Logger.debug(ERR_MSG_NULL_ROOT_NODE + ". Retrying: " + retryCount);
            SystemClock.sleep(ROOT_NODE_POLLING_INTERVAL);
            retryCount++;
        } while (retryCount < ROOT_NODE_RETRIES_COUNT);
        throw new UiAutomator2Exception(ERR_MSG_NULL_ROOT_NODE);
    }
}
