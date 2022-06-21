package co.electriccoin.zcash.spackle.process

import android.content.pm.PackageInfo
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.spackle.AndroidApiVersion
import co.electriccoin.zcash.spackle.versionCodeCompat
import org.junit.Assert.assertEquals
import org.junit.Test

class VersionCodeCompatTest {
    @Test
    @SmallTest
    fun versionCodeCompat() {
        val expectedVersionCode = 123L

        val packageInfo = PackageInfo().apply {
            @Suppress("Deprecation")
            versionCode = expectedVersionCode.toInt()
            if (AndroidApiVersion.isAtLeastT) {
                longVersionCode = expectedVersionCode
            }
        }

        assertEquals(expectedVersionCode, packageInfo.versionCodeCompat)
    }
}
