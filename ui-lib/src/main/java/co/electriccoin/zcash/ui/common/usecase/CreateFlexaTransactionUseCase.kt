package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.TransactionProposalNotCreatedException
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.repository.BiometricsCancelledException
import co.electriccoin.zcash.ui.common.repository.BiometricsFailureException
import co.electriccoin.zcash.ui.common.repository.SubmitProposalState
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import com.flexa.core.Flexa
import com.flexa.core.shared.Transaction
import com.flexa.spend.buildSpend
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

class CreateFlexaTransactionUseCase(
    private val synchronizerProvider: SynchronizerProvider,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val biometricRepository: BiometricRepository,
    private val context: Context,
) {
    suspend operator fun invoke(transaction: Result<Transaction>) {
        try {
            biometricRepository.requestBiometrics(
                BiometricRequest(message = stringRes(R.string.integrations_flexa_biometric_message))
            )
            zashiProposalRepository.createProposal(getZecSend(transaction.getOrNull()))
            zashiProposalRepository.submitTransaction()
            when (
                val result =
                    zashiProposalRepository.submitState
                        .filterIsInstance<SubmitProposalState.Result>()
                        .first()
                        .submitResult
            ) {
                is SubmitResult.Success -> {
                    Flexa
                        .buildSpend()
                        .transactionSent(
                            commerceSessionId = transaction.getOrNull()?.commerceSessionId.orEmpty(),
                            txSignature = result.txIds.first()
                        )
                }

                is SubmitResult.GrpcFailure -> {
                    Flexa
                        .buildSpend()
                        .transactionSent(
                            commerceSessionId = transaction.getOrNull()?.commerceSessionId.orEmpty(),
                            txSignature = result.txIds.first()
                        )
                }

                else -> {
                    // do nothing
                }
            }
        } catch (_: BiometricsFailureException) {
            // do nothing
        } catch (_: BiometricsCancelledException) {
            // do nothing
        } catch (_: IllegalStateException) {
            // do nothing
        } catch (_: TransactionProposalNotCreatedException) {
            // do nothing
        } catch (_: Exception) {
            // do nothing
        }
    }

    @Suppress("TooGenericExceptionThrown")
    private suspend fun getZecSend(transaction: Transaction?): ZecSend {
        requireNotNull(transaction)

        val address = transaction.destinationAddress.split(":").last()

        val recipientAddressState =
            RecipientAddressState.new(
                address = address,
                // TODO [#342]: Verify Addresses without Synchronizer
                // TODO [#342]: https://github.com/zcash/zcash-android-wallet-sdk/issues/342
                type = synchronizerProvider.getSynchronizer().validateAddress(address)
            )

        return when (
            val zecSendValidation =
                ZecSendExt.new(
                    context = context,
                    destinationString = address,
                    zecString = transaction.amount,
                    // Take memo for a valid non-transparent receiver only
                    memoString = ""
                )
        ) {
            is ZecSendExt.ZecSendValidation.Valid ->
                zecSendValidation.zecSend.copy(
                    destination =
                        when (recipientAddressState.type) {
                            is AddressType.Invalid ->
                                WalletAddress.Unified.new(recipientAddressState.address)

                            AddressType.Shielded ->
                                WalletAddress.Unified.new(recipientAddressState.address)

                            AddressType.Tex ->
                                WalletAddress.Tex.new(recipientAddressState.address)

                            AddressType.Transparent ->
                                WalletAddress.Transparent.new(recipientAddressState.address)

                            AddressType.Unified ->
                                WalletAddress.Unified.new(recipientAddressState.address)

                            null -> WalletAddress.Unified.new(recipientAddressState.address)
                        }
                )

            is ZecSendExt.ZecSendValidation.Invalid -> throw RuntimeException("Validation failed")
        }
    }
}
