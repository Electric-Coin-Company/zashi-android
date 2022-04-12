package co.electriccoin.zcash.ui.screen.support.util

import androidx.test.filters.SmallTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailUtilTest {
    companion object {
        const val RECIPIENT = "foo@bar.com" // $NON-NLS
        const val SUBJECT = "ohai there" // $NON-NLS
        const val BODY = "i can haz cheezburger" // $NON-NLS
    }

    @Test
    @SmallTest
    fun newMailToUriString() {
        val actual = EmailUtil.newMailToUriString(RECIPIENT, SUBJECT, BODY)
        val expected = "mailto:foo@bar.com?subject=ohai%20there&body=i%20can%20haz%20cheezburger"
        assertEquals(expected, actual)
    }
}
