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

package io.appium.uiautomator2.common.exceptions;

import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import io.appium.uiautomator2.model.AccessibilityNodeInfoList;
import io.appium.uiautomator2.model.Finder;

/**
 * An exception thrown when the element can not be found.
 */

@SuppressWarnings("serial")
public class ElementNotFoundException extends Exception {
    private static final String ERR_MSG_ELEMENT_NOT_FOUND = "An element could not be located. Strategy: %s; Locator:%s";

    public ElementNotFoundException() {
        super(ERR_MSG_ELEMENT_NOT_FOUND);
    }

    private ElementNotFoundException(String strategy, Object locator) {
        super(String.format(ERR_MSG_ELEMENT_NOT_FOUND , strategy, locator));
    }
    public ElementNotFoundException(final AccessibilityNodeInfo node) {
        this("AccessibilityNodeInfo", node);
    }

    public ElementNotFoundException(final BySelector bySelector) {
        this("BySelector", bySelector);
    }

    public ElementNotFoundException(final UiSelector uiSelector) {
        this("UiSelector", uiSelector);
    }

    public ElementNotFoundException(final String extra) {
        super(ERR_MSG_ELEMENT_NOT_FOUND + extra);
    }

    public ElementNotFoundException(final Finder finder) {
        this("XPath", finder);
    }

    public ElementNotFoundException(Throwable t) {
        super(t);
    }

    public ElementNotFoundException(AccessibilityNodeInfoList nodeList) {
        this("AccessibilityNodeInfoList", nodeList);
    }
}
