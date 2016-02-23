package io.appium.uiautomator2.model;

import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

public class AndroidElement {
    private UiObject2 uiObject2;

    public AndroidElement(UiObject2 uiObject2){
        this.uiObject2 = uiObject2;
    }

    public void click() throws UiObjectNotFoundException {
        uiObject2.click();
    }
}
