package io.appium.uiautomator2.model.uiobject.proxy;

import android.support.test.uiautomator.StaleObjectException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;

/**
 * Convert {@link StaleObjectException} to {@link StaleElementReferenceException}.
 */
public class UiObject2AdapterProxy implements InvocationHandler {

    private final UiObject2Adapter uiObject2Adapter;

    public static UiObject2Adapter newInstance(UiObject2Adapter object) {
        return (UiObject2Adapter) java.lang.reflect.Proxy.newProxyInstance(
                object.getClass().getClassLoader(),
                object.getClass().getInterfaces(),
                new UiObject2AdapterProxy(object));
    }

    private UiObject2AdapterProxy(UiObject2Adapter uiObject2Adapter) {
        this.uiObject2Adapter = uiObject2Adapter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(uiObject2Adapter, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof StaleObjectException) {
                throw new StaleElementReferenceException(uiObject2Adapter);
            }
            throw e.getCause();
        }
    }
}
