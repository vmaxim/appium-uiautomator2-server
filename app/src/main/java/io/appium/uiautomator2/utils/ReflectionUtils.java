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

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;

public class ReflectionUtils {

    private Object targetObject;
    private Class targetClass;

    public void setTargetObject(@NonNull final Object targetObject) {
        this.targetObject = targetObject;
        this.targetClass = targetObject.getClass();
    }

    public @NonNull
    Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(@NonNull final String targetClassName) {
        this.targetClass = getClass(targetClassName);
    }

    public void setTargetClass(@NonNull final Class targetClass) {
        this.targetClass = targetClass;
    }

    private @NonNull
    Class getClass(final String name) throws UiAutomator2Exception {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException e) {
            final String msg = String.format("unable to find class %s", name);
            throw new UiAutomator2Exception(msg, e);
        }
    }

    public <T> T getField(@NonNull final String fieldName) throws UiAutomator2Exception {
        assert targetObject != null;

        for (Field field : getAllFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                try {
                    return (T) field.get(targetObject);
                } catch (IllegalAccessException e) {
                    final String msg = String.format("error while getting field %s from object %s",
                            fieldName, targetObject);
                    Logger.error(msg + " " + e.getMessage());
                    throw new UiAutomator2Exception(msg, e);
                }
            }
        }
        throw new UiAutomator2Exception(String.format("Field %s does not exists in object %",
                fieldName, targetObject));
    }

    private List<Field> getAllFields() {
        List<Field> allFields = new ArrayList<>();
        for (Class<?> c = targetClass; c != null; c = c.getSuperclass()) {
            allFields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return allFields;
    }

    public <T> T
    invoke(@NonNull final Method method, final Object... parameters) throws
            UiAutomator2Exception {
        assert targetObject != null;
        try {
            return (T) method.invoke(targetObject, parameters);
        } catch (final Exception e) {
            final String msg = String.format("error while invoking method %s on object %s with " +
                            "parameters %s",
                    method, targetObject, Arrays.toString(parameters));
            Logger.error(msg + " " + e.getMessage());
            throw new UiAutomator2Exception(msg, e);
        }
    }

    public Method method(@NonNull final String methodName, final Class... parameterTypes) throws
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
