package io.appium.uiautomator2.model.finder.impl;

import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.finder.ElementFinder;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;

/**
 * Created by max on 05.03.2018.
 */

public class XPathFinder implements ElementFinder<String> {
    @Override
    public AndroidElement findElement(@NonNull String selector) throws ElementNotFoundException,
            InvalidSelectorException, NoSuchDriverException, ParserConfigurationException,
            XPathExpressionException {
        AccessibilityNodeInfo accessibilityNodeInfo = io.appium.uiautomator2.model.XPathFinder
                .getNodesList(selector, null).get(0);

        return null;
    }

    @Override
    public AndroidElement findElement(@NonNull UiObject2Adapter parentElement, @NonNull String s)
            throws ElementNotFoundException, StaleElementReferenceException, NoSuchDriverException {
        return null;
    }

    @Override
    public AndroidElement findElement(@NonNull UiObjectAdapter parentElement, @NonNull String s)
            throws StaleElementReferenceException, ElementNotFoundException {
        return null;
    }

    @Override
    public List<AndroidElement> findElements(@NonNull String s) throws ElementNotFoundException {
        return null;
    }

    @Override
    public List<AndroidElement> findElements(@NonNull UiObject2Adapter parentElement, @NonNull
            String s) throws NoSuchDriverException, StaleElementReferenceException {
        return null;
    }

    @Override
    public List<AndroidElement> findElements(@NonNull UiObjectAdapter parentElement, @NonNull
            String s) throws StaleElementReferenceException {
        return null;
    }
}
