package co.electriccoin.zcash.ui.screen.deletewallet

import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.AddressBookStorageProvider
import co.electriccoin.zcash.ui.common.provider.MetadataStorageProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.repository.BiometricsCancelledException
import co.electriccoin.zcash.ui.common.repository.BiometricsFailureException
import co.electriccoin.zcash.ui.common.repository.FlexaRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageCacheRepository
import co.electriccoin.zcash.ui.common.usecase.ErrorArgs
import co.electriccoin.zcash.ui.common.usecase.NavigateToErrorUseCase
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.first

class ResetZashiUseCase(
    private val walletCoordinator: WalletCoordinator,
    private val flexaRepository: FlexaRepository,
    private val synchronizerProvider: SynchronizerProvider,
    private val addressBookStorageProvider: AddressBookStorageProvider,
    private val metadataStorageProvider: MetadataStorageProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider,
    private val addressBookRepository: AddressBookRepository,
    private val homeMessageCacheRepository: HomeMessageCacheRepository,
    private val biometricRepository: BiometricRepository,
    private val navigateToError: NavigateToErrorUseCase
) {
    @Suppress("TooGenericExceptionCaught", "ThrowsCount")
    suspend operator fun invoke(keepFiles: Boolean) {
        try {
            biometricRepository.requestBiometrics(
                BiometricRequest(
                    message =
                        stringRes(
                            R.string.authentication_system_ui_subtitle,
                            stringRes(R.string.authentication_use_case_delete_wallet)
                        )
                )
            )

            flexaRepository.disconnect()
            synchronizerProvider.getSdkSynchronizer().closeFlow().first()
            if (!clearSDK()) throw ResetZashiException("Wallet deletion failed")
            if (!keepFiles) {
                addressBookStorageProvider.geAddressBookDir()?.deleteRecursively()
                metadataStorageProvider.getMetadataDir()?.deleteRecursively()
            }
            if (!clearFiles()) throw ResetZashiException("Failed to delete files")
            if (!clearSharedPrefs()) throw ResetZashiException("Failed to clear shared preferences")
            clearInMemoryData()
        } catch (_: BiometricsFailureException) {
            // do nothing
        } catch (_: BiometricsCancelledException) {
            // do nothing
        } catch (e: ResetZashiException) {
            navigateToError.invoke(ErrorArgs.General(e))
        } catch (e: Exception) {
            navigateToError.invoke(ErrorArgs.General(e))
        }
    }

    private suspend fun clearSDK(): Boolean = walletCoordinator.deleteSdkDataFlow().first()

    private fun clearFiles(): Boolean {
        val abCleared = addressBookStorageProvider.geAddressBookDir()?.deleteRecursively() in listOf(true, null)
        val metadataCleared = metadataStorageProvider.getMetadataDir()?.deleteRecursively() in listOf(true, null)
        return abCleared && metadataCleared
    }

    private suspend fun clearSharedPrefs(): Boolean {
        val standardPrefsCleared = standardPreferenceProvider().clearPreferences()
        val encryptedPrefsCleared = encryptedPreferenceProvider().clearPreferences()
        return standardPrefsCleared && encryptedPrefsCleared
    }

    private suspend fun clearInMemoryData() {
        addressBookRepository.resetAddressBook()
        homeMessageCacheRepository.reset()
    }
}

private class ResetZashiException(
    message: String
) : Exception(message)
