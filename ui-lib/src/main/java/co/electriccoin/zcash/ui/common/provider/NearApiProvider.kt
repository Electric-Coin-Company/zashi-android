package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.near.ErrorDto
import co.electriccoin.zcash.ui.common.model.near.NearTokenDto
import co.electriccoin.zcash.ui.common.model.near.QuoteRequest
import co.electriccoin.zcash.ui.common.model.near.QuoteResponseDto
import co.electriccoin.zcash.ui.common.model.near.SubmitDepositTransactionRequest
import co.electriccoin.zcash.ui.common.model.near.SwapStatusResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias GetNearSupportedTokensResponse = List<NearTokenDto>

interface NearApiProvider {
    @Throws(ResponseException::class, ResponseWithNearErrorException::class)
    suspend fun getSupportedTokens(): GetNearSupportedTokensResponse

    @Throws(ResponseException::class, ResponseWithNearErrorException::class)
    suspend fun requestQuote(request: QuoteRequest): QuoteResponseDto

    @Throws(ResponseException::class, ResponseWithNearErrorException::class)
    suspend fun submitDepositTransaction(request: SubmitDepositTransactionRequest)

    @Throws(ResponseException::class, ResponseWithNearErrorException::class)
    suspend fun checkSwapStatus(depositAddress: String): SwapStatusResponseDto
}

class ResponseWithNearErrorException(
    val error: ErrorDto,
    override val cause: ResponseException
) : ResponseException(
        response = cause.response,
        cachedResponseText = "Code: ${cause.response.status}, message: ${error.message}"
    )

class KtorNearApiProvider(
    private val httpClientProvider: HttpClientProvider
) : NearApiProvider {
    override suspend fun getSupportedTokens(): GetNearSupportedTokensResponse =
        execute {
            get("https://1click.chaindefuser.com/v0/tokens").body()
        }

    override suspend fun requestQuote(request: QuoteRequest): QuoteResponseDto =
        execute {
            post("https://1click.chaindefuser.com/v0/quote") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, AUTH_TOKEN)
                setBody(request)
            }.body()
        }

    override suspend fun submitDepositTransaction(request: SubmitDepositTransactionRequest) {
        execute {
            post("https://1click.chaindefuser.com/v0/deposit/submit") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, AUTH_TOKEN)
                setBody(request)
            }
        }
    }

    override suspend fun checkSwapStatus(depositAddress: String): SwapStatusResponseDto =
        execute {
            get("https://1click.chaindefuser.com/v0/status") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, AUTH_TOKEN)
                parameter("depositAddress", depositAddress)
            }.body()
        }

    @Suppress("TooGenericExceptionCaught")
    @Throws(ResponseException::class)
    private suspend inline fun <T> execute(
        crossinline block: suspend HttpClient.() -> T
    ): T =
        withContext(Dispatchers.IO) {
            httpClientProvider.create().use {
                try {
                    block(it)
                } catch (e: ResponseException) {
                    val response = e.response
                    val error: ErrorDto? = runCatching { response.body<ErrorDto?>() }.getOrNull()
                    if (error != null) {
                        throw ResponseWithNearErrorException(error = error, cause = e)
                    } else {
                        throw e
                    }
                }
            }
        }
}

private const val AUTH_TOKEN =
    "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjIwMjUtMDQtMjMtdjEifQ.eyJ2IjoxLCJrZXlfdHlwZS" +
        "I6ImRpc3RyaWJ1dGlvbl9jaGFubmVsIiwicGFydG5lcl9pZCI6ImVsZWN0cmljY29pbiIsImlhdCI6MTc1NTE5MDc0MywiZXhwIjoxNzg2Nz" +
        "I2NzQzfQ.LHhSp459njnOoyCssprT4Rc-J4TqlPo6qCcKy0A5npuc3A5iHl-zZ-qua_XroN9ZmU8HxeE4y0qVDeBMQgrzwdV3EybkfXTuSaH" +
        "I8D4BwbAvkZgYMGqdlCpVFMU4g1uWZSZr2jZiQMkaGm5FxkLsO9bf1g38v-IkT6pEgLYM37kd5K5j4vEv2OC8Qs0dOCPvrnbP_t83ef4ldvJ" +
        "7fDYlN9faLudHx-BU_FV5vMgMab8yZE_mpYtRNFRAKcSFgIqHlcUdxFZ_nM7yvt6aXoVHbiO9Z8XwhN24ADjnaDtNJ-Jp_z9NqRTxwsNQK2T" +
        "oszrwNqTMqf86_TuXfl7otZAQMw"
