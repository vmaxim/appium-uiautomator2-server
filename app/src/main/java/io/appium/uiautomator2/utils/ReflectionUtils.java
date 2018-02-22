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

@SuppressWarnings("unchecked")
public class ReflectionUtils {

    private static final String ERR_MSG_UNABLE_TO_FIND_CLASS = "unable to find class %s";
    private static final String ERR_MSG_UNABLE_GET_FIELD = "error while getting field %s from " +
            "object %s";
    private static final String ERR_MSG_FIELD_DOES_NOT_EXIST = "Field %s does not exist in object" +
            " %s";
    private static final String ERR_MSG_INVOCATION_ERROR = "error while invoking method %s on " +
            "object %s with parameters %s";
    private static final String ERR_MSG_UNABLE_TO_GET_METHOD = "error while getting method %s " +
            "from class %s with parameter types %s";

    private Object targetObject;
    private Class targetClass;

    public void setTargetObject(@NonNull final Object targetObject) {
        this.targetObject = targetObject;
        this.targetClass = targetObject.getClass();
    }

    public void setTargetClass(@NonNull final String targetClassName) {
        this.targetClass = getClass(targetClassName);
    }

    private Class getClass(@NonNull final String name) throws UiAutomator2Exception {
        try {
            return Class.forName(name);
        } catch (@NonNull final ClassNotFoundException e) {
            final String msg = String.format(ERR_MSG_UNABLE_TO_FIND_CLASS, name);
            throw new UiAutomator2Exception(msg, e);
        }
    }

    @NonNull
    public <T> T getField(@NonNull final String fieldName) throws UiAutomator2Exception {
        assert targetObject != null;
        for (Field field : getAllFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                try {
                    return (T) field.get(targetObject);
                } catch (IllegalAccessException e) {
                    final String msg = String.format(ERR_MSG_UNABLE_GET_FIELD,
                            fieldName, targetObject);
                    Logger.error(msg + " " + e.getMessage());
                    throw new UiAutomator2Exception(msg, e);
                }
            }
        }
        throw new UiAutomator2Exception(String.format(ERR_MSG_FIELD_DOES_NOT_EXIST,
                fieldName, targetObject));
    }

    @NonNull
    private List<Field> getAllFields() {
        List<Field> allFields = new ArrayList<>();
        for (Class<?> c = targetClass; c != null; c = c.getSuperclass()) {
            allFields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return allFields;
    }

    @NonNull
    public <T> T
    invoke(@NonNull final Method method, final Object... parameters) throws
            UiAutomator2Exception {
        assert targetObject != null;
        try {
            return (T) method.invoke(targetObject, parameters);
        } catch (final Exception e) {
            final String msg = String.format(ERR_MSG_INVOCATION_ERROR,
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
            final String msg = String.format(ERR_MSG_UNABLE_TO_GET_METHOD,
                    methodName, targetClass, Arrays.toString(parameterTypes));
            Logger.error(msg + " " + e.getMessage());
            throw new UiAutomator2Exception(msg, e);
        }
    }

}
