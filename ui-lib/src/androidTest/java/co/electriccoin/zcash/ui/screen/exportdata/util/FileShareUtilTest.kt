package co.electriccoin.zcash.ui.screen.exportdata.util

import android.content.Intent
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.test.getAppContext
import co.electriccoin.zcash.ui.util.FileShareUtil
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.io.path.pathString
import kotlin.test.Ignore
import kotlin.test.assertContains

class FileShareUtilTest {
    // TODO [#1034]: Finish disabled FileShareUtilTest
    // TODO [#1034]: https://github.com/Electric-Coin-Company/zashi-android/issues/1034
    @Ignore("Temporary file permission is not correctly set")
    @Test
    @SmallTest
    fun check_intent_for_private_data_file_sharing() {
        val tempFilePath =
            kotlin.io.path.createTempFile(
                directory = getAppContext().cacheDir.toPath(),
                suffix = ".sqlite3"
            )
        val intent =
            FileShareUtil.newShareContentIntent(
                context = getAppContext(),
                dataFilePath = tempFilePath.pathString,
                fileType = FileShareUtil.ZASHI_INTERNAL_DATA_MIME_TYPE,
                shareText = null,
                sharePickerText = "Test Picker Title",
                versionInfo = VersionInfoFixture.new()
            )
        assertEquals(intent.action, Intent.ACTION_VIEW)
        assertEquals(
            FileShareUtil.SHARE_OUTSIDE_THE_APP_FLAGS or FileShareUtil.SHARE_CONTENT_PERMISSION_FLAGS,
            intent.flags
        )
        assertContains(FileShareUtil.ZASHI_INTERNAL_DATA_AUTHORITY, intent.data.toString())
    }
}
