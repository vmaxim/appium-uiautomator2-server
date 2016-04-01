package io.appium.uiautomator2.unittest.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.okhttp.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.util.Log;

import static io.appium.uiautomator2.unittest.test.TestHelper.post;

/**
 * Created by sravanm on 17-03-2016.
 */
@RunWith(AndroidJUnit4.class)

public class RoutesTest {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int port = 8080;
    private static final int WAIT_FOR_SERVER = 5000;
    private static final String testAppPkg = "io.selendroid.testapp";
    private static final String baseUri = "/wd/hub/session/:sessionId/element";
    private static ServerInstrumentation serverInstrumentation = null;
    private static boolean isStopServer = false;
    private Context ctx = null;

    /**
     * start io.appium.uiautomator2.server and launch the application main activity     *
     *
     * @throws InterruptedException
     */
    @Before
    public void startServer() throws InterruptedException {
        if (serverInstrumentation == null) {
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
            serverInstrumentation = ServerInstrumentation.getInstance(ctx, port);
            Log.i("[AppiumUiAutomator2Server]", " Starting Server ");
            serverInstrumentation.startServer();
            Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(testAppPkg);
            ctx.startActivity(intent);
            Thread.sleep(WAIT_FOR_SERVER);
        }
    }

    /**
     * find element
     */
    @Test
    public void findElement() {
        Log.i("[AppiumUiAutomator2Server]", " find element");
        Log.i(post(baseUri, "{\"using\":\"name\",\"value\":\"Display Popup Window\"}"));
    }

    /**
     * click on element      *
     *
     * @throws JSONException
     */
    @Test
    public void clickElement() throws JSONException {
        Log.i("[AppiumUiAutomator2Server]", " click element");
        String elementId = new JSONObject(post(baseUri, "{\"using\":\"name\",\"value\":\"Display Popup Window\"}"))
                .getJSONObject("value").getString("ELEMENT");
        post(baseUri + elementId + "/click", "{\"id\": \"" + elementId + "\"}");
    }
}
