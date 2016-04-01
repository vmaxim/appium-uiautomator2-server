package io.appium.uiautomator2.server.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.util.Log;

@RunWith(AndroidJUnit4.class)
public class AppiumUiAutomator2Server {

    private static final int port = 8080;
    private static ServerInstrumentation serverInstrumentation = null;
    private Context ctx = null;
    private static boolean isStopServer = false;

    @Test
    public void startServer() throws InterruptedException {
        if (serverInstrumentation == null) {
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
            serverInstrumentation = ServerInstrumentation.getInstance(ctx, port);
            Log.i("[AppiumUiAutomator2Server]", " Starting Server");
            while (!isStopServer) {
                serverInstrumentation.startServer();
            }
        }
    }

    public static void isStopServer(boolean stopServer) {
        isStopServer = stopServer;
    }
}
