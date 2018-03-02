package io.appium.uiautomator2.core;

import android.support.annotation.Nullable;

public abstract class ReturningRunnable<T> implements Runnable {

    @Nullable
    private T result;

    public ReturningRunnable() {
        result = null;
    }

    @Nullable
    public T getResult() {
        return result;
    }

    protected void setResult(@Nullable T value) {
        result = value;
    }
}