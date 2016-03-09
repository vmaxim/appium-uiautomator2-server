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

import io.appium.uiautomator2.http.HttpServer;
import io.appium.uiautomator2.util.Log;


public class AndroidServer {
    private int driverPort;
    private HttpServer webServer;

    public AndroidServer(int port) {
        driverPort = port;
        webServer = new HttpServer(driverPort);
        init();
        Log.i("AndroidServer created on port " + port);
    }

    protected void init() {
        webServer.addHandler(new AppiumServlet());
    }

    public void start() {
        webServer.start();
    }

    public void stop() {
        webServer.stop();
    }

    public int getPort() {
        return webServer.getPort();
    }
}
