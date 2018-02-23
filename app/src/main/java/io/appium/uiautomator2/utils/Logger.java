package io.appium.uiautomator2.utils;

import java.util.Locale;

public class Logger {

    private static final String TAG = "appium";

    /**
     * Logger error
     */
    public static void error(String message, Object... params) {
        android.util.Log.e(TAG, formatString(message, params));
    }

    /**
     * Logger error
     */
    public static void error(String message, Throwable throwable) {
        android.util.Log.e(TAG, message, throwable);
    }

    /**
     * Logger info
     */
    public static void info(String message, Object...params) {
        android.util.Log.i(TAG, formatString(message, params));
    }

    /**
     * Logger debug
     */
    public static void debug(String message, Object... params) {
        android.util.Log.d(TAG, formatString(message, params));
    }

    private static String formatString(String message, Object...params) {
        if (params.length == 0) {
            return message;
        }
        return String.format(Locale.getDefault(), message, params);
    }
}
