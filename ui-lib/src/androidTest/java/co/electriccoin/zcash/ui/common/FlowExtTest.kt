package co.electriccoin.zcash.ui.common

import androidx.test.filters.FlakyTest
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.time.Duration
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

    @OptIn(ExperimentalTime::class)
    private fun raceConditionTest(duration: Duration): Boolean = runBlocking {
        val flow = (0..1000).asFlow().throttle(duration)

        val values = mutableListOf<Int>()
        flow.collect {
            values.add(it)
        }

        return@runBlocking values.zipWithNext().all { it.first <= it.second }
    }

    @FlakyTest
    @Test
    fun stressTest() = runBlocking {
        repeat(10) {
            assertTrue { raceConditionTest(0.001.seconds) }
        }
        repeat(10) {
            assertTrue { raceConditionTest(0.0001.seconds) }
        }
        repeat(10) {
            assertTrue { raceConditionTest(0.00001.seconds) }
        }
    }
}
