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
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.common.exceptions.NoSuchDriverException;
import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.core.finder.BySelectorFinder;
import io.appium.uiautomator2.core.finder.UiSelectorFinder;
import io.appium.uiautomator2.model.AccessibilityNodeInfoList;
import io.appium.uiautomator2.model.AndroidElement;

public class CoreFacade {

    private final UiDeviceAdapter uiDeviceAdapter;
    private final GesturesAdapter gesturesAdapter;
    private final InteractionControllerAdapter interactionControllerAdapter;
    private final UiAutomation uiAutomation;
    private final AccessibilityInteractionClientAdapter accessibilityInteractionClientAdapter;
    private final ScrollEventRegister scrollEventRegister;
    private final QueryControllerAdapter queryControllerAdapter;
    private final AccessibilityNodeInfoDumper accessibilityNodeInfoDumper;
    private final UiSelectorFinder uiSelectorFinder;
    private final BySelectorFinder bySelectorFinder;

    public CoreFacade(@NonNull final UiDeviceAdapter uiDeviceAdapter,
                      @NonNull final GesturesAdapter gesturesAdapter,
                      @NonNull final InteractionControllerAdapter interactionControllerAdapter,
                      @NonNull final UiAutomation uiAutomation,
                      @NonNull final AccessibilityInteractionClientAdapter
                              accessibilityInteractionClientAdapter,
                      @NonNull final ScrollEventRegister scrollEventRegister,
                      @NonNull final QueryControllerAdapter queryControllerAdapter,
                      @NonNull final AccessibilityNodeInfoDumper accessibilityNodeInfoDumper,
                      @NonNull final UiSelectorFinder uiSelectorFinder,
                      @NonNull final BySelectorFinder bySelectorFinder) {
        this.uiDeviceAdapter = uiDeviceAdapter;
        this.gesturesAdapter = gesturesAdapter;
        this.interactionControllerAdapter = interactionControllerAdapter;
        this.uiAutomation = uiAutomation;
        this.accessibilityInteractionClientAdapter = accessibilityInteractionClientAdapter;
        this.scrollEventRegister = scrollEventRegister;
        this.queryControllerAdapter = queryControllerAdapter;
        this.accessibilityNodeInfoDumper = accessibilityNodeInfoDumper;
        this.uiSelectorFinder = uiSelectorFinder;
        this.bySelectorFinder = bySelectorFinder;
    }

    public void back() {
        uiDeviceAdapter.back();
    }

    public void clearAccessibilityCache() {
        accessibilityInteractionClientAdapter.clearAccessibilityCache();
    }

    public Boolean click(int x, int y) {
        return uiDeviceAdapter.click(x, y);
    }

    public boolean drag(final int startX, final int startY, final int endX, final int endY, final
    Integer steps) {
        return uiDeviceAdapter.drag(startX, startY, endX, endY, steps);
    }

    @NonNull
    public AndroidElement findElement(UiSelector selector) throws ElementNotFoundException {
        return uiSelectorFinder.findElement(selector);
    }

    @NonNull
    public AndroidElement findElement(@NonNull final BySelector bySelector) throws ElementNotFoundException {
        return bySelectorFinder.findElement(bySelector);
    }

    @NonNull
    public AndroidElement findElement(@NonNull final AccessibilityNodeInfoList nodeList) throws ElementNotFoundException {
        return bySelectorFinder.findElement(nodeList);
    }

    @NonNull
    public AndroidElement findElement(@NonNull final AccessibilityNodeInfo nodeInfo) throws ElementNotFoundException {
        return bySelectorFinder.findElement(nodeInfo);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final BySelector selector) {
        return bySelectorFinder.findElements(selector);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final UiSelector selector) throws ElementNotFoundException {
        return uiSelectorFinder.findElements(selector);
    }

    @NonNull
    public List<AndroidElement> findElements(@NonNull final AccessibilityNodeInfoList nodeList) {
        return bySelectorFinder.findElements(nodeList);
    }

    public int getDisplayHeight() {
        return uiDeviceAdapter.getDisplayHeight();
    }

