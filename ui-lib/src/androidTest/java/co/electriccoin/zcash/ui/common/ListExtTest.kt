package co.electriccoin.zcash.ui.common

import androidx.test.filters.SmallTest
import co.electriccoin.zcash.ui.common.extension.first
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.Test

class ListExtTest {
    @Test
    @SmallTest
    fun first_under() {
        val limited = listOf(1, 2, 3).first(2)

        assertThat(limited.count(), equalTo(2))
        assertThat(limited, contains(1, 2))
    }

    @Test
    @SmallTest
    fun first_equal() {
        val limited = listOf(1, 2, 3).first(3)

        assertThat(limited.count(), equalTo(3))
        assertThat(limited, contains(1, 2, 3))
    }

    @Test
    @SmallTest
    fun first_over() {
        val limited = listOf(1, 2, 3).first(5)

        assertThat(limited.count(), equalTo(3))
        assertThat(limited, contains(1, 2, 3))
    }
}
