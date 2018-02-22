package io.appium.uiautomator2.core;

import android.support.annotation.Nullable;

public abstract class ReturningRunnable<T> implements Runnable {

    @Nullable
    public T result;

    public ReturningRunnable() {
        result = null;
    }

    @Nullable
    public T getResult() {
        return result;
    }

    protected void setResult(T value) {
        result = value;
    }
}