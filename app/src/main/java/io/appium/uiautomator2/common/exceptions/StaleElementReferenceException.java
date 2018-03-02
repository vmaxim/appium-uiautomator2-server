package io.appium.uiautomator2.common.exceptions;

import io.appium.uiautomator2.model.AndroidElement;

public class StaleElementReferenceException extends Exception {
    private static final long serialVersionUID = -5835005031770654071L;
    private final String id;

    public StaleElementReferenceException(AndroidElement androidElement) {
        super();
        this.id = androidElement.getId();
    }

    public StaleElementReferenceException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
//    public StaleElementReferenceException(String message) {
//        super(message);
//    }
//
//    public StaleElementReferenceException(Throwable t) {
//        super(t);
//    }
//
//    public StaleElementReferenceException(String message, Throwable t) {
//        super(message, t);
//    }
}
