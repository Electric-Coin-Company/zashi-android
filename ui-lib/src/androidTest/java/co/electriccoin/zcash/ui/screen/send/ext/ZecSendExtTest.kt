package co.electriccoin.zcash.ui.screen.send.ext

import androidx.compose.runtime.saveable.SaverScope
import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.fixture.ZecSendFixture
import cash.z.ecc.sdk.model.ZecSend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ZecSendExtTest {

    @Test
    @SmallTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun round_trip() = runTest {
        val original = ZecSendFixture.new()
        val saved = with(ZecSend.Saver) {
            val allowingScope = SaverScope { true }

            allowingScope.save(original)
        }

        val restored = ZecSend.Saver.restore(saved!!)

        assertEquals(original, restored)
    }

    @Test
    @SmallTest
    fun restore_empty() {
        val restored = ZecSend.Saver.restore(emptyList<Any?>())
        assertEquals(null, restored)
    }
}
