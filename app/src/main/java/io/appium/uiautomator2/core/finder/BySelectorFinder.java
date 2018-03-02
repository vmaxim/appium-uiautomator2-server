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
import android.support.annotation.Nullable;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.core.ByMatcherAdapter;
import io.appium.uiautomator2.core.UiDeviceAdapter;
import io.appium.uiautomator2.model.AccessibilityNodeInfoList;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapterFactory;
import io.appium.uiautomator2.utils.Logger;

/**
 * The helper class for finding {@link AndroidElement} by {@link BySelector} or
 * {@link AccessibilityNodeInfo}.<br>
 * Each method of this class throws
 * {@link io.appium.uiautomator2.common.exceptions.ElementNotFoundException}
 * if element can not be found.
 */
public class BySelectorFinder {

    private static final String ERR_MSG_CREATE_UIOBJECT2 = "Error while creating UiObject2 object";
    private static final String ERR_MSG_CONVERT_UIOBJECT_TO_UIOBJECT2 = "Unable to convert UiObject to " +
            "UiObject2. UiObject id:%s. AccessibilityNodeInfo:%s";
    private final UiDeviceAdapter uiDeviceAdapter;
    private final ByMatcherAdapter byMatcherAdapter;
    private final UiObjectAdapterFactory uiObjectAdapterFactory;

    @Inject
    public BySelectorFinder(@NonNull UiDeviceAdapter uiDeviceAdapter,
                            @NonNull ByMatcherAdapter byMatcherAdapter,
                            @NonNull UiObjectAdapterFactory uiObjectAdapterFactory) {
        this.uiDeviceAdapter = uiDeviceAdapter;
        this.byMatcherAdapter = byMatcherAdapter;
        this.uiObjectAdapterFactory = uiObjectAdapterFactory;
    }

    @NonNull
    public AndroidElement findElement(@NonNull final BySelector selector)
            throws ElementNotFoundException {
        uiDeviceAdapter.waitForIdle();
        /* ByMatcherAdapter.findMatch() returns null if element is not exist */
        final AccessibilityNodeInfo node = byMatcherAdapter.findMatch(selector);
        if (node == null) {
            throw new ElementNotFoundException(selector);
        }
        return createUiObject2Element(selector, node);
    }

    @NonNull
    public AndroidElement findElement(@NonNull final UiObject2Adapter parentContext, @NonNull
    final BySelector selector) throws ElementNotFoundException {
        UiObject2 uiObject2 = parentContext.getUiObject().findObject(selector);
        /* UiObject2.findObject() returns null if element is not exist */
        if (uiObject2 == null) {
            throw new ElementNotFoundException(selector);
        }
        return uiObjectAdapterFactory.create(uiObject2);
    }

    @NonNull
    public AndroidElement findElement(@NonNull final UiObjectAdapter parentContext, @NonNull
    final BySelector selector) throws StaleElementReferenceException, ElementNotFoundException {
        UiObject2Adapter uiObject2Adapter = convertUiObjectToUiObject2(parentContext);
        return findElement(uiObject2Adapter, selector);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final BySelector selector) {
        final List<AccessibilityNodeInfo> nodeList = byMatcherAdapter.findMatches(selector);
        return createUiObject2Elements(nodeList);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiObject2Adapter parentContext,
                                             @NonNull final BySelector selector) {
        final List<UiObject2> uiObject2List = parentContext.getUiObject().findObjects(selector);
        List<AndroidElement> result = new ArrayList<>(uiObject2List.size());
        for (UiObject2 uiObject2 : uiObject2List) {
            UiObject2Adapter androidElement = uiObjectAdapterFactory.create(uiObject2);
            result.add(androidElement);
        }
        return result;
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiObjectAdapter parentContext,
                                             @NonNull final BySelector selector) throws
            StaleElementReferenceException, ElementNotFoundException {
        UiObject2Adapter uiObject2Adapter = convertUiObjectToUiObject2(parentContext);
        return findElements(uiObject2Adapter, selector);
    }

    @NonNull
    public UiObject2Adapter findElement(@Nullable final AccessibilityNodeInfo node) throws ElementNotFoundException {
        if (node == null) {
            throw new ElementNotFoundException();
        }
        final BySelector selector = By.clazz(node.getClassName().toString());
        return createUiObject2Element(selector, node);
    }

    @NonNull
    public UiObject2Adapter findElement(@NonNull final AccessibilityNodeInfoList nodeList) throws ElementNotFoundException {
        if (nodeList.isEmpty()) {
            throw new ElementNotFoundException(nodeList);
        }
        return findElement(nodeList.get(0));
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final AccessibilityNodeInfoList nodeList) {
        return createUiObject2Elements(nodeList);
    }

    @NonNull
    private UiObject2Adapter createUiObject2Element(@Nullable final BySelector selector,
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

    /**
     * We can't find the child element with BySelector on UiObject,
     * as an alternative creating UiObject2 with UiObject's AccessibilityNodeInfo
     * and finding the child element on UiObject2.
     */
    @NonNull
    private UiObject2Adapter convertUiObjectToUiObject2(
            @NonNull final UiObjectAdapter uiObjectAdapter) throws StaleElementReferenceException {
        final AccessibilityNodeInfo nodeInfo = uiObjectAdapter.getAccessibilityNodeInfo();
        try {
            return findElement(nodeInfo);
        } catch (ElementNotFoundException e) {
            throw new UiAutomator2Exception(String.format(ERR_MSG_CONVERT_UIOBJECT_TO_UIOBJECT2,
                    uiObjectAdapter.getId(), nodeInfo));
        }
    }
}
