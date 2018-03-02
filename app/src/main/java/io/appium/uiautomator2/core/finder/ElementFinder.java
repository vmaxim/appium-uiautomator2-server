package io.appium.uiautomator2.core.finder;

import android.support.annotation.NonNull;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiSelector;

import io.appium.uiautomator2.common.exceptions.ElementNotFoundException;
import io.appium.uiautomator2.model.AndroidElement;

public class ElementFinder {

    private final BySelectorFinder bySelectorFinder;
    private final UiSelectorFinder uiSelectorFinder;

    public ElementFinder(@NonNull final BySelectorFinder bySelectorFinder,
                         @NonNull final UiSelectorFinder uiSelectorFinder) {
        this.bySelectorFinder = bySelectorFinder;
        this.uiSelectorFinder = uiSelectorFinder;
    }

    public AndroidElement findElement(@NonNull final BySelector selector) throws ElementNotFoundException {
        return bySelectorFinder.findElement(selector);
    }

    public AndroidElement findElement(@NonNull final UiSelector selector) throws ElementNotFoundException {
        return uiSelectorFinder.findElement(selector);
    }
}
