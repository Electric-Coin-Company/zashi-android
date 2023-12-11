package co.electriccoin.zcash.ui.benchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import org.junit.Rule
import org.junit.Test

/**
 * This is an example startup benchmark. Its purpose is to provide basic  startup measurements, and captured
 * system traces for investigating the app's performance.
 *
 * It navigates to the device's home screen, and launches the default activity. Run this benchmark from Studio only
 * against the release variant type and use one of the 'Benchmark' trailing build variants for this module.
 * The 'Debug' trailing modules are also available, but they just provide compatibility with other debug modules.
 *
 * We ideally run this against a physical device with Android SDK level 29, at least, as profiling is provided by this
 * version and later on.
 */
class BasicStartupBenchmark {
    companion object {
        private const val APP_TARGET_PACKAGE_NAME = "co.electriccoin.zcash"
    }

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() =
        benchmarkRule.measureRepeated(
            packageName = APP_TARGET_PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.COLD
        ) {
            pressHome()
            startActivityAndWait()
        }
}
