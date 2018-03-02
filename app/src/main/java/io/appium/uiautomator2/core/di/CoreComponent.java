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
package io.appium.uiautomator2.core.di;

import javax.inject.Singleton;

import dagger.Component;
import io.appium.uiautomator2.core.CoreFacade;
import io.appium.uiautomator2.core.UiAutomatorBridgeAdapter;
import io.appium.uiautomator2.core.finder.BySelectorFinder;
import io.appium.uiautomator2.core.finder.UiSelectorFinder;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoHelper;
import io.appium.uiautomator2.utils.ReflectionUtils;

@Singleton
@Component(modules = {CoreModule.class})
public interface CoreComponent {

    UiAutomatorBridgeAdapter getUiAutomatorBridge();

    AccessibilityNodeInfoHelper getAccessibilityNodeInfoHelper();

    ReflectionUtils getReflectionUtils();

    CoreFacade getCoreFacade();

    UiSelectorFinder getUiObjectFinder();

    BySelectorFinder getUiObject2Finder();

}
