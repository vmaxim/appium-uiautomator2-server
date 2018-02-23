package io.appium.uiautomator2.model.session;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.appium.uiautomator2.model.NotificationListener;
import io.appium.uiautomator2.model.dto.AccessibilityScrollData;

public class Session {
    private final String SEND_KEYS_TO_ELEMENT = "sendKeysToElement";
    private String sessionId;
    private ConcurrentMap<String, JSONObject> commandConfiguration;
    private AccessibilityScrollData lastScrollData;
    private  Map<String, Object> capabilities = new HashMap<>();
    private CachedElements cachedElements;

    public Session(CachedElements cachedElements) {
        this.sessionId = UUID.randomUUID().toString();
        this.commandConfiguration = new ConcurrentHashMap<>();
        this.commandConfiguration.put(SEND_KEYS_TO_ELEMENT, new JSONObject());
        this.cachedElements = cachedElements;
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

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> caps) {
        capabilities = caps;
    }

    public CachedElements getCachedElements() {
        return cachedElements;
    }

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
