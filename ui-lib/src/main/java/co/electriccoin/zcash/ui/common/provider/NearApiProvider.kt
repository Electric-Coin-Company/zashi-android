package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.NearTokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

typealias GetNearSupportedTokensResponse = List<NearTokenDto>

interface NearApiProvider {
    @Throws(ResponseException::class, IOException::class)
    suspend fun getSupportedTokens(): GetNearSupportedTokensResponse
}

class KtorNearApiProvider : NearApiProvider {
    override suspend fun getSupportedTokens(): GetNearSupportedTokensResponse =
        execute {
            get("https://1click.chaindefuser.com/v0/tokens").body()
        }

    @Suppress("TooGenericExceptionCaught")
    @Throws(ResponseException::class)
    private suspend inline fun <T> execute(
        crossinline block: suspend HttpClient.() -> T
    ): T =
        withContext(Dispatchers.IO) {
            try {
                createHttpClient().use { block(it) }
            } catch (e: ResponseException) {
                throw e
            } catch (e: Exception) {
                throw IOException(e.message, e)
            }
        }

    @Suppress("MagicNumber")
    private fun createHttpClient() =
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json() }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            message.chunked(250).forEach { Twig.debug { it } }
                        }
                    }
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            expectSuccess = true
        }
}
