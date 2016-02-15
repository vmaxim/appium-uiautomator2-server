package io.appium.uiautomator2.util;

public class Log {
    public static final String TAG = "appium";

    private static String getString(Object... args) {
        StringBuilder content = new StringBuilder();

        for (Object arg : args) {
            if (arg != null) {
                content.append(arg.toString());
            }
        }

        return content.toString();
    }

    /**
     * Log error
     */
    public static void e(Object... messages) {
        android.util.Log.e(TAG, getString(messages));
    }

    /**
     * Log info
     */
    public static void i(Object... messages) {
        android.util.Log.i(TAG, getString(messages));
    }
}
