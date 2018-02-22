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
package io.appium.uiautomator2.model.di;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.appium.uiautomator2.core.di.CoreModule;
import io.appium.uiautomator2.model.AccessibilityNodeInfo2UiSelector;
import io.appium.uiautomator2.model.uiobject.UiObjectAdapterFactory;

@Module(includes = {CoreModule.class})
public class ModelModule {

    @Provides
    @NonNull
    @Singleton
    UiObjectAdapterFactory provideUiObjectElementFactory() {
        return new UiObjectAdapterFactory();
    }

    @Provides
    @NonNull
    @Singleton
    AccessibilityNodeInfo2UiSelector provideAccessibilityNodeInfo2UiSelector() {
        return new AccessibilityNodeInfo2UiSelector();
    }
}
