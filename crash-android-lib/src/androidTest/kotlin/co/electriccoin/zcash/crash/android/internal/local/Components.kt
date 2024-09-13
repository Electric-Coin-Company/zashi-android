package co.electriccoin.zcash.crash.android.internal.local

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.spackle.AndroidApiVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Components {
    @Test
    @SmallTest
    fun process_names() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val pm = ApplicationProvider.getApplicationContext<Context>().packageManager
        val providerInfo = pm.getProviderInfoCompat(ComponentName(context, CrashProcessNameContentProvider::class.java))
        val receiverInfo = pm.getReceiverInfoCompat(ComponentName(context, ExceptionReceiver::class.java))

        assertEquals(providerInfo.processName, receiverInfo.processName)
        assertTrue(providerInfo.processName.endsWith(GlobalCrashReporter.CRASH_PROCESS_NAME_SUFFIX))
    }
}

private fun PackageManager.getProviderInfoCompat(componentName: ComponentName) =
    if (AndroidApiVersion.isAtLeastTiramisu) {
        getProviderInfo(componentName, PackageManager.ComponentInfoFlags.of(0))
    } else {
        @Suppress("Deprecation")
        getProviderInfo(componentName, 0)
    }

private fun PackageManager.getReceiverInfoCompat(componentName: ComponentName) =
    if (AndroidApiVersion.isAtLeastTiramisu) {
        getReceiverInfo(componentName, PackageManager.ComponentInfoFlags.of(0))
    } else {
        @Suppress("Deprecation")
        getReceiverInfo(componentName, 0)
    }
