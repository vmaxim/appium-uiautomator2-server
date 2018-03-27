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
package io.appium.uiautomator2.model.uiobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.core.UiDeviceAdapter;
import io.appium.uiautomator2.model.AccessibilityNodeInfoList;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.impl.UiObject2AdapterImpl;
import io.appium.uiautomator2.model.uiobject.impl.UiObjectAdapterImpl;
import io.appium.uiautomator2.model.uiobject.proxy.UiObject2AdapterProxy;
import io.appium.uiautomator2.model.uiobject.proxy.UiObjectAdapterProxy;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.App.core;

public class UiObjectAdapterFactory {

    private static final String ERR_MSG_CREATE_UIOBJECT2 = "Error while creating UiObject2 object";

    private final UiDeviceAdapter uiDeviceAdapter;

    @Inject
    public UiObjectAdapterFactory(@NonNull UiDeviceAdapter uiDeviceAdapter){
        this.uiDeviceAdapter = uiDeviceAdapter;

    }

    public UiObjectAdapter create(UiObject object) {
        return UiObjectAdapterProxy.newInstance(new UiObjectAdapterImpl(object, core.getReflectionUtils()));
    }

    public UiObject2Adapter create(UiObject2 object) {
        return UiObject2AdapterProxy.newInstance(new UiObject2AdapterImpl(object, core.getReflectionUtils()));
    }

    public UiObject2Adapter create(BySelector selector, AccessibilityNodeInfo accessibilityNodeInfo) {
        return create(createUiObject2Element(selector, accessibilityNodeInfo));
    }

    public UiObject2Adapter create(AccessibilityNodeInfo accessibilityNodeInfo) {
        BySelector selector = By.clazz(accessibilityNodeInfo.getClassName().toString());
        return create(createUiObject2Element(selector, accessibilityNodeInfo));
    }

    public List<AndroidElement> create(AccessibilityNodeInfoList nodeInfoList) {
        List<AndroidElement> result = new ArrayList<>(nodeInfoList.size());
        for (AccessibilityNodeInfo node : nodeInfoList) {
            result.add(create(node));
        }
        return result;
    }

    @NonNull
    private UiObject2 createUiObject2Element(@Nullable final BySelector selector,
                                                    @NonNull final AccessibilityNodeInfo node) {
        final Constructor cons = UiObject2.class.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        final Object[] constructorParams = {uiDeviceAdapter.getUiDevice(), selector, node};
        try {
            final UiObject2 uiObject2 = (UiObject2) cons.newInstance(constructorParams);
            return uiObject2;
        } catch (@NonNull InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            Logger.error(ERR_MSG_CREATE_UIOBJECT2 + " " + e);
            throw new UiAutomator2Exception(ERR_MSG_CREATE_UIOBJECT2, e);
        }
    }
}
