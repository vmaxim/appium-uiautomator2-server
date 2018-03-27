/*
 * Copyright (C) 2013 DroidDriver committers
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
package io.appium.uiautomator2.model;

import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import io.appium.uiautomator2.App;
import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Attribute;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Preconditions;

import static io.appium.uiautomator2.App.core;

/**
 * Find matching UiElementSnapshot by XPath.
 */
public class XPathFinder implements Finder {
    public final static Map<AccessibilityNodeInfo, UiElementANISnapshot> map = new WeakHashMap<>();
    private static final XPath XPATH_COMPILER = XPathFactory.newInstance().newXPath();
    // The two maps should be kept in sync
    private static final Map<UiElementSnapshot<?, ?>, Element> TO_DOM_MAP = new HashMap<>();
    private static final Map<Element, UiElementSnapshot<?, ?>> FROM_DOM_MAP = new HashMap<>();
    // document needs to be static so that when buildDomNode is called recursively
    // on children they are in the same document to be appended.
    private static Document document;
    private static UiElementANISnapshot rootElement;
    private final String xPathString;
    private final XPathExpression xPathExpression;

    public XPathFinder(String xPathString) throws XPathExpressionException {
        this.xPathString = Preconditions.checkNotNull(xPathString);
        xPathExpression = XPATH_COMPILER.compile(xPathString);
    }

    public static void clearData() {
        TO_DOM_MAP.clear();
        FROM_DOM_MAP.clear();
        document = null;
    }

    public static AccessibilityNodeInfoList getNodesList(String xpathExpression,
                                                         AccessibilityNodeInfo nodeInfo) throws
            NoSuchDriverException, XPathExpressionException, ElementNotFoundException {
        if (nodeInfo == null) {
            refreshUiElementTree();
        } else {
            refreshUiElementTree(nodeInfo);
        }
        XPathFinder finder = new XPathFinder(xpathExpression);
        AccessibilityNodeInfoList result = finder.find(finder.getRootElement());
        if (result.isEmpty()) {
            throw new ElementNotFoundException("Strategy:ByXpath;Locator:" + xpathExpression);
        }
        return result;
    }

    public static void refreshUiElementTree() throws NoSuchDriverException {
        rootElement = UiElementANISnapshot.newRootElement(core.getCoreFacade().getRootNode(),
                NotificationListener.getToastMSGs());
    }

    public static void refreshUiElementTree(AccessibilityNodeInfo nodeInfo) throws
            NoSuchDriverException {
        rootElement = UiElementANISnapshot.newRootElement(nodeInfo, null /*Toast Messages*/);
    }

    private Document getDocument() {
        if (document == null) {
            try {
                document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                throw new UiAutomator2Exception(e);
            }
        }
        return document;
    }

    /**
     * Returns the DOM node representing this UiElementSnapshot.
     */
    private Element getDomNode(UiElementSnapshot<?, ?> uiElementSnapshot) {
        Element domNode = TO_DOM_MAP.get(uiElementSnapshot);
        if (domNode == null) {
            domNode = buildDomNode(uiElementSnapshot);
        }
        return domNode;
    }

