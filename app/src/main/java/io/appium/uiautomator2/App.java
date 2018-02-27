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
package io.appium.uiautomator2;


import android.support.annotation.Nullable;

import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.core.di.CoreComponent;
import io.appium.uiautomator2.core.di.DaggerCoreComponent;
import io.appium.uiautomator2.model.di.DaggerModelComponent;
import io.appium.uiautomator2.model.di.ModelComponent;
import io.appium.uiautomator2.model.session.Session;
import io.appium.uiautomator2.model.session.di.SessionComponent;

public class App {

    public static CoreComponent core = DaggerCoreComponent.create();
    public static ModelComponent model = DaggerModelComponent.create();
    @Nullable
    private static SessionComponent session;

    public static Session getSession() throws NoSuchDriverException {
        if (session == null) {
            throw new NoSuchDriverException();
        }
        return session.getSession();
    }

    public static synchronized Session startSession() {
        session = model.initSession();
        return session.getSession();
    }

    public static synchronized void deleteSession() {
        if (session == null) {
            throw new NoSuchDriverException();
        }
        session = null;
    }
}
