/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.appium.uiautomator2.core;

import android.graphics.Point;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Xml;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoHelper;
import io.appium.uiautomator2.utils.Logger;

/**
 * The AccessibilityNodeInfoDumper in Android Open Source Project contains a lot of bugs which will
 * stay in old android versions forever. By coping the code of the latest version it is ensured that
 * all patches become available on old android versions. <p/> down ported bugs are e.g. { @link
 * https://code.google.com/p/android/issues/detail?id=62906 } { @link
 * https://code.google.com/p/android/issues/detail?id=58733 }
 */
public class AccessibilityNodeInfoDumper {
    // XML 1.0 Legal Characters (http://stackoverflow.com/a/4237934/347155)
    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    private static final Pattern XML10Pattern = Pattern.compile("[^" + "\u0009\r\n" +
            "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]");
    private static final String[] NAF_EXCLUDED_CLASSES = new String[]{android.widget.GridView
            .class.getName(), android.widget.GridLayout.class.getName(), android.widget.ListView
            .class.getName(), android.widget.TableLayout.class.getName()};
    @NonNull
    private final UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter;
    @NonNull
    private final AccessibilityNodeInfoHelper accessibilityNodeInfoHelper;

    @Inject
    AccessibilityNodeInfoDumper(@NonNull final UiAutomatorBridgeAdapter uiAutomatorBridgeAdapter,
                                @NonNull final AccessibilityNodeInfoHelper
                                        accessibilityNodeInfoHelper) {
        this.uiAutomatorBridgeAdapter = uiAutomatorBridgeAdapter;
        this.accessibilityNodeInfoHelper = accessibilityNodeInfoHelper;
    }

    /**
     * Using {@link AccessibilityNodeInfo} this method will walk the layout hierarchy and return
     * String object of xml hierarchy
     *
     * @param root The root accessibility node.
     */
    public String getWindowXMLHierarchy(@Nullable final AccessibilityNodeInfo root) throws
            UiAutomator2Exception {
        final long startTime = SystemClock.uptimeMillis();
        final StringWriter xmlDump = new StringWriter();
        try {
            final XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(xmlDump);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "hierarchy");

            if (root != null) {
                final Display display = uiAutomatorBridgeAdapter.getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);

                serializer.attribute("", "rotation", Integer.toString(display.getRotation()));
                dumpNodeRec(root, serializer, 0, size.x, size.y);
            }

            serializer.endTag("", "hierarchy");
            serializer.endDocument();
        } catch (@NonNull final IOException e) {
            Logger.error("failed to dump window to file", e);
        }
        final long endTime = SystemClock.uptimeMillis();
        Logger.info("Fetch time: %d ms", (endTime - startTime));
        return xmlDump.toString();
    }


    private void dumpNodeRec(@NonNull final AccessibilityNodeInfo node,
                             @NonNull final XmlSerializer serializer, final int index,
                             final int width, final int height) throws IOException {
        serializer.startTag("", "node");
        if (!nafExcludedClass(node) && !nafCheck(node))
            serializer.attribute("", "NAF", Boolean.toString(true));
        serializer.attribute("", "index", Integer.toString(index));
        final String text;
        if (node.getRangeInfo() == null) {
            text = safeCharSeqToString(node.getText());
        } else {
            text = Float.toString(node.getRangeInfo().getCurrent());
        }
        serializer.attribute("", "text", text);
        serializer.attribute("", "class", safeCharSeqToString(node.getClassName()));
        serializer.attribute("", "package", safeCharSeqToString(node.getPackageName()));
        serializer.attribute("", "content-desc", safeCharSeqToString(node.getContentDescription()));
        serializer.attribute("", "checkable", Boolean.toString(node.isCheckable()));
        serializer.attribute("", "checked", Boolean.toString(node.isChecked()));
        serializer.attribute("", "clickable", Boolean.toString(node.isClickable()));
        serializer.attribute("", "enabled", Boolean.toString(node.isEnabled()));
        serializer.attribute("", "focusable", Boolean.toString(node.isFocusable()));
        serializer.attribute("", "focused", Boolean.toString(node.isFocused()));
        serializer.attribute("", "scrollable", Boolean.toString(node.isScrollable()));
        serializer.attribute("", "long-clickable", Boolean.toString(node.isLongClickable()));
        serializer.attribute("", "password", Boolean.toString(node.isPassword()));
        serializer.attribute("", "selected", Boolean.toString(node.isSelected()));
        serializer.attribute("", "bounds", accessibilityNodeInfoHelper
                .getVisibleBoundsInScreen(node, width, height).toShortString());
        serializer.attribute("", "resource-id", safeCharSeqToString(node.getViewIdResourceName()));

        final int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            final AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (child.isVisibleToUser()) {
                    dumpNodeRec(child, serializer, i, width, height);
                } else {
                    Logger.info("Skipping invisible child: %s", child.toString());
                }
                child.recycle();
            } else {
                Logger.info("Null child %d/%d, parent: %s", i, count, node.toString());
            }
        }
        serializer.endTag("", "node");
    }

    /**
     * The list of classes to exclude my not be complete. We're attempting to only reduce noise from
     * standard layout classes that may be falsely configured to accept clicks and are also
     * enabled.
     *
     * @return true if node is excluded.
     */
    private boolean nafExcludedClass(@NonNull final AccessibilityNodeInfo node) {
        final String className = safeCharSeqToString(node.getClassName());
        for (final String excludedClassName : NAF_EXCLUDED_CLASSES) {
            if (className.endsWith(excludedClassName)) return true;
        }
        return false;
    }

    /**
     * We're looking for UI controls that are enabled, clickable but have no text nor
     * content-description. Such controls configuration indicate an interactive control is present
     * in the UI and is most likely not accessibility friendly. We refer to such controls here as
     * NAF controls (Not Accessibility Friendly)
     *
     * @return false if a node fails the check, true if all is OK
     */
    private boolean nafCheck(@NonNull final AccessibilityNodeInfo node) {
        boolean isNaf = node.isClickable() && node.isEnabled() && safeCharSeqToString(node
                .getContentDescription()).isEmpty() && safeCharSeqToString(node.getText())
                .isEmpty();
        if (!isNaf) return true;
        // check children since sometimes the containing element is clickable
        // and NAF but a child's text or description is available. Will assume
        // such layout as fine.
        return childNafCheck(node);
    }

    /**
     * This should be used when it's already determined that the node is NAF and a further check of
     * its children is in order. A node maybe a container such as LinerLayout and may be set to be
     * clickable but have no text or content description but it is counting on one of its children
     * to fulfill the requirement for being accessibility friendly by having one or more of its
     * children fill the text or content-description. Such a combination is considered by this
     * dumper as acceptable for accessibility.
     *
     * @return false if node fails the check.
     */
    private boolean childNafCheck(@NonNull final AccessibilityNodeInfo node) {
        int childCount = node.getChildCount();
        for (int x = 0; x < childCount; x++) {
            AccessibilityNodeInfo childNode = node.getChild(x);
            if (childNode == null) {
                Logger.info("Null child %d/%d, parent: %s", x, childCount, node.toString());
                continue;
            }
            if (!safeCharSeqToString(childNode.getContentDescription()).isEmpty() ||
                    !safeCharSeqToString(childNode.getText()).isEmpty())
                return true;
            if (childNafCheck(childNode)) return true;
        }
        return false;
    }

    public String safeCharSeqToString(@Nullable final CharSequence cs) {
        if (cs == null) {
            return "";
        }
        return XML10Pattern.matcher(String.valueOf(cs)).replaceAll("?");
    }
}
