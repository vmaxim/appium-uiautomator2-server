package io.appium.uiautomator2.model.uiobject.proxy;

import android.support.test.uiautomator.UiObjectNotFoundException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;

/**
 * UiObject does not support stale state, but we can change this behaviour
 * and throw {@link StaleElementReferenceException} if element can not be found.
 */
public class UiObjectAdapterProxy implements InvocationHandler {

    private final UiObjectAdapter uiObjectAdapter;

    public static UiObjectAdapter newInstance(UiObjectAdapter object) {
        return (UiObjectAdapter) java.lang.reflect.Proxy.newProxyInstance(
                object.getClass().getClassLoader(),
                object.getClass().getInterfaces(),
                new UiObjectAdapterProxy(object));
    }

    private UiObjectAdapterProxy(UiObjectAdapter uiObjectAdapter) {
        this.uiObjectAdapter = uiObjectAdapter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(uiObjectAdapter, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof UiObjectNotFoundException) {
                throw new StaleElementReferenceException(uiObjectAdapter);
            }
            throw e.getCause();
        }
    }
}
