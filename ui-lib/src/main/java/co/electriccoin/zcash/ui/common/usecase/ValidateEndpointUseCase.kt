package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.lightwallet.client.model.LightWalletEndpoint

class ValidateEndpointUseCase {
    operator fun invoke(endpoint: String): LightWalletEndpoint? {
        val valid = ENDPOINT_REGEX.toRegex().matches(endpoint)

        return if (valid) {
            endpoint.toEndpoint(":")
        } else {
            null
        }
    }

    private fun String.toEndpoint(delimiter: String): LightWalletEndpoint {
        val parts = split(delimiter)
        return LightWalletEndpoint(parts[0], parts[1].toInt(), true)
    }
}

private const val ENDPOINT_REGEX = "^(([^:/?#\\s]+)://)?([^/?#\\s]+):([1-9][0-9]{3}|[1-5][0-9]{2}|[0-9]{1,2})$"
