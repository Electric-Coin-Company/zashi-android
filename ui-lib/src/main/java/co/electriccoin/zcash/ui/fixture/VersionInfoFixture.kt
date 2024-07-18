package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.common.model.Changelog
import co.electriccoin.zcash.ui.common.model.VersionInfo

// Magic Number doesn't matter here for hard-coded fixture values
@Suppress("MagicNumber")
object VersionInfoFixture {
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1L
    const val IS_DEBUGGABLE = false
    const val IS_TESTNET = false
    const val IS_RUNNING_UNDER_TEST_SERVICE = false
    const val GIT_SHA = "635dac0eb9ddc2bc6da5177f0dd495d8b76af4dc"
    const val GIT_COMMIT_COUNT = 1L
    val CHANGELOG = ChangelogFixture.new()

    @Suppress("LongParameterList")
    fun new(
        versionName: String = VERSION_NAME,
        versionCode: Long = VERSION_CODE,
        isDebuggable: Boolean = IS_DEBUGGABLE,
        isRunningUnderTestService: Boolean = IS_RUNNING_UNDER_TEST_SERVICE,
        isTestnet: Boolean = IS_TESTNET,
        gitSha: String = GIT_SHA,
        gitCommitCount: Long = GIT_COMMIT_COUNT,
        changelog: Changelog = CHANGELOG
    ) = VersionInfo(
        versionName = versionName,
        versionCode = versionCode,
        isDebuggable = isDebuggable,
        isRunningUnderTestService = isRunningUnderTestService,
        isTestnet = isTestnet,
        gitSha = gitSha,
        gitCommitCount = gitCommitCount,
        changelog = changelog,
    )
}
