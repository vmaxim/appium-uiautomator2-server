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

package io.appium.uiautomator2.model.settings;

import io.appium.uiautomator2.model.AppiumUiAutomatorDriver;
import io.appium.uiautomator2.model.Session;
import io.appium.uiautomator2.utils.Logger;

public class AllowInvisibleElements extends AbstractSetting<Boolean> {

    private static final String SETTING_NAME = "allowInvisibleElements";

    public AllowInvisibleElements() {
        super(Boolean.class, SETTING_NAME);
    }

    @Override
    public Boolean getValue() {
        final Session session = AppiumUiAutomatorDriver.getInstance().getSession();
        final Object allowInvisibleElements = session.getCapabilities().get(getName());
        return allowInvisibleElements != null && (boolean) allowInvisibleElements;
    }

    @Override
    protected void apply(Boolean allowInvisibleElements) {
        Logger.debug("Dummy setting.");
    }

}
