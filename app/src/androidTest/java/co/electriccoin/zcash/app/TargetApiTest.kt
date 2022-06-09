package co.electriccoin.zcash.app

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.Test

class TargetApiTest {

    @Test
    @SmallTest
    fun checkTargetApi() {
        // This test case prevents accidental release of the app targeting a newer API level than
        // we currently support.  Don't change this unless you're absolutely sure we're ready to
        // target the new API level.
        assertThat(
            ApplicationProvider.getApplicationContext<Application>().applicationInfo.targetSdkVersion,
            lessThanOrEqualTo(Build.VERSION_CODES.S_V2)
        )
    }
}
