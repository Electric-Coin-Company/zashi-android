package cash.z.ecc.sdk.model

import androidx.test.filters.SmallTest
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.fixture.Zip321UriBuildFixture
import cash.z.ecc.sdk.fixture.Zip321UriParseFixture
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ZecRequestTest {
    companion object {
        private const val URI: String = "zcash:tmXuTnE11JojToagTqxXUn6KvdxDE3iLKbp?amount=1&message=Hello%20world!"

        @Suppress("MagicNumber")
        private val AMOUNT = Zatoshi(1)
        private val MESSAGE = ZecRequestMessage("Hello world!")
        private const val ADDRESS_STRING = "tmXuTnE11JojToagTqxXUn6KvdxDE3iLKbp"
        private val ADDRESS: WalletAddress.Unified =
            runBlocking {
                WalletAddress.Unified.new(ADDRESS_STRING)
            }
        val REQUEST = ZecRequest(ADDRESS, AMOUNT, MESSAGE)
    }

    @Test
    @SmallTest
    fun parse_uri_not_null() =
        runTest {
            val parsed = ZecRequest.fromUri(Zip321UriParseFixture.URI)

            assertNotNull(parsed)
        }

    @Test
    @SmallTest
    fun parse_uri_valid_result() =
        runTest {
            val parsed = ZecRequest.fromUri(Zip321UriParseFixture.URI)

            assertTrue(parsed.message.value.length <= ZecRequestMessage.MAX_MESSAGE_LENGTH)
            assertTrue(parsed.address.address.isNotEmpty())
            assertTrue(parsed.amount.value >= 0)
        }

    @Test
    @SmallTest
    fun parse_uri_correct_result() =
        runTest {
            val parsed = ZecRequest.fromUri(Zip321UriParseFixture.URI)
            val expected =
                ZecRequest(
                    WalletAddress.Unified.new(Zip321UriParseFixture.ADDRESS),
                    Zip321UriParseFixture.AMOUNT,
                    Zip321UriParseFixture.MESSAGE
                )

            assertEquals(parsed, expected)
        }

    @Test
    @SmallTest
    // TODO [#397]: Waiting for an implementation of Uri parser in SDK project
    @Ignore("Waiting for an implementation of Uri parser in SDK project")
    fun parse_uri_incorrect_result() =
        runTest {
            val parsed = ZecRequest.fromUri(URI)
            val expected = REQUEST
            val actual =
                ZecRequest(
                    WalletAddress.Unified.new(Zip321UriParseFixture.ADDRESS),
                    Zip321UriParseFixture.AMOUNT,
                    Zip321UriParseFixture.MESSAGE
                )

            assertNotEquals(parsed, expected)
            assertEquals(parsed, actual)
        }

    @Test
    @SmallTest
    fun build_uri_not_null() =
        runTest {
            val request = Zip321UriBuildFixture.REQUEST
            val built = request.toUri()

            assertNotNull(built)
        }

    @Test
    @SmallTest
    fun build_uri_valid_result() =
        runTest {
            val request = Zip321UriBuildFixture.REQUEST
            val built = request.toUri()

            assertTrue(built.isNotEmpty())
            assertTrue(built.startsWith("zcash"))
        }

    @Test
    @SmallTest
    fun built_uri_correct_result() =
        runTest {
            val request = Zip321UriBuildFixture.REQUEST
            val built = request.toUri()
            val expected = Zip321UriBuildFixture.URI

            assertEquals(built, expected)
        }

    @Test
    @SmallTest
    // TODO [#397]: Waiting for an implementation of Uri parser in SDK project
    @Ignore("Waiting for an implementation of Uri parser in SDK project")
    fun build_uri_incorrect_result() =
        runTest {
            val request = Zip321UriBuildFixture.REQUEST
            val built = request.toUri()
            val expected = URI
            val actual = Zip321UriBuildFixture.URI

            assertNotEquals(built, expected)
            assertEquals(built, actual)
        }
}
