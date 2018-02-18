package io.appium.uiautomator2.core;


import android.app.UiAutomation;
import android.support.test.uiautomator.Configurator;
import android.view.accessibility.AccessibilityEvent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import io.appium.uiautomator2.model.AccessibilityScrollData;
import io.appium.uiautomator2.model.AppiumUiAutomatorDriver;
import io.appium.uiautomator2.model.Session;
import io.appium.uiautomator2.utils.Logger;

public class EventRegister {

    private UiAutomation uiAutomation;

    @Inject
    public EventRegister(UiAutomation uiAutomation) {
        this.uiAutomation = uiAutomation;
    }

    public Boolean runAndRegisterScrollEvents(ReturningRunnable<Boolean> runnable, long timeout) {
        UiAutomation.AccessibilityEventFilter eventFilter = new UiAutomation
                .AccessibilityEventFilter() {
            @Override
            public boolean accept(AccessibilityEvent event) {
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

        Session session = AppiumUiAutomatorDriver.getInstance().getSession();

        if (event == null) {
            session.setLastScrollData(null);
        } else {
            session.setLastScrollData(new AccessibilityScrollData(event));
        }
        return runnable.getResult();
    }

    public Boolean runAndRegisterScrollEvents(ReturningRunnable<Boolean> runnable) {
        return runAndRegisterScrollEvents(runnable, Configurator.getInstance()
                .getScrollAcknowledgmentTimeout());
    }
}
