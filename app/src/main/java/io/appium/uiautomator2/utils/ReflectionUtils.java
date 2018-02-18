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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;

public class ReflectionUtils {

    private Object targetObject;
    private Class targetClass;

    public void setTarget(Object targetObject) {
        this.targetObject = targetObject;
        this.targetClass = targetObject.getClass();
    }

    /**
     * Clears the in-process Accessibility cache, removing any stale references. Because the
     * AccessibilityInteractionClient singleton stores copies of AccessibilityNodeInfo instances,
     * calls to public APIs such as `recycle` do not guarantee cached references get updated. See
     * the android.view.accessibility AIC and ANI source code for more information.
     */
    public boolean clearAccessibilityCache() throws UiAutomator2Exception {
        boolean success = false;

//        try {
//            final Class c = Class
//                    .forName("android.view.accessibility.AccessibilityInteractionClient");
//            final Method getInstance = method(c, "getInstance");
//            final Object instance = getInstance.invoke(null);
//            final Method clearCache = method(instance.getClass(),
//                    "clearCache");
//            clearCache.invoke(instance);
//
//            success = true;
//        } catch (IllegalAccessException e) {
//            Logger.error("Failed to clear Accessibility Node cache. ", e);
//        } catch (InvocationTargetException e) {
//            Logger.error("Failed to clear Accessibility Node cache. ", e);
//        } catch (ClassNotFoundException e) {
//            Logger.error("Failed to clear Accessibility Node cache. ", e);
//        }
        return success;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClassName) {
        this.targetClass = getClass(targetClassName);
    }

    public Class getClass(final String name) throws UiAutomator2Exception {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            final String msg = String.format("unable to find class %s", name);
            throw new UiAutomator2Exception(msg, e);
        }
    }

    public <T> T getField(final String fieldName) throws UiAutomator2Exception {
        try {
            final Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);

            return (T) field.get(targetObject);
        } catch (final Exception e) {
            final String msg = String.format("error while getting field %s from object %s",
                    fieldName, targetObject);
            Logger.error(msg + " " + e.getMessage());
            throw new UiAutomator2Exception(msg, e);
        }
    }

    public Object invoke(final Method method, final Object... parameters) throws
            UiAutomator2Exception {
        try {
            return method.invoke(targetObject, parameters);
        } catch (final Exception e) {
            final String msg = String.format("error while invoking method %s on object %s with " +
                            "parameters %s",
                    method, targetObject, Arrays.toString(parameters));
            Logger.error(msg + " " + e.getMessage());
            throw new UiAutomator2Exception(msg, e);
        }
    }

    public Method method(final String methodName, final Class... parameterTypes) throws
            UiAutomator2Exception {
        try {
            final Method method = targetClass.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (final Exception e) {
            final String msg = String.format("error while getting method %s from class %s with " +
                            "parameter types %s",
                    methodName, targetClass, Arrays.toString(parameterTypes));
            Logger.error(msg + " " + e.getMessage());
            throw new UiAutomator2Exception(msg, e);
        }
    }

}
