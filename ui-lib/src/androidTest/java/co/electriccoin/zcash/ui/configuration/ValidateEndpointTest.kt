package co.electriccoin.zcash.ui.configuration
import androidx.test.filters.SmallTest
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
/**
 * Comprehensive tests for ValidateEndpointUseCase functionality.
 *
 * Tests the robust URL parsing implementation that handles protocol schemes,
 * invalid hostnames, and full port range validation without crashing.
 */
class ValidateEndpointTest {
    private val useCase = ValidateEndpointUseCase()
    @Test
    @SmallTest
    fun EndpointWithProtocolSchemeTest() {
        // Expected behavior: Should parse successfully and return valid endpoint
        val result = useCase("https://zec.rocks:443")
        // Should successfully parse the endpoint, extracting host and port
        assertThat("Protocol scheme should be handled gracefully", result, notNullValue())
        assertThat("Hostname should be extracted correctly", result?.host, equalTo("zec.rocks"))
        assertThat("Port should be extracted correctly", result?.port, equalTo(443))
        assertThat("Should default to secure connection", result?.isSecure, equalTo(true))
    }
    @Test
    @SmallTest
    fun EndpointWithHttpProtocolTest() {
        val result = useCase("http://mainnet.lightwalletd.com:9067")
        // Should successfully parse the endpoint, extracting host and port
        assertThat("HTTP protocol scheme should be handled gracefully", result, notNullValue())
        assertThat("Hostname should be extracted correctly", result?.host, equalTo("mainnet.lightwalletd.com"))
        assertThat("Port should be extracted correctly", result?.port, equalTo(9067))
        assertThat("Should default to secure connection", result?.isSecure, equalTo(true))
    }
    @Test
    @SmallTest
    fun EndpointWithInvalidHostnameLeadingDotTest() {
        // Expected: Should return null (invalid hostname) without crashing
        val result = useCase(".zec.rocks:443")
        assertThat("Invalid hostname with leading dot should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointWithTrailingDotHostnameTest() {
        // Expected: Should return null (invalid hostname) without crashing
        val result = useCase("zec.rocks.:443")
        assertThat("Invalid hostname with trailing dot should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointValidStandardFormatTest() {
        val result = useCase("mainnet.lightwalletd.com:9067")
        assertThat("Valid endpoint should not be null", result, notNullValue())
        assertThat("Hostname should be correct", result?.host, equalTo("mainnet.lightwalletd.com"))
        assertThat("Port should be correct", result?.port, equalTo(9067))
        assertThat("Secure should be true by default", result?.isSecure, equalTo(true))
    }
    @Test
    @SmallTest
    fun EndpointValidWithSingleDigitPortTest() {
        val result = useCase("example.com:8")
        assertThat("Valid endpoint with single digit port should not be null", result, notNullValue())
        assertThat("Hostname should be correct", result?.host, equalTo("example.com"))
        assertThat("Port should be correct", result?.port, equalTo(8))
    }
    @Test
    @SmallTest
    fun EndpointValidWithMaxPortTest() {
        val result = useCase("example.com:65535")
        assertThat("Port 65535 should be valid (max valid port)", result, notNullValue())
        assertThat("Hostname should be correct", result?.host, equalTo("example.com"))
        assertThat("Port should be correct", result?.port, equalTo(65535))
    }
    @Test
    @SmallTest
    fun EndpointEmptyStringTest() {
        val result = useCase("")
        assertThat("Empty string should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointMissingPortTest() {
        val result = useCase("mainnet.lightwalletd.com")
        assertThat("Endpoint without port should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointMissingHostnameTest() {
        val result = useCase(":9067")
        assertThat("Endpoint without hostname should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointInvalidPortZeroTest() {
        val result = useCase("example.com:0")
        assertThat("Port 0 should be invalid", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointInvalidPortNegativeTest() {
        val result = useCase("example.com:-1")
        assertThat("Negative port should be invalid", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointInvalidPortNonNumericTest() {
        val result = useCase("example.com:abc")
        assertThat("Non-numeric port should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointInvalidPortWithLettersTest() {
        val result = useCase("example.com:80a")
        assertThat("Port with letters should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointMultipleColonsTest() {
        val result = useCase("example.com:80:80")
        assertThat("Multiple colons should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointWithPathTest() {
        val result = useCase("example.com:80/path")
        assertThat("Endpoint with path should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointWithQueryParamsTest() {
        val result = useCase("example.com:80?param=value")
        assertThat("Endpoint with query params should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointWithFragmentTest() {
        val result = useCase("example.com:80#fragment")
        assertThat("Endpoint with fragment should return null", result, nullValue())
    }
    @Test
    @SmallTest
    fun EndpointLocalhostTest() {
        val result = useCase("localhost:9067")
        assertThat("Localhost should be valid", result, notNullValue())
        assertThat("Hostname should be localhost", result?.host, equalTo("localhost"))
        assertThat("Port should be correct", result?.port, equalTo(9067))
    }
    @Test
    @SmallTest
    fun EndpointIpAddressTest() {
        val result = useCase("127.0.0.1:9067")
        assertThat("IP address should be valid", result, notNullValue())
        assertThat("Hostname should be IP address", result?.host, equalTo("127.0.0.1"))
        assertThat("Port should be correct", result?.port, equalTo(9067))
    }
    @Test
    @SmallTest
    fun EndpointWithSubdomainTest() {
        val result = useCase("api.mainnet.lightwalletd.com:9067")
        assertThat("Subdomain should be valid", result, notNullValue())
        assertThat("Hostname should include subdomain", result?.host, equalTo("api.mainnet.lightwalletd.com"))
        assertThat("Port should be correct", result?.port, equalTo(9067))
    }
}
