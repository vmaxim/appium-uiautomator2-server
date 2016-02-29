package io.appium.uiautomator2.model;

import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

public class AndroidElement {
    private UiObject2 element;
    private String id;

    public AndroidElement(String id, UiObject2 element){
        this.id = id;
        this.element = element;
    }

    public void click() throws UiObjectNotFoundException {
        element.click();
    }

    public String getId(){
        return this.id;
    }
}
