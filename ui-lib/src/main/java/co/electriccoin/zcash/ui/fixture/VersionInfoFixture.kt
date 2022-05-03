package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.screen.about.model.VersionInfo

// Magic Number doesn't matter here for hard-coded fixture values
@Suppress("MagicNumber")
object VersionInfoFixture {
    const val VERSION_NAME = "1.0.3"
    const val VERSION_CODE = 3L

    fun new(
        versionName: String = VERSION_NAME,
        versionCode: Long = VERSION_CODE
    ) = VersionInfo(versionName, versionCode)
}
