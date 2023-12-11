package co.electriccoin.zcash.ui.integration.test.common

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun getStringResource(
    @StringRes resId: Int
) = ApplicationProvider.getApplicationContext<Context>().getString(resId)

fun getStringResourceWithArgs(
    @StringRes resId: Int,
    vararg formatArgs: String
) = ApplicationProvider.getApplicationContext<Context>().getString(resId, *formatArgs)

// We're using indexes to find the right button, as it seems to be the best available way to test a click
// action on a permission button. These indexes remain the same for LTR as well as RTL layout direction.
fun getPermissionNegativeButtonUiObject(): UiObject? {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    return UiDevice.getInstance(instrumentation).findObject(
        UiSelector()
            .className("android.widget.Button") // $NON-NLS
            .index(
                // tested up to version 33
                when {
                    Build.VERSION.SDK_INT <= 28 -> 0
                    Build.VERSION.SDK_INT == 29 -> 1
                    Build.VERSION.SDK_INT >= 30 -> 2
                    else -> 2
                }
            )
    )
}

fun getPermissionPositiveButtonUiObject(): UiObject? {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    return UiDevice.getInstance(instrumentation).findObject(
        UiSelector()
            .className("android.widget.Button") // $NON-NLS
            .index(
                // tested up to version 33
                when {
                    Build.VERSION.SDK_INT <= 28 -> 1
                    Build.VERSION.SDK_INT >= 29 -> 0
                    else -> 0
                }
            )
    )
}

fun waitForDeviceIdle(timeout: Duration = 1000.milliseconds) {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    UiDevice.getInstance(instrumentation).waitForWindowUpdate(
        ApplicationProvider.getApplicationContext<Context>().packageName,
        timeout.inWholeMilliseconds
    )
}
