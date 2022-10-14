package co.electriccoin.zcash.ui.common

import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class FlowExtTest {

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    @Test
    @SmallTest
    fun throttle_one_sec() = runTest {
        val timer = TimeSource.Monotonic.markNow()
        val flow = flow {
            while (timer.elapsedNow() <= 5.seconds) {
                emit(1)
            }
        }.throttle(1.seconds)

        var timeMark: TimeMark? = null
        flow.collect {
            if (timeMark == null) {
                timeMark = TimeSource.Monotonic.markNow()
            } else {
                assert(timeMark!!.elapsedNow() >= 1.seconds)
                timeMark = TimeSource.Monotonic.markNow()
            }
        }
    }
}
