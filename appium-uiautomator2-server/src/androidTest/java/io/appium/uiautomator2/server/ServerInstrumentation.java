/*
 * Copyright 2012-2014 eBay Software Foundation and selendroid committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.appium.uiautomator2.server;

import android.content.Context;
import android.os.Looper;
import android.os.PowerManager;

import io.appium.uiautomator2.util.Log;

import static io.appium.uiautomator2.util.Log.e;

public class ServerInstrumentation {
    private static ServerInstrumentation instance = null;
    private static Context context = null;
    private HttpdThread serverThread = null;
    private PowerManager.WakeLock wakeLock;
    private int serverPort = 8080;

    private ServerInstrumentation(int serverPort) {
        this.serverPort = serverPort;

        if (!isValidPort(serverPort)) {
            throw new RuntimeException(("Invalid port: " + serverPort));
        }
    }

    private static boolean isValidPort(int port) {
        return port >= 1024 && port <= 65535;
    }

    public static synchronized ServerInstrumentation getInstance(Context activityContext, int serverPort) {
        if (instance == null) {
            context = activityContext;
            instance = new ServerInstrumentation(serverPort);
        }
        return instance;
    }

    public void stopServer() {
        try {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
            stopServerThread();
        } catch (Exception e) {
            e("Error shutting down: ", e);
        }
        instance = null;
    }


    public void startServer() {
        if (serverThread != null && serverThread.isAlive()) {
            return;
        }

        if (serverThread != null) {
            Log.e("Stopping selendroid http server");
            stopServer();
        }

        serverThread = new HttpdThread(this, this.serverPort);
        serverThread.start();
    }

    private void stopServerThread() {
        if (serverThread == null) {
            return;
        }
        if (!serverThread.isAlive()) {
            serverThread = null;
            return;
        }

        e("Stopping selendroid http server");
        serverThread.stopLooping();
        serverThread.interrupt();
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        serverThread = null;
    }

    private class HttpdThread extends Thread {

        private final AndroidServer server;
        private ServerInstrumentation instrumentation = null;
        private Looper looper;

        public HttpdThread(ServerInstrumentation instrumentation, int serverPort) {
            this.instrumentation = instrumentation;
            // Create the server but absolutely do not start it here
            server = new AndroidServer(serverPort);
        }

        @Override
        public void run() {
            Looper.prepare();
            looper = Looper.myLooper();
            startServer();
            Looper.loop();
        }

        public AndroidServer getServer() {
            return server;
        }

        private void startServer() {
            try {
                // Get a wake lock to stop the cpu going to sleep
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Selendroid");
                try {
                    wakeLock.acquire();
                } catch (SecurityException e) {
                }

                server.start();

                Log.i("Started selendroid http server on port " + server.getPort());
            } catch (Exception e) {
                Log.e("Error starting httpd.", e);

                throw new RuntimeException("Httpd failed to start!");
            }
        }

        public void stopLooping() {
            if (looper == null) {
                return;
            }
            looper.quit();
        }
    }
}