    private void setNodeLocalName(Element element, String className) {
        try {
            Field localName = element.getClass().getDeclaredField("localName");
            localName.setAccessible(true);
            localName.set(element, tag(className));
        } catch (NoSuchFieldException e) {
            Logger.error("Unable to set field localName:" + e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.error("Unable to set field localName:" + e.getMessage());
        }
    }

    private Element buildDomNode(UiElementSnapshot<?, ?> uiElementSnapshot) {
        String className = uiElementSnapshot.getClassName();
        if (className == null) {
            className = "UNKNOWN";
        }
        Element element = getDocument().createElement(simpleClassName(className));
        TO_DOM_MAP.put(uiElementSnapshot, element);
        FROM_DOM_MAP.put(element, uiElementSnapshot);

        /**
         * Setting the Element's className field.
         * Reason for setting className field in Element object explicitly,
         * className property might contain special characters like '$' if it is a Inner class and
         * just not possible to create Element object with special characters.
         * But Appium should consider Inner classes i.e special characters should be included.
         */
        setAttribute(element, Attribute.INDEX, String.valueOf(uiElementSnapshot.getIndex()));
        setAttribute(element, Attribute.CLASS, className);
        setAttribute(element, Attribute.RESOURCE_ID, uiElementSnapshot.getResourceId());
        setAttribute(element, Attribute.PACKAGE, uiElementSnapshot.getPackageName());
        setAttribute(element, Attribute.CONTENT_DESC, uiElementSnapshot.getContentDescription());
        setAttribute(element, Attribute.TEXT, uiElementSnapshot.getText());
        setAttribute(element, Attribute.CHECKABLE, uiElementSnapshot.isCheckable());
        setAttribute(element, Attribute.CHECKED, uiElementSnapshot.isChecked());
        setAttribute(element, Attribute.CLICKABLE, uiElementSnapshot.isClickable());
        setAttribute(element, Attribute.ENABLED, uiElementSnapshot.isEnabled());
        setAttribute(element, Attribute.FOCUSABLE, uiElementSnapshot.isFocusable());
        setAttribute(element, Attribute.FOCUSED, uiElementSnapshot.isFocused());
        setAttribute(element, Attribute.SCROLLABLE, uiElementSnapshot.isScrollable());
        setAttribute(element, Attribute.LONG_CLICKABLE, uiElementSnapshot.isLongClickable());
        setAttribute(element, Attribute.PASSWORD, uiElementSnapshot.isPassword());
        if (uiElementSnapshot.hasSelection()) {
            element.setAttribute(Attribute.SELECTION_START.getName(),
                    Integer.toString(uiElementSnapshot.getSelectionStart()));
            element.setAttribute(Attribute.SELECTION_END.getName(),
                    Integer.toString(uiElementSnapshot.getSelectionEnd()));
        }
        setAttribute(element, Attribute.SELECTED, uiElementSnapshot.isSelected());
        element.setAttribute(Attribute.BOUNDS.getName(), uiElementSnapshot.getBounds() == null ?
                null : uiElementSnapshot.getBounds().toShortString());

        for (UiElementSnapshot<?, ?> child : uiElementSnapshot.getChildren()) {
            element.appendChild(getDomNode(child));
        }
        return element;
    }

    private void setAttribute(Element element, Attribute attr, String value) {
        if (value != null) {
            element.setAttribute(attr.getName(), value);
        }
    }

    private void setAttribute(Element element, Attribute attr, boolean value) {
        element.setAttribute(attr.getName(), String.valueOf(value));
    }

    /**
     * @return The tag name used to build UiElementSnapshot DOM. It is preferable to use
     * this to build XPath instead of String literals.
     */
    public String tag(String className) {
        // the nth anonymous class has a class name ending in "Outer$n"
        // and local inner classes have names ending in "Outer.$1Inner"
        return className.replaceAll("\\$[0-9]+", "\\$");
    }

    /**
     * returns by excluding inner class name.
     */
    private String simpleClassName(String name) {
        String result = name.replaceAll("\\$[0-9]+", "\\$");
        // we want the index of the inner class
        int start = result.lastIndexOf('$');

        // if this isn't an inner class, just find the start of the
        // top level class name.
        if (start == -1) {
            return result;
        }
        return result.substring(0, start);
    }

    @Override
    public String toString() {
        return xPathString;
    }

    @Override
    public AccessibilityNodeInfoList find(UiElementSnapshot context) throws
            ElementNotFoundException {
        Element domNode = getDomNode((UiElementSnapshot<?, ?>) context);
        try {
            getDocument().appendChild(domNode);
            NodeList nodes = (NodeList) xPathExpression.evaluate(domNode, XPathConstants.NODESET);
            AccessibilityNodeInfoList list = new AccessibilityNodeInfoList();

            int nodesLength = nodes.getLength();
            for (int i = 0; i < nodesLength; i++) {
                if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE && !FROM_DOM_MAP.get(nodes
                        .item(i)).getClassName().equals("hierarchy")) {
                    list.add(FROM_DOM_MAP.get(nodes.item(i)).node);
                }
            }
            return list;
        } catch (XPathExpressionException e) {
            throw new ElementNotFoundException(e);
        } finally {
            try {
                getDocument().removeChild(domNode);
            } catch (DOMException e) {
                Logger.error("Failed to clear document", e);
                document = null; // getDocument will create new
            }
        }
    }

    public UiElementANISnapshot getRootElement() throws NoSuchDriverException {
        if (rootElement == null) {
            refreshUiElementTree();
        }
        return rootElement;
    }
}
