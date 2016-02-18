### appium-uiautmator2-server

A netty server that runs on the device lisening for commands.

### Starting server
build the android project using the commands 
`gradle clean assembleDebug`
`gradle clean assembleDebugAndroidTest`

push both src and test apks to the device and execute the instrumentation tests.

`adb shell am instrument -w io.appium.uiautomator2.test/android.support.test.runner.AndroidJUnitRunner`
