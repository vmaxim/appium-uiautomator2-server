package io.appium.uiautomator2.model;

import android.app.UiAutomation;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.utils.Logger;

import static java.lang.System.currentTimeMillis;


public final class NotificationListener {
    private final static NotificationListener INSTANCE = new NotificationListener();
    private static List<CharSequence> toastMessages = new ArrayList<CharSequence>();
    private final int TOAST_CLEAR_TIMEOUT = 3500;
    private final int WAIT_FOR_EVENT_TIMEOUT = 500;
    private Listener listener;

    private NotificationListener(){
    }

    public static NotificationListener getInstance(){
        return INSTANCE;
    }

    public static List<CharSequence> getToastMSGs() {
        return toastMessages;
    }

    /**
     * Listens for Notification Messages
     */
    public void start(){
        Logger.debug("Starting toast notification listener.");
        if (listener != null && listener.isAlive()) {
            Logger.debug("Toast notification listener is already started.");
            return;
        }
        listener = new Listener();
        // listener.start();
    }

    public void stop(){
        Logger.debug("Stopping toast notification listener.");
        if (listener == null || !listener.isAlive()) {
            Logger.debug("Toast notification listener is already stopped.");
            return;
        }
        listener.stopLooping();
        try {
            listener.join(WAIT_FOR_EVENT_TIMEOUT);
        } catch (InterruptedException ignore) {
        }
    }

    private class Listener extends Thread{

        //return true if the AccessibilityEvent type is NOTIFICATION type
        private final UiAutomation.AccessibilityEventFilter eventFilter = new UiAutomation.AccessibilityEventFilter() {
            @Override
            public boolean accept(AccessibilityEvent event) {
                return event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
            }
        };
        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Not performing any event.
            }
        };
        private boolean stopLooping = false;
        private long previousTime = currentTimeMillis();

        public void run() {
            while (true) {
                AccessibilityEvent accessibilityEvent = null;
                toastMessages = init();

                try {
                    //wait for AccessibilityEvent filter
                    accessibilityEvent = App.core.getUiAutomatorBridge().getUiAutomation()
                            .executeAndWaitForEvent(runnable /*executable event*/, eventFilter /* event to filter*/, WAIT_FOR_EVENT_TIMEOUT /*time out in ms*/);
                } catch (Exception ignore) {}

                if (accessibilityEvent != null) {
                    toastMessages = accessibilityEvent.getText();
                    previousTime = currentTimeMillis();
                }
                if(stopLooping){
                    break;
                }
            }
        }

        public List<CharSequence> init(){
            if( currentTimeMillis() - previousTime  > TOAST_CLEAR_TIMEOUT) {
                return new ArrayList<CharSequence>();
            }
            return toastMessages;
        }

        public void stopLooping() {
            stopLooping = true;
        }
    }

}
