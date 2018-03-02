/*
 * Copyright 2007-2011 Selenium committers
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.appium.uiautomator2.model;

import android.support.annotation.NonNull;
import android.support.test.uiautomator.BySelector;

import java.util.regex.Pattern;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.model.ByStrategy.SELECTOR_ACCESSIBILITY_ID;
import static io.appium.uiautomator2.model.ByStrategy.SELECTOR_ANDROID_UIAUTOMATOR;
import static io.appium.uiautomator2.model.ByStrategy.SELECTOR_CLASS;
import static io.appium.uiautomator2.model.ByStrategy.SELECTOR_NATIVE_ID;
import static io.appium.uiautomator2.model.ByStrategy.SELECTOR_XPATH;

/**
 * Mechanism used to locate elements within a document. In order to create your own locating
 * mechanisms, it is possible to subclass this class and override the protected methods as
 * required.
 */
public class By {

    private static final Pattern RESOURCE_ID_REGEX = Pattern
            .compile("^[a-zA-Z_][a-zA-Z0-9\\._]*:[^\\/]+\\/[\\S]+$");
    private static final String ERR_MSG_CONVERT_TO_UIAUTO_BY = "Can not convert %s to android.support.test.uiautomator.BySelector";

    private String locator;
    private final ByStrategy strategy;

    public By(@NonNull final ByStrategy strategy, @NonNull final String locator)  {
        this.strategy = strategy;
        this.locator = locator;
        if (strategy == SELECTOR_NATIVE_ID) {
            addAppPackage();
        }
    }

    public By(@NonNull final String strategyName, @NonNull final String locator) throws InvalidSelectorException {
        this(ByStrategy.get(strategyName), locator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        By by = (By) o;
        return locator.equals(by.locator) && strategy.equals(by.strategy);
    }

    public String getElementLocator() {
        return locator;
    }

    public ByStrategy getElementStrategy() {
        return strategy;
    }

    @Override
    public String toString() {
        return strategy.getDescription() + ":" + locator;
    }

    public BySelector toBySelector() {
        switch (strategy) {
            case SELECTOR_NATIVE_ID:
                return android.support.test.uiautomator.By.res(locator);
            case SELECTOR_ACCESSIBILITY_ID:
                return android.support.test.uiautomator.By.desc(locator);
            case SELECTOR_CLASS:
                return android.support.test.uiautomator.By.clazz(locator);
            default:
                throw new UiAutomator2Exception(String.format(ERR_MSG_CONVERT_TO_UIAUTO_BY, this));
        }
    }

    private void addAppPackage()  {
        String appPackage = null;
        try {
            appPackage = (String) App.getSession().getCapabilities().get("appPackage");
        } catch (NoSuchDriverException e) {
            Logger.error("Uable to get app package. Session does not started.");
        }
        if (appPackage == null) {
            return;
        }
        String locatorWithAppPackage = getElementLocator();
        if (!RESOURCE_ID_REGEX.matcher(getElementLocator()).matches()) {
            // not a fully qualified resource id
            // transform "textToBeChanged" into:
            // com.example.android.testing.espresso.BasicSample:id/textToBeChanged
            // it's prefixed with the app package.
            locatorWithAppPackage =  appPackage + ":id/" + getElementLocator();
            Logger.debug("Updated findElement locator strategy: " + locatorWithAppPackage);
        }
        locator = locatorWithAppPackage;
    }

    public static By accessibilityId(@NonNull final String id) {
        return new By(SELECTOR_ACCESSIBILITY_ID, id);
    }

    public static By xpath(@NonNull final String xpath) {
        return new By(SELECTOR_XPATH, xpath);
    }

    public static By id(@NonNull final String id) {
        return new By(SELECTOR_NATIVE_ID, id);
    }

    public static By androidUiAutomator(@NonNull final String selector) {
        return new By(SELECTOR_ANDROID_UIAUTOMATOR, selector);
    }

    public static By className(@NonNull final String className) {
        return new By(SELECTOR_CLASS, className);
    }
}
