package co.electriccoin.zcash.ui.common.provider

import android.util.Log
import co.electriccoin.zcash.ui.common.model.near.ErrorDto
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException

interface HttpClientProvider {
    suspend fun create(): HttpClient
}

class HttpClientProviderImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val isTorEnabledStorageProvider: IsTorEnabledStorageProvider
) : HttpClientProvider {
    override suspend fun create(): HttpClient =
        if (isTorEnabledStorageProvider.get() == true) createTor() else createDefault()

    private suspend fun createTor() =
        synchronizerProvider
            .getSynchronizer()
            .getTorHttpClient {
                configureHttpClient()
                install(HttpCallValidator) {
                    handleResponseExceptionWithRequest { exception, _ ->
                        if (exception is ResponseException) {
                            val response = exception.response
                            val error: ErrorDto? = runCatching { response.body<ErrorDto?>() }.getOrNull()
                            if (error != null) {
                                throw ResponseWithErrorException(
                                    response = response,
                                    cachedResponseText = "Code: ${response.status}, message: ${error.message}",
                                    error = error
                                )
                            }
                        } else if (exception is RuntimeException) {
                            throw IOException(exception.message, exception)
                        }
                    }
                }
            }

    private fun createDefault() =
        HttpClient(OkHttp) {
            configureHttpClient()
            install(HttpCallValidator) {
                handleResponseExceptionWithRequest { exception, _ ->
                    if (exception is ResponseException) {
                        val response = exception.response
                        val error: ErrorDto? = runCatching { response.body<ErrorDto?>() }.getOrNull()
                        if (error != null) {
                            throw ResponseWithErrorException(
                                response = response,
                                cachedResponseText = "Code: ${response.status}, message: ${error.message}",
                                error = error
                            )
                        }
                    }
                }
            }
        }

    private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.configureHttpClient() {
        install(ContentNegotiation) { json() }
        install(Logging) {
            logger = KtorLogger()
            level = LogLevel.ALL
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        expectSuccess = true
    }
}

private class KtorLogger : Logger {
    override fun log(message: String) {
        Log.d("HttpClient", message)
    }
}
