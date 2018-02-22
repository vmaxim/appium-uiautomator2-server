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
import android.support.annotation.Nullable;

import java.util.regex.Pattern;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.model.ByStrategy.SELECTOR_NATIVE_ID;

/**
 * Mechanism used to locate elements within a document. In order to create your own locating
 * mechanisms, it is possible to subclass this class and override the protected methods as
 * required.
 */
public class By {

    private static final String ERR_MSG_UNSUPPORTED_LOCATOR = "By locator %s is currently not supported!";
    static final Pattern RESOURCE_ID_REGEX = Pattern
            .compile("^[a-zA-Z_][a-zA-Z0-9\\._]*:[^\\/]+\\/[\\S]+$");

    private String locator;
    private final ByStrategy strategy;

    public By(@NonNull final ByStrategy strategy, @NonNull final String locator) {
        this.strategy = strategy;
        this.locator = locator;
    }

    public By(@NonNull final String strategyName, @NonNull final String locator) {
        strategy = ByStrategy.get(strategyName);
        if (strategy == null) {
            String msg = String.format(ERR_MSG_UNSUPPORTED_LOCATOR, strategyName);
            throw new UnsupportedOperationException(msg);
        }
        if (strategy == SELECTOR_NATIVE_ID) {
            addAppPackage();
        }
        this.locator = locator;
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

    private void addAppPackage() {
        String locatorWithAppPackage = getElementLocator();
        if (!RESOURCE_ID_REGEX.matcher(getElementLocator()).matches()) {
            // not a fully qualified resource id
            // transform "textToBeChanged" into:
            // com.example.android.testing.espresso.BasicSample:id/textToBeChanged
            // it's prefixed with the app package.
            String appPackage = (String) App.session.getSession().capabilities.get("appPackage");
            locatorWithAppPackage =  appPackage + ":id/" + getElementLocator();
            Logger.debug("Updated findElement locator strategy: " + locatorWithAppPackage);
        }
        locator = locatorWithAppPackage;
    }

}
