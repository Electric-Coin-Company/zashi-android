package co.electriccoin.zcash.spackle.process

import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.ProviderInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AbstractProcessNameContentProviderTest {
    @Test
    @SmallTest
    fun getProcessName_from_provider_info() {
        val expectedApplicationProcessName = "beep" // $NON-NLS
        val ctx: ContextWrapper =
            object : ContextWrapper(ApplicationProvider.getApplicationContext()) {
                override fun getApplicationInfo() =
                    ApplicationInfo().apply {
                        processName = expectedApplicationProcessName
                    }
            }

        val actualProcessName =
            AbstractProcessNameContentProvider.getProcessNameLegacy(
                ctx,
                ProviderInfo()
            )

        assertEquals(expectedApplicationProcessName, actualProcessName)
    }
}
