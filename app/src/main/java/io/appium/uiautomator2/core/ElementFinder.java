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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapterFactory;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoList;
import io.appium.uiautomator2.utils.Logger;

public class ElementFinder {

    private static final Pattern UI_SELECTOR_ENDS_WITH_INSTANCE = Pattern.compile("" +
            ".*INSTANCE=\\d+]$");
    private static final String ERR_MSG_CREATE_UIOBJECT2 = "error while creating  UiObject2 object";

    private final ByMatcherAdapter byMatcherAdapter;
    private final UiDeviceAdapter uiDeviceAdapter;
    private final UiObjectAdapterFactory uiObjectAdapterFactory;

    @Inject
    public ElementFinder(@NonNull final UiDeviceAdapter uiDeviceAdapter,
                         @NonNull final ByMatcherAdapter byMatcherAdapter,
                         @NonNull final UiObjectAdapterFactory uiObjectAdapterFactory) {
        this.uiDeviceAdapter = uiDeviceAdapter;
        this.byMatcherAdapter = byMatcherAdapter;
        this.uiObjectAdapterFactory = uiObjectAdapterFactory;
    }

    @Nullable
    public AndroidElement findElement(@NonNull final UiSelector selector) {
        final UiObject uiObject = uiDeviceAdapter.getUiDevice().findObject(selector);
        if (uiObject.exists()) {
            return uiObjectAdapterFactory.create(uiObject);
        }
        return null;
    }

    @Nullable
    public AndroidElement findElement(@NonNull final BySelector selector) {
        uiDeviceAdapter.waitForIdle();
        final AccessibilityNodeInfo node = byMatcherAdapter.findMatch(selector);
        if (node == null) {
            return null;
        }
        return createUiObject2Element(selector, node);

    }

    @NonNull
    public AndroidElement findElement(@NonNull final AccessibilityNodeInfo node) {
        uiDeviceAdapter.waitForIdle();
        final BySelector selector = By.clazz(node.getClassName().toString());
        return createUiObject2Element(selector, node);
    }

    @Nullable
    public AndroidElement findElement(@NonNull final AccessibilityNodeInfoList nodeList) {
        if (nodeList.isEmpty()) {
            return null;
        }
        return findElement(nodeList.get(0));
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final BySelector selector) {
        final List<AccessibilityNodeInfo> nodeList = byMatcherAdapter.findMatches(selector);
        return createUiObject2Elements(nodeList);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiSelector selector) {
        final List<AndroidElement> elements = new ArrayList<>();
        final String selectorString = selector.toString();
        final boolean useIndex = selectorString.contains("CLASS_REGEX=");
        final boolean endsWithInstance = UI_SELECTOR_ENDS_WITH_INSTANCE.matcher(selectorString)
                .matches();
        Logger.debug("findElements selector:" + selectorString);

        // If sel is UiSelector[CLASS=android.widget.Button, INSTANCE=0]
        // then invoking instance with a non-0 argument will corrupt the selector.
        //
        // sel.instance(1) will transform the selector into:
        // UiSelector[CLASS=android.widget.Button, INSTANCE=1]
        //
        // The selector now points to an entirely different element.
        if (endsWithInstance) {
            Logger.debug("Selector ends with instance.");
            // There's exactly one element when using instance.
            final AndroidElement instanceObj = findElement(selector);
            if (instanceObj != null && ((UiObject) instanceObj).exists()) {
                elements.add(instanceObj);
            }
            return elements;
        }
        UiObjectAdapter lastFoundObj;
        UiSelector tmp;
        int counter = 0;
        do {
            if (useIndex) {
                Logger.debug("Using index...");
                tmp = selector.index(counter);
            } else {
                Logger.debug("Using instance...");
                tmp = selector.instance(counter);
            }
            Logger.debug("findElements tmp selector:" + tmp.toString());
            lastFoundObj = (UiObjectAdapter) findElement(tmp);
            counter++;
            if (lastFoundObj != null && lastFoundObj.exists()) {
                elements.add(lastFoundObj);
            }
        } while (lastFoundObj != null);
        return elements;
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final AccessibilityNodeInfoList nodeList) {
        return createUiObject2Elements(nodeList);
    }

    @NonNull
    private UiObject2Adapter createUiObject2Element(@NonNull final BySelector selector,
                                                    @NonNull final AccessibilityNodeInfo node) {
        final Constructor cons = UiObject2.class.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        final Object[] constructorParams = {uiDeviceAdapter.getUiDevice(), selector, node};
        try {
            final UiObject2 uiObject2 = (UiObject2) cons.newInstance(constructorParams);
            return uiObjectAdapterFactory.create(uiObject2);
        } catch (@NonNull InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            Logger.error(ERR_MSG_CREATE_UIOBJECT2 + " " + e);
            throw new UiAutomator2Exception(ERR_MSG_CREATE_UIOBJECT2, e);
        }
    }

    @NonNull
    private List<AndroidElement> createUiObject2Elements(
            @NonNull final List<AccessibilityNodeInfo> nodeList) {
        final List<AndroidElement> result = new ArrayList<>();
        for (final AccessibilityNodeInfo node : nodeList) {
            result.add(createUiObject2Element(By.clazz(node.getClassName().toString()), node));
        }
        return result;
    }
}