    public int getDisplayRotation() {
        return uiDeviceAdapter.getDisplayRotation();
    }

    public int getDisplayWidth() {
        return uiDeviceAdapter.getDisplayWidth();
    }

    public AccessibilityNodeInfo getRootNode() {
        return queryControllerAdapter.getRootNode();
    }

    public int getScaledTouchSlop() {
        return gesturesAdapter.getViewConfig().getScaledTouchSlop();
    }

    public Boolean injectEventSync(@NonNull final InputEvent event) throws NoSuchDriverException {
        return interactionControllerAdapter.injectEventSync(event);
    }

    public boolean openNotification() {
        return uiDeviceAdapter.openNotification();
    }

    @Nullable
    public Boolean performMultiPointerGesture(@NonNull final MotionEvent.PointerCoords[][] pcs) throws NoSuchDriverException {
        return interactionControllerAdapter.performMultiPointerGesture(pcs);
    }

    public boolean pressEnter() {
        return uiDeviceAdapter.pressEnter();
    }

    public void pressKeyCode(@NonNull final Integer keyCode, @NonNull final Integer metaState) {
        uiDeviceAdapter.pressKeyCode(keyCode, metaState);
    }

    public void pressKeyCode(@NonNull final Integer keyCode) {
        uiDeviceAdapter.pressKeyCode(keyCode);
    }

    @Nullable
    public Boolean runAndRegisterScrollEvents(@NonNull final ReturningRunnable<Boolean> returningRunnable) throws NoSuchDriverException {
        return scrollEventRegister.runAndRegisterScrollEvents(returningRunnable);
    }

    public void scrollTo(@NonNull final String scrollToString) throws UiObjectNotFoundException {
        uiDeviceAdapter.scrollTo(scrollToString);
    }

    public void setCompressedLayoutHierarchy(@NonNull final Boolean compressLayout) {
        uiDeviceAdapter.setCompressedLayoutHierarchy(compressLayout);
    }

    public void setOrientationLeft() throws RemoteException {
        uiDeviceAdapter.setOrientationLeft();
    }

    public void setOrientationNatural() throws RemoteException {
        uiDeviceAdapter.setOrientationNatural();
    }

    public void setOrientationRight() throws RemoteException {
        uiDeviceAdapter.setOrientationRight();
    }

    public boolean setRotation(final int rotation) {
        return uiAutomation.setRotation(rotation);
    }

    public boolean swipe(final int startX, final int startY, final int endX, final int endY, final
    Integer steps) {
        return uiDeviceAdapter.swipe(startX, startY, endX, endY, steps);
    }

    @Nullable
    public Boolean touchDown(final int x, final int y) throws NoSuchDriverException {
        return interactionControllerAdapter.touchDown(x, y);
    }

    @Nullable
    public Boolean touchMove(final int x, final int y) throws NoSuchDriverException {
        return interactionControllerAdapter.touchMove(x, y);
    }

    @Nullable
    public Boolean touchUp(final int x, final int y) throws NoSuchDriverException {
        return interactionControllerAdapter.touchUp(x, y);
    }

    public <R> R wait(@NonNull final SearchCondition<R> condition, final long timeout) {
        return uiDeviceAdapter.wait(condition, timeout);
    }

    public void waitForIdle() {
        uiDeviceAdapter.waitForIdle();
    }

    public void waitForIdle(@NonNull final Integer timeout) {
        uiDeviceAdapter.waitForIdle(timeout);
    }

    public void wakeUp() throws RemoteException {
        uiDeviceAdapter.wakeUp();
    }

    public String getWindowXMLHierarchy(@Nullable final AccessibilityNodeInfo root) throws
            UiAutomator2Exception {
        return accessibilityNodeInfoDumper.getWindowXMLHierarchy(root);
    }

    public String safeCharSeqToString(@Nullable final CharSequence cs) {
        return accessibilityNodeInfoDumper.safeCharSeqToString(cs);
    }

    public void setCompressedLayoutHeirarchy(Boolean compressLayout) {
        uiDeviceAdapter.setCompressedLayoutHierarchy(compressLayout);
    }
}
