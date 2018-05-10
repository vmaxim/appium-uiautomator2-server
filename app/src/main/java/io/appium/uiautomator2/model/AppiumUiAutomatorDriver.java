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

package io.appium.uiautomator2.model;


import android.support.annotation.Nullable;

import java.util.Map;
import java.util.UUID;

import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.utils.Logger;


public class AppiumUiAutomatorDriver {

    private static AppiumUiAutomatorDriver instance;
    private Session session;

    private AppiumUiAutomatorDriver() {
    }

    public static synchronized AppiumUiAutomatorDriver getInstance() {
        if (instance == null) {
            instance = new AppiumUiAutomatorDriver();
        }
        return instance;
    }

    public String initializeSession(Map<String, Object> capabilities) {
        if (session != null) {
            Logger.info(String.format("Terminate active session '%s'", session.getSessionId()));
            deleteSession();
        }
        session = new Session(UUID.randomUUID().toString(), capabilities);
        ServerInstrumentation.getInstance().startServer();
        NotificationListener.getInstance().start();
        return session.getSessionId();
    }

    @Nullable
    public Session getSession() {
        return session;
    }

    public void deleteSession() {
        NotificationListener.getInstance().stop();
        ServerInstrumentation.getInstance().stopServer();
        session = null;
    }
}

