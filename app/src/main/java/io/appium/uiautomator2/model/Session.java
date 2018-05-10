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

import org.apache.commons.lang.BooleanUtils;

import java.util.HashMap;
import java.util.Map;

import static io.appium.uiautomator2.model.settings.Settings.ELEMENT_RESPONSE_ATTRIBUTES;
import static io.appium.uiautomator2.model.settings.Settings.SHOULD_USE_COMPACT_RESPONSES;

public class Session {
    private Map<String, Object> capabilities = new HashMap<>();
    private String sessionId;
    private AccessibilityScrollData lastScrollData;

    public Session(String sessionId, Map<String, Object> capabilities) {
        this.sessionId = sessionId;
        this.capabilities = capabilities;
    }

    public boolean shouldUseCompactResponses() {
        boolean shouldUseCompactResponses = true;
        if (capabilities.containsKey(SHOULD_USE_COMPACT_RESPONSES.toString())) {
            shouldUseCompactResponses = BooleanUtils.toBoolean(
                    capabilities.get(SHOULD_USE_COMPACT_RESPONSES.toString()).toString());
        }
        return shouldUseCompactResponses;
    }

    public String[] getElementResponseAttributes() {
        if (capabilities.containsKey(ELEMENT_RESPONSE_ATTRIBUTES.toString())) {
            return capabilities.get(ELEMENT_RESPONSE_ATTRIBUTES.toString()).toString()
                    .split(",");
        }
        return new String[]{"name", "text"};
    }

    public String getSessionId() {
        return sessionId;
    }

    public AccessibilityScrollData getLastScrollData() {
        return lastScrollData;
    }

    public void setLastScrollData(AccessibilityScrollData scrollData) {
        lastScrollData = scrollData;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }
}
