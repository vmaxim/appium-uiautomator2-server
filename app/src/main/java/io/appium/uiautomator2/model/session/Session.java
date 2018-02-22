package io.appium.uiautomator2.model.session;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.appium.uiautomator2.model.dto.AccessibilityScrollData;
import io.appium.uiautomator2.model.NotificationListener;

public class Session {
    private final String SEND_KEYS_TO_ELEMENT = "sendKeysToElement";
    private String sessionId;
    private ConcurrentMap<String, JSONObject> commandConfiguration;
    private AccessibilityScrollData lastScrollData;
    public  Map<String, Object> capabilities = new HashMap<>();

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
        this.commandConfiguration = new ConcurrentHashMap<>();
        JSONObject configJsonObject = new JSONObject();
        this.commandConfiguration.put(SEND_KEYS_TO_ELEMENT, configJsonObject);
        NotificationListener.getInstance().start();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setCommandConfiguration(String command, JSONObject config) {
        if (commandConfiguration.containsKey(command)) {
            commandConfiguration.replace(command, config);
        }
    }

//    public CachedElements getCachedElements() {
//        return cachedElements;
//    }

    public JSONObject getCommandConfiguration(String command) {
        return commandConfiguration.get(command);
    }

    public void setLastScrollData(AccessibilityScrollData scrollData) {
        lastScrollData = scrollData;
    }

    public AccessibilityScrollData getLastScrollData() {
        return lastScrollData;
    }
}
