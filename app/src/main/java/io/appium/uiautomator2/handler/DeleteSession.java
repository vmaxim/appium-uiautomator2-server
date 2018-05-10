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

package io.appium.uiautomator2.handler;

import io.appium.uiautomator2.common.exceptions.SessionRemovedException;
import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.model.AppiumUiAutomatorDriver;
import io.appium.uiautomator2.model.Session;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

public class DeleteSession extends SafeRequestHandler {

    public DeleteSession(String mappedUri) {
        super(mappedUri);
    }

    @Override
    protected AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Delete session command");
        SessionRemovedException sessionRemovedException =
                new SessionRemovedException(WDStatus.NO_SUCH_DRIVER.message());

        final Session currentSession = AppiumUiAutomatorDriver.getInstance().getSession();
        final String sessionId = getSessionId(request);
        if (currentSession == null || !currentSession.getSessionId().equals(sessionId)) {
            throw sessionRemovedException;
        }

        AppiumUiAutomatorDriver.getInstance().deleteSession();
        return new AppiumResponse(sessionId, WDStatus.SUCCESS, "Session deleted");
    }
}
