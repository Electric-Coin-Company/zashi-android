package co.electriccoin.zcash.ui.common.repository

import android.content.Context
import androidx.biometric.BiometricManager
import co.electriccoin.zcash.spackle.AndroidApiVersion
import co.electriccoin.zcash.ui.BiometricActivity
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

interface BiometricRepository {
    val allowedAuthenticators: Int

    fun onBiometricResult(result: BiometricResult)

    @Throws(BiometricsFailureException::class, BiometricsCancelledException::class)
    suspend fun requestBiometrics(request: BiometricRequest)
}

data class BiometricRequest(
    val message: StringResource,
    val requestCode: String = UUID.randomUUID().toString(),
)

sealed interface BiometricResult {
    val requestCode: String

    data class Success(
        override val requestCode: String
    ) : BiometricResult

    data class Failure(
        override val requestCode: String
    ) : BiometricResult

    data class Cancelled(
        override val requestCode: String
    ) : BiometricResult
}

class BiometricsFailureException : Exception()

class BiometricsCancelledException : Exception()

class BiometricRepositoryImpl(
    private val context: Context,
    private val biometricManager: BiometricManager
) : BiometricRepository {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val onBiometricsResult = MutableSharedFlow<BiometricResult>()

    override val allowedAuthenticators: Int
        get() =
            when {
                // Android SDK version == 27
                (AndroidApiVersion.isExactlyO) ->
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                // Android SDK version >= 30
                (AndroidApiVersion.isAtLeastR) ->
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                // Android SDK version == 28 || 29
                (AndroidApiVersion.isExactlyP || AndroidApiVersion.isExactlyQ) ->
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL

                else -> error("Unsupported Android SDK version")
            }

    override fun onBiometricResult(result: BiometricResult) {
        scope.launch {
            onBiometricsResult.emit(result)
        }
    }

    override suspend fun requestBiometrics(request: BiometricRequest) {
        if (!canAuthenticate()) {
            // do nothing
            return
        }

        context.startActivity(
            BiometricActivity.createIntent(
                context = context,
                requestCode = request.requestCode,
                subtitle = request.message.getString(context)
            )
        )
        when (
            onBiometricsResult.filter { it.requestCode == request.requestCode }.first()
        ) {
            is BiometricResult.Cancelled -> throw BiometricsCancelledException()
            is BiometricResult.Failure -> throw BiometricsFailureException()
            is BiometricResult.Success -> {
                // do nothing
            }
        }
    }

    private fun canAuthenticate(): Boolean =
        when (biometricManager.canAuthenticate(allowedAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
}
