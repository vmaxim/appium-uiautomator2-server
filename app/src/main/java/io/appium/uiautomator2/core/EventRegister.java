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
package io.appium.uiautomator2.core;


import android.app.UiAutomation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.Configurator;
import android.view.accessibility.AccessibilityEvent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.model.dto.AccessibilityScrollData;
import io.appium.uiautomator2.model.session.Session;
import io.appium.uiautomator2.utils.Logger;

public class EventRegister {

    @NonNull
    private final UiAutomation uiAutomation;

    @Inject
    public EventRegister(@NonNull final UiAutomation uiAutomation) {
        this.uiAutomation = uiAutomation;
    }

    @Nullable
    private Boolean runAndRegisterScrollEvents(@NonNull ReturningRunnable<Boolean> runnable, long
            timeout) {
        final UiAutomation.AccessibilityEventFilter eventFilter = new UiAutomation
                .AccessibilityEventFilter() {
            @Override
            public boolean accept(@NonNull AccessibilityEvent event) {
                return event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED;
            }
        };

        AccessibilityEvent event = null;
        try {
            event = uiAutomation.executeAndWaitForEvent(runnable,
                    eventFilter, timeout);
            Logger.debug("Retrieved accessibility event for scroll");
        } catch (TimeoutException ignore) {
            Logger.error("Expected to receive a scroll accessibility event but hit the timeout " +
                    "instead");
        }

        final Session session = App.session.getSession();

        if (event == null) {
            session.setLastScrollData(null);
        } else {
            session.setLastScrollData(new AccessibilityScrollData(event));
        }
        return runnable.getResult();
    }

    @Nullable
    public Boolean runAndRegisterScrollEvents(@NonNull ReturningRunnable<Boolean> runnable) {
        return runAndRegisterScrollEvents(runnable, Configurator.getInstance()
                .getScrollAcknowledgmentTimeout());
    }
}
