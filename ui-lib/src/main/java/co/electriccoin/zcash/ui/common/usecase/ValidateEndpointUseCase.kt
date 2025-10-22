package co.electriccoin.zcash.ui.common.usecase

import androidx.core.net.toUri
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint

class ValidateEndpointUseCase {
    @Suppress("ComplexCondition", "TooGenericExceptionCaught", "ReturnCount", "MagicNumber")
    operator fun invoke(endpoint: String): LightWalletEndpoint? {
        return try {
            // First validate regex
            if (!ENDPOINT_REGEX.toRegex().matches(endpoint)) return null

            // Parse using Android's Uri class for robust URL parsing
            val uri =
                if (endpoint.contains("://")) {
                    endpoint.toUri()
                } else {
                    // Handle endpoints without protocol scheme
                    "https://$endpoint".toUri()
                }

            val host = uri.host
            val port = uri.port

            // Validate hostname - reject empty, leading/trailing periods
            if (host.isNullOrBlank() ||
                host.startsWith(".") ||
                host.endsWith(".") ||
                host.contains("..")
            ) {
                return null
            }

            // Validate port range
            if (port !in 1..65535) {
                return null
            }

            LightWalletEndpoint(host, port, true)
        } catch (_: Exception) {
            // Catch any parsing errors and return null instead of crashing
            null
        }
    }
}

@Suppress("MaxLineLength", "ktlint:standard:max-line-length")
private const val ENDPOINT_REGEX = "^(([^:/?#\\s]+)://)?([^/?#\\s.][^/?#\\s]*[^/?#\\s.]):([1-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$"
