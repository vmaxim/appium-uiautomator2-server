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

package io.appium.uiautomator2.unittest.test;

import android.util.Pair;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.unittest.test.internal.BaseTest;
import io.appium.uiautomator2.unittest.test.internal.Response;
import io.appium.uiautomator2.unittest.test.internal.commands.DeviceCommands;

import static io.appium.uiautomator2.unittest.test.internal.TestUtils.waitForElement;
import static io.appium.uiautomator2.unittest.test.internal.TestUtils.waitForElementInvisibility;
import static io.appium.uiautomator2.unittest.test.internal.commands.DeviceCommands.findElement;
import static io.appium.uiautomator2.unittest.test.internal.commands.DeviceCommands.findElements;
import static io.appium.uiautomator2.unittest.test.internal.commands.ElementCommands.getText;
import static io.appium.uiautomator2.unittest.test.internal.commands.ElementCommands.sendKeys;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SuppressWarnings("JavaDoc")
public class FindElementCommandsTest extends BaseTest {
    private static final String MAIN_ACTIVITY = ".ApiDemos";
    private static final String SCROLLBAR_ACTIVITY = ".view.ScrollBar1";
    private static final String CONTROLS_ACTIVITY = ".view.Controls1";

    private Response response;
    private By by;
    private By unsupportedBy = new By() {

        @Override
        public String getElementLocator() {
            return "unsupported";
        }

        @Override
        public String getElementStrategy() {
            return "unsupported";
        }
    };
    private List<By> fakeElementList = Arrays.asList(
            By.xpath("//*[@text='test']"),
            By.className("test"),
            By.id("test"),
            By.accessibilityId("test"),
            By.androidUiAutomator("text(\"test\")")
    );
    private List<By> contextList = Arrays.asList(
            By.className("android.widget.Spinner"),
            By.id("resize_mode"),
            By.xpath("//*[@resource-id='io.appium.android.apis:id/resize_mode']"),
            By.androidUiAutomator(" className ( android.widget.Spinner ). " +
                    "resourceId (\"io.appium.android.apis:id/resize_mode\")")
            // TODO: Add By.accessibilityId context
    );
    private List<By> targetList = Arrays.asList(
            By.xpath("./android.widget.TextView"),
            By.id("android:id/text1"),
            By.className("android.widget.TextView"),
            By.androidUiAutomator("UiSelector(). className(\"android.widget.TextView\")")
            // TODO: Add By.accessibilityId target
    );

    @Before
    public void setUp() throws JSONException {
        startActivity(CONTROLS_ACTIVITY);
    }

    /* Common Tests */
    @Test
    public void shouldBeAbleToFindElement() {
        List<By> byList = Arrays.asList(
                By.xpath("(//*[@resource-id='android:id/content']/*" +
                        "//android.widget.CheckBox[@clickable='true' and @enabled='true'])" +
                        "[position()>=1][not(@text='')]"),
                By.id("io.appium.android.apis:id/check1"),
                By.className("android.widget.CheckBox"),
                By.accessibilityId("Checkbox 1"),
                By.androidUiAutomator("resourceId(\"io.appium.android.apis:id/check1\")")

        );
        for (By by : byList) {
            response = findElement(by);
            assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
            /* Check element text */
            response = getText(response.getElementId());
            assertEquals("Checkbox 1", response.getValue());
        }
    }

    @Test
    public void shouldBeAbleToFindElements() throws JSONException {
        List<Pair<String, By>> byList = Arrays.asList(
                new Pair<>(MAIN_ACTIVITY, By.xpath("//*[@resource-id='android:id/text1']")),
                new Pair<>(MAIN_ACTIVITY, By.id("android:id/text1")),
                new Pair<>(MAIN_ACTIVITY, By.className("android.widget.TextView")),
                new Pair<>(SCROLLBAR_ACTIVITY, By.accessibilityId("Lorem ipsum dolor sit amet.")),
                new Pair<>(SCROLLBAR_ACTIVITY,
                        By.androidUiAutomator("text(\"Lorem ipsum dolor sit amet.\")"))

        );

        for (Pair<String, By> pair : byList) {
            startActivity(pair.first);
            response = findElements(pair.second);
            assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
            assertThat(response.getElementCount(), greaterThan(1));
        }
    }

