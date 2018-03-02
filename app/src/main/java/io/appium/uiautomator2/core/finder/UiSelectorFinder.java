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
package io.appium.uiautomator2.core.finder;

import android.support.annotation.NonNull;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.core.UiDeviceAdapter;
import io.appium.uiautomator2.model.AccessibilityNodeInfo2UiSelector;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapterFactory;
import io.appium.uiautomator2.utils.Logger;

/**
 * The helper class for finding {@link AndroidElement} by {@link UiSelector}.
 */
public class UiSelectorFinder {

    private static final Pattern UI_SELECTOR_ENDS_WITH_INSTANCE = Pattern.compile("" +
            ".*INSTANCE=\\d+]$");
    private static final String ERR_MSG_CONVERT_UIOBJECT2_TO_UIOBJECT = "Unable to convert UiObject2 to " +
            "UiObject. UiObject2 id:%s. Generated UiSelector:%s";

    private final UiObjectAdapterFactory uiObjectAdapterFactory;
    private final UiDeviceAdapter uiDeviceAdapter;

    @Inject
    public UiSelectorFinder(@NonNull final UiObjectAdapterFactory uiObjectAdapterFactory,
                            @NonNull final UiDeviceAdapter uiDeviceAdapter) {
        this.uiObjectAdapterFactory = uiObjectAdapterFactory;
        this.uiDeviceAdapter = uiDeviceAdapter;
    }

    @NonNull
    public AndroidElement findElement(@NonNull final UiSelector selector)
            throws ElementNotFoundException {
        uiDeviceAdapter.waitForIdle();
        final UiObject uiObject = uiDeviceAdapter.getUiDevice().findObject(selector);
        /* UiDevice.findObject() does not throw UiObjectNotFoundException */
        if (!uiObject.exists()) {
            throw new ElementNotFoundException(selector);
        }
        return uiObjectAdapterFactory.create(uiObject);
    }


    @NonNull
    public AndroidElement findElement(@NonNull final UiObjectAdapter parentContext,
                                      @NonNull final UiSelector selector)
            throws StaleElementReferenceException, ElementNotFoundException {
        try {
            /* Check if parent element is stale */
            if (!parentContext.exists()) {
                throw new StaleElementReferenceException(parentContext);
            }
            final UiObject child = parentContext.getUiObject().getChild(selector);
            if (!child.exists()) {
                throw new ElementNotFoundException(selector);
            }
            return uiObjectAdapterFactory.create(child);
        } catch (UiObjectNotFoundException ignore) {
            /* UiObject.getChild() never throws UiObjectNotFoundException. */
            throw new RuntimeException("Catch UiObjectNotFoundException while invoking UiObject" +
                    ".getChild(). This should never happen.", ignore);
        }
    }

    @NonNull
    public AndroidElement findElement(@NonNull UiObject2Adapter parentContext,
                                      @NonNull final UiSelector selector) throws
            ElementNotFoundException, StaleElementReferenceException, NoSuchDriverException {

        final UiObjectAdapter uiObjectAdapter = convertUiObject2ToUiObject(parentContext);
        return findElement(uiObjectAdapter, selector);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiSelector selector) throws
            ElementNotFoundException {
        final List<AndroidElement> elements = new ArrayList<>();
        final String selectorString = selector.toString();
        Logger.debug("findElements selector:" + selectorString);

        /*
            If sel is UiSelector[CLASS=android.widget.Button, INSTANCE=0]
            then invoking instance with a non-0 argument will corrupt the selector.

            sel.instance(1) will transform the selector into:
            UiSelector[CLASS=android.widget.Button, INSTANCE=1]

            The selector now points to an entirely different element.
        */
        final boolean endsWithInstance = UI_SELECTOR_ENDS_WITH_INSTANCE
                .matcher(selectorString).matches();
        if (endsWithInstance) {
            Logger.debug("Selector ends with instance.");
            // There's exactly one element when using instance.
            try {
                final UiObjectAdapter instanceObj = (UiObjectAdapter) findElement(selector);
                elements.add(instanceObj);
            } catch (ElementNotFoundException ignore) {
            }
            return elements;
        }
        UiObjectAdapter lastFoundObj;
        UiSelector tmp;
        int counter = 0;
        final boolean useIndex = selectorString.contains("CLASS_REGEX=");
        do {
            if (useIndex) {
                Logger.debug("Using index...");
                tmp = selector.index(counter);
            } else {
                Logger.debug("Using instance...");
                tmp = selector.instance(counter);
            }
            Logger.debug("findElements tmp selector:" + tmp.toString());
            try {
                lastFoundObj = (UiObjectAdapter) findElement(tmp);
                elements.add(lastFoundObj);
            } catch (ElementNotFoundException ignore) {
                lastFoundObj = null;
            }
            counter++;
        } while (lastFoundObj != null);
        return elements;
    }


    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiObjectAdapter parentElement,
                                             @NonNull final UiSelector selector) throws
            ElementNotFoundException, StaleElementReferenceException {
        if (!parentElement.exists()) {
            throw new StaleElementReferenceException(parentElement);
        }
        final String selectorString = selector.toString();
        Logger.debug("getElements selector:" + selectorString);
        final ArrayList<AndroidElement> elements = new ArrayList<>();

        /*
            If sel is UiSelector[CLASS=android.widget.Button, INSTANCE=0]
            then invoking instance with a non-0 argument will corrupt the selector.
            sel.instance(1) will transform the selector into:
            UiSelector[CLASS=android.widget.Button, INSTANCE=1]
            The selector now points to an entirely different element.
         */
        final boolean endsWithInstance = UI_SELECTOR_ENDS_WITH_INSTANCE
                .matcher(selectorString).matches();
        if (endsWithInstance) {
            return findElements(selector);
        }

        UiObjectAdapter lastFoundObj;
        int counter = 0;
        do {
            Logger.debug("Element is " + parentElement + ", counter: " + counter);
            try {
                lastFoundObj = (UiObjectAdapter) findElement(parentElement,selector.instance(counter));
                elements.add(lastFoundObj);
            } catch (ElementNotFoundException ignore) {
                lastFoundObj = null;
            }
            counter++;
        } while (lastFoundObj != null);
        return elements;
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiObject2Adapter parentContext,
                                             @NonNull final UiSelector selector) throws
            NoSuchDriverException, StaleElementReferenceException, ElementNotFoundException {
        UiObjectAdapter uiObjectAdapter = convertUiObject2ToUiObject(parentContext);
        return findElements(uiObjectAdapter, selector);
    }

    /**
     * We can't find the child elements with UiSelector on UiObject2,
     * as an alternative creating UiObject with UiObject2's AccessibilityNodeInfo
     * and finding the child elements on UiObject.
     */
    @NonNull
    private UiObjectAdapter convertUiObject2ToUiObject(
            @NonNull final UiObject2Adapter uiObject2Adapter) throws StaleElementReferenceException, NoSuchDriverException {
        final AccessibilityNodeInfo nodeInfo = uiObject2Adapter.getAccessibilityNodeInfo();
        final UiSelector uiSelector = new AccessibilityNodeInfo2UiSelector().generate(nodeInfo);
        try {
            return (UiObjectAdapter) findElement(uiSelector);
        } catch (ElementNotFoundException e) {
            throw new UiAutomator2Exception(String.format(ERR_MSG_CONVERT_UIOBJECT2_TO_UIOBJECT,
                    uiObject2Adapter.getId(), uiSelector));
        }
    }
}
