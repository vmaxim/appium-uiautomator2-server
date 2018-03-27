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
package io.appium.uiautomator2.model.finder;

import android.support.annotation.NonNull;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.InvalidSelectorException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.StaleElementReferenceException;
import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.model.uiobject.UiObject2Adapter;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapter;

public interface ElementFinder<T> {

    AndroidElement findElement(@NonNull T t) throws ElementNotFoundException, InvalidSelectorException, NoSuchDriverException, ParserConfigurationException, XPathExpressionException;

    AndroidElement findElement(@NonNull UiObject2Adapter parentElement, @NonNull T t) throws
            ElementNotFoundException, StaleElementReferenceException, NoSuchDriverException;

    AndroidElement findElement(@NonNull UiObjectAdapter parentElement, @NonNull T t) throws
            StaleElementReferenceException, ElementNotFoundException;

    List<AndroidElement> findElements(@NonNull T t) throws ElementNotFoundException;

    List<AndroidElement> findElements(@NonNull UiObject2Adapter parentElement, @NonNull T t)
            throws NoSuchDriverException, StaleElementReferenceException;

    List<AndroidElement> findElements(@NonNull UiObjectAdapter parentElement, @NonNull T t)
            throws StaleElementReferenceException;

}