    @Test
    public void findElementShouldReturnNoSuchElementIfElementDoesNotExist() {
        for (By by : fakeElementList) {
            response = findElement(by, 0L);
            assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), response.getStatus());
        }
    }

    @Test
    public void findElementsShouldNOTReturnNoSuchElementIfElementDoesNotExist() {
        for (By by : fakeElementList) {
            response = findElements(by);
            assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
        }
    }

    @Test
    public void findElementsShouldNotCorruptLocatorWithInstance() {
        by = By.androidUiAutomator("className(android.widget.CheckBox).instance(1)");
        response = findElements(by);
        assertEquals(WDStatus.SUCCESS.code(), response.getStatus());

        /* Check element text */
        response = getText(response.getElementId(0));
        assertEquals("Checkbox 2", response.getValue());
    }

    // TODO: Move this test to element tests
    @Test
    public void shouldThrowStaleElementExceptionIfElementIsNotLongerExist() throws JSONException {
        List<By> byList = Arrays.asList(
                By.xpath("//*[@text='Save']"),
                By.className("android.widget.Button"),
                By.id("button"),
                By.accessibilityId("Save")
                /* StaleElementReferenceException is not supported By.androidUiAutomator */
        );
        List<String> elements = new ArrayList<>();
        for (By by : byList) {
            elements.add(findElement(by).getElementId());
        }
        startActivity(MAIN_ACTIVITY);
        waitForElementInvisibility(elements.get(0));

        for (String element : elements) {
            response = getText(element);
            assertEquals(WDStatus.STALE_ELEMENT_REFERENCE.code(), response.getStatus());
        }
    }

    @Test
    public void findElementShouldThrowErrorIfStrategyIsNotSupported() {
        response = findElement(unsupportedBy, 0L);
        assertEquals(WDStatus.UNKNOWN_ERROR.code(), response.getStatus());
    }

    @Test
    public void findElementsShouldThrowErrorIfStrategyIsNotSupported() {
        response = findElements(unsupportedBy);
        assertEquals(WDStatus.UNKNOWN_ERROR.code(), response.getStatus());
    }
    /* End Common Tests */

    /* By.xpath */
    @Test
    public void shouldIgnoreTopHierarchyNode() {
        by = By.xpath("//hierarchy");
        response = findElement(by, 0L);
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), response.getStatus());
    }

    @Test
    public void shouldAddRootHierarchyNodeInPath() {
        by = By.xpath("//hierarchy//android.widget.ScrollView");
        response = findElement(by);
        assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
    }

    @Test
    public void shouldReturnInvalidSelectorIfXpathExpIsInvalid() {
        by = By.xpath("//[");
        response = findElement(by, 0L);
        assertEquals(WDStatus.INVALID_SELECTOR.code(), response.getStatus());
    }
    /* End By.xpath */

    /* Context Search */
    @Test
    public void shouldFindElementFromContext() throws JSONException {
        startActivity(".app.SoftInputModes");
        for (By context : contextList) {
            response = findElement(context);
            String contextId = response.getElementId();
            for (By target : targetList) {
                response = findElement(target, contextId);
                assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
                response = getText(response.getElementId());
                assertEquals("Unspecified", response.getValue());
            }
        }
    }

    @Test
    public void shouldFindElementsFromContext() throws JSONException {
        startActivity(".app.SoftInputModes");
        for (By context : contextList) {
            response = findElement(context);
            String contextId = response.getElementId();
            for (By target : targetList) {
                response = findElements(target, contextId);
                assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
                assertThat(response.getElementCount(), greaterThan(0));
            }
        }
    }

    @Test
    public void findElementShouldReturnNoSuchElementIfContextDoesNotExist() {
        response = findElement(By.xpath("//*"), "invalidcontext", 0L);
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), response.getStatus());
    }

    @Test
    // TODO: Check the spec about SUCCESS responce status
    public void findElementsShouldNOTReturnNoSuchElementIfContextDoesNotExist() {
        response = findElements(By.xpath("//*"), "invalidcontext");
        assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
    }
    /* End Context Search */

    /* By.androidUiAutomator */
    @Test
    public void scrollMethodsShouldNotThrowExceptionIfScrollableDoesNotExist() throws
            JSONException {
        final String scrollableLocator = "new UiScrollable(UiSelector().text(\"test\"))" +
                ".setAsHorizontalList()";
        List<By> byList = Arrays.asList(
                By.androidUiAutomator(scrollableLocator +
                        ".scrollIntoView(new UiSelector().text(\"Save\"))"),
                By.androidUiAutomator(scrollableLocator + ".scrollDescriptionIntoView(\"Save\")"),
                By.androidUiAutomator(scrollableLocator + ".scrollTextIntoView(\"Save\")")
        );
        for (By by : byList) {
            response = findElement(by);
            assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
        }
    }

    @Test
    public void shouldBeAbleToFindElementViaUiSelectorWithQuotesParenthesesAndCommasInParams()
            throws JSONException {
        startActivity(".view.TextFields");
        response = waitForElement(By.id("io.appium.android.apis:id/edit"));
        sendKeys(response.getElementId(), "Use a \"tel:\" (415) Expressway, Suite 400, Austin");
        By androidUiAutomator = By.androidUiAutomator(".text(" +
                "\"Use a \\\"tel:\\\" (415) Expressway, Suite 400, Austin\");");
        response = findElement(androidUiAutomator);
        assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
    }

    @Test
    public void shouldBeAbleToFindElementViaUiScrollableScrollMethods() throws JSONException {
        final String uiScrollable = "new UiScrollable(new UiSelector()" +
                ".resourceId(\"android:id/list\"))";
        List<By> byList = Arrays.asList(
                By.androidUiAutomator(uiScrollable +
                        ".scrollIntoView(new UiSelector().text(\"Beer Cheese\"))"),
                By.androidUiAutomator(uiScrollable + ".scrollTextIntoView(\"Beer Cheese\")")
                // TODO: Add getChildByDescription and scrollDescriptionIntoView methods
        );
        for (By by : byList) {
            startActivity(".view.List1");
            waitForElement(By.id("android:id/list"));
            response = findElement(by);
            assertEquals(WDStatus.SUCCESS.code(), response.getStatus());
        }
    }

    @Test
    public void shouldThrowExceptionIfMethodDoesNotReturnUiSelector() {
        by = By.androidUiAutomator("toString()");
        response = findElement(by, 0L);
        assertEquals(WDStatus.INVALID_SELECTOR.code(), response.getStatus());
    }

    @Test
    public void shouldReturnInvalidSelectorIfUiSelectorIsInvalid() {
        Map<String, String> invalidSelectors = new HashMap<>();
        invalidSelectors.put("Empty locator", "");
        invalidSelectors.put("No acceptable prefix", "test");
        invalidSelectors.put("No period", "UiSelector()text");
        invalidSelectors.put("Invalid argument count", "UiSelector().text(\"test\", 1)");
        invalidSelectors.put("Missing argument", "UiSelector().text(,)");
        invalidSelectors.put("Missing last argument", "UiSelector().text(\"test\",)");
        invalidSelectors.put("Missing method name", "UiSelector().(\"test\")");
        invalidSelectors.put("Invalid constructor", "UiSelector(\"test\")");
        invalidSelectors.put("Unclosed paren", "UiSelector().text(");
        invalidSelectors.put("Invalid method", "UiSelector().test()");
        invalidSelectors.put("Invalid string param type", "UiSelector().text(1)");
        invalidSelectors.put("Invalid boolean param type", "UiSelector().enabled(1)");
        invalidSelectors.put("Invalid integer param type", "UiSelector().index(a)");
        for (Map.Entry<String, String> entry : invalidSelectors.entrySet()) {
            by = By.androidUiAutomator(entry.getValue());
            response = findElement(by, 0L);
            assertEquals(entry.getKey(), WDStatus.INVALID_SELECTOR.code(), response.getStatus());
        }
    }
    /* End By.androidUiAutomator */
}
