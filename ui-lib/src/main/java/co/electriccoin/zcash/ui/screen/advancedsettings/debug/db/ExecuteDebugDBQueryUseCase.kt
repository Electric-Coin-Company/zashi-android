package co.electriccoin.zcash.ui.screen.advancedsettings.debug.db

import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.repository.BiometricsCancelledException
import co.electriccoin.zcash.ui.common.repository.BiometricsFailureException
import co.electriccoin.zcash.ui.design.util.stringRes

class ExecuteDebugDBQueryUseCase(
    private val synchronizerProvider: SynchronizerProvider,
    private val biometricRepository: BiometricRepository
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(query: String): String? =
        try {
            biometricRepository
                .requestBiometrics(
                    BiometricRequest(
                        message = stringRes("Please authenticate to execute this query")
                    )
                )

            synchronizerProvider.getSynchronizer().debugQuery(query)
        } catch (_: BiometricsFailureException) {
            null
        } catch (_: BiometricsCancelledException) {
            null
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
}
