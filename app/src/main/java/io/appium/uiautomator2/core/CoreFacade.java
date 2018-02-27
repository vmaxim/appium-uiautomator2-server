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

import io.appium.uiautomator2.model.AndroidElement;
import io.appium.uiautomator2.utils.AccessibilityNodeInfoList;

public class CoreFacade {

    private final ElementFinder elementFinder;
    private final UiDeviceAdapter uiDeviceAdapter;
    private final GesturesAdapter gesturesAdapter;
    private final InteractionControllerAdapter interactionControllerAdapter;
    private final UiAutomation uiAutomation;
    private final AccessibilityInteractionClientAdapter accessibilityInteractionClientAdapter;
    private final EventRegister eventRegister;
    private final QueryControllerAdapter queryControllerAdapter;

    public CoreFacade(@NonNull final ElementFinder elementFinder,
                      @NonNull final UiDeviceAdapter uiDeviceAdapter,
                      @NonNull final GesturesAdapter gesturesAdapter,
                      @NonNull final InteractionControllerAdapter interactionControllerAdapter,
                      @NonNull final UiAutomation uiAutomation,
                      @NonNull final AccessibilityInteractionClientAdapter
                              accessibilityInteractionClientAdapter,
                      @NonNull final EventRegister eventRegister,
                      @NonNull final QueryControllerAdapter queryControllerAdapter) {
        this.elementFinder = elementFinder;
        this.uiDeviceAdapter = uiDeviceAdapter;
        this.gesturesAdapter = gesturesAdapter;
        this.interactionControllerAdapter = interactionControllerAdapter;
        this.uiAutomation = uiAutomation;
        this.accessibilityInteractionClientAdapter = accessibilityInteractionClientAdapter;
        this.eventRegister = eventRegister;
        this.queryControllerAdapter = queryControllerAdapter;
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

    @Nullable
    public AndroidElement findElement(@NonNull final BySelector bySelector) {
        return elementFinder.findElement(bySelector);
    }

    @Nullable
    public AndroidElement findElement(@NonNull final AccessibilityNodeInfoList nodeList) {
        return elementFinder.findElement(nodeList);
    }

    @Nullable
    public AndroidElement findElement(@NonNull final UiSelector selector) {
        return elementFinder.findElement(selector);
    }

    @Nullable
    public AndroidElement findElement(@NonNull final AccessibilityNodeInfo nodeInfo) {
        return elementFinder.findElement(nodeInfo);
    }

    public List<AndroidElement> findElements(@NonNull final BySelector selector) {
        return elementFinder.findElements(selector);
    }

    public List<AndroidElement> findElements(@NonNull final UiSelector selector) {
        return elementFinder.findElements(selector);
    }

    public List<AndroidElement> findElements(@NonNull final AccessibilityNodeInfoList nodeList) {
        return elementFinder.findElements(nodeList);
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

    public Boolean injectEventSync(@NonNull final InputEvent event) {
        return interactionControllerAdapter.injectEventSync(event);
    }

    public boolean openNotification() {
        return uiDeviceAdapter.openNotification();
    }

    @Nullable
    public Boolean performMultiPointerGesture(@NonNull final MotionEvent.PointerCoords[][] pcs) {
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

    public Boolean runAndRegisterScrollEvents(@NonNull final ReturningRunnable<Boolean> returningRunnable) {
        return eventRegister.runAndRegisterScrollEvents(returningRunnable);
    }

    public void scrollTo(@NonNull final String scrollToString) throws UiObjectNotFoundException {
        uiDeviceAdapter.scrollTo(scrollToString);
    }

    public void setCompressedLayoutHeirarchy(@NonNull final Boolean compressLayout) {
        uiDeviceAdapter.setCompressedLayoutHeirarchy(compressLayout);
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

    public boolean touchDown(final int x, final int y) {
        return interactionControllerAdapter.touchDown(x, y);
    }

    public boolean touchMove(final int x, final int y) {
        return interactionControllerAdapter.touchMove(x, y);
    }

    public boolean touchUp(final int x, final int y) {
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
}
