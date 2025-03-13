package co.electriccoin.zcash.ui.screen.integrations.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Proposal
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.repository.BiometricRepository
import co.electriccoin.zcash.ui.common.repository.BiometricRequest
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletStateUseCase
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import co.electriccoin.zcash.ui.screen.integrations.model.IntegrationsState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import com.flexa.core.Flexa
import com.flexa.spend.Transaction
import com.flexa.spend.buildSpend
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntegrationsViewModel(
    getZcashCurrency: GetZcashCurrencyProvider,
    observeWalletState: ObserveWalletStateUseCase,
    isFlexaAvailableUseCase: IsFlexaAvailableUseCase,
    isCoinbaseAvailable: IsCoinbaseAvailableUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val getZashiAccount: GetZashiAccountUseCase,
    private val isFlexaAvailable: IsFlexaAvailableUseCase,
    private val getSpendingKey: GetZashiSpendingKeyUseCase,
    private val context: Context,
    private val biometricRepository: BiometricRepository,
    private val navigationRouter: NavigationRouter,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase,
    private val observeWalletAccounts: ObserveWalletAccountsUseCase,
) : ViewModel() {
    val flexaNavigationCommand = MutableSharedFlow<Unit>()

    private val isEnabled =
        observeWalletState()
            .map {
                it != TopAppBarSubTitleState.Restoring
            }

    val state =
        combine(
            isFlexaAvailableUseCase.observe(),
            isCoinbaseAvailable.observe(),
            isEnabled,
            observeWalletAccounts()
        ) { isFlexaAvailable, isCoinbaseAvailable, isEnabled, accounts ->
            IntegrationsState(
                disabledInfo = stringRes(R.string.integrations_disabled_info).takeIf { isEnabled.not() },
                onBack = ::onBack,
                items =
                    listOfNotNull(
                        ZashiListItemState(
                            // Set the wallet currency by app build is more future-proof, although we hide it from
                            // the UI in the Testnet build
                            icon = R.drawable.ic_integrations_coinbase,
                            title = stringRes(R.string.integrations_coinbase, getZcashCurrency.getLocalizedName()),
                            subtitle =
                                stringRes(
                                    R.string.integrations_coinbase_subtitle,
                                    getZcashCurrency.getLocalizedName()
                                ),
                            onClick = ::onBuyWithCoinbaseClicked
                        ).takeIf { isCoinbaseAvailable == true },
                        ZashiListItemState(
                            // Set the wallet currency by app build is more future-proof, although we hide it from
                            // the UI in the Testnet build
                            isEnabled = isEnabled,
                            icon =
                                if (isEnabled) {
                                    R.drawable.ic_integrations_flexa
                                } else {
                                    R.drawable.ic_integrations_flexa_disabled
                                },
                            title = stringRes(R.string.integrations_flexa),
                            subtitle = stringRes(R.string.integrations_flexa_subtitle),
                            onClick = ::onFlexaClicked
                        ).takeIf { isFlexaAvailable == true },
                        ZashiListItemState(
                            title = stringRes(R.string.integrations_keystone),
                            subtitle = stringRes(R.string.integrations_keystone_subtitle),
                            icon = R.drawable.ic_integrations_keystone,
                            onClick = ::onConnectKeystoneClick
                        ).takeIf { accounts.orEmpty().none { it is KeystoneAccount } },
                    ).toImmutableList()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    fun onBack() = navigationRouter.back()

    private fun onBuyWithCoinbaseClicked() =
        viewModelScope.launch {
            navigateToCoinbase()
        }

    private fun onConnectKeystoneClick() = navigationRouter.forward(ConnectKeystone)

    private fun onFlexaClicked() =
        viewModelScope.launch {
            if (isFlexaAvailable()) {
                flexaNavigationCommand.emit(Unit)
            }
        }

    fun onFlexaResultCallback(transaction: Result<Transaction>) =
        viewModelScope.launch {
            runCatching {
                biometricRepository.requestBiometrics(
                    BiometricRequest(message = stringRes(R.string.integrations_flexa_biometric_message))
                )
                Twig.debug { "Getting send transaction proposal" }
                getSynchronizer()
                    .proposeSend(
                        account = getZashiAccount().sdkAccount,
                        send = getZecSend(transaction.getOrNull())
                    )
            }.onSuccess { proposal ->
                Twig.debug { "Transaction proposal successful: ${proposal.toPrettyString()}" }
                val result = submitTransactions(proposal = proposal, spendingKey = getSpendingKey())
                when (val output = result.first) {
                    is SubmitResult.Success -> {
                        Twig.debug { "Transaction successful $result" }
                        Flexa
                            .buildSpend()
                            .transactionSent(
                                commerceSessionId = transaction.getOrNull()?.commerceSessionId.orEmpty(),
                                txSignature = result.second.orEmpty()
                            )
                    }
                    is SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc -> {
                        Twig.warn { "Transaction grpc failure $result" }
                        Flexa
                            .buildSpend()
                            .transactionSent(
                                commerceSessionId = transaction.getOrNull()?.commerceSessionId.orEmpty(),
                                txSignature = output.result.txIdString()
                            )
                    }

                    else -> {
                        Twig.error { "Transaction submission failed" }
                    }
                }
            }.onFailure {
                Twig.error(it) { "Transaction proposal failed" }
            }
        }

    private suspend fun submitTransactions(
        proposal: Proposal,
        spendingKey: UnifiedSpendingKey
    ): Pair<SubmitResult, String?> {
        Twig.debug { "Sending transactions..." }

        val result =
            runCreateTransactions(
                synchronizer = getSynchronizer(),
                spendingKey = spendingKey,
                proposal = proposal
            )

        // Triggering the transaction history and balances refresh to be notified immediately
        // about the wallet's updated state
        (getSynchronizer() as SdkSynchronizer).run {
            refreshTransactions()
            refreshAllBalances()
        }

        return result
    }

    private suspend fun runCreateTransactions(
        synchronizer: Synchronizer,
        spendingKey: UnifiedSpendingKey,
        proposal: Proposal
    ): Pair<SubmitResult, String?> {
        val submitResults = mutableListOf<TransactionSubmitResult>()

        return runCatching {
            synchronizer
                .createProposedTransactions(
                    proposal = proposal,
                    usk = spendingKey
                ).collect { submitResult ->
                    Twig.info { "Transaction submit result: $submitResult" }
                    submitResults.add(submitResult)
                }
            if (submitResults.find { it is TransactionSubmitResult.Failure } != null) {
                if (submitResults.size == 1) {
                    // The first transaction submission failed - user might just be able to re-submit the transaction
                    // proposal. Simple error pop up is fine then
                    val result = (submitResults[0] as TransactionSubmitResult.Failure)
                    if (result.grpcError) {
                        SubmitResult.SimpleTrxFailure.SimpleTrxFailureGrpc(result) to null
                    } else {
                        SubmitResult.SimpleTrxFailure.SimpleTrxFailureSubmit(result) to null
                    }
                } else {
                    // Any subsequent transaction submission failed - user needs to resolve this manually. Multiple
                    // transaction failure screen presented
                    SubmitResult.MultipleTrxFailure(submitResults) to null
                }
            } else {
                // All transaction submissions were successful
                SubmitResult.Success(emptyList()) to
                    submitResults
                        .filterIsInstance<TransactionSubmitResult.Success>()
                        .map { it.txIdString() }
                        .firstOrNull()
            }
        }.onSuccess {
            Twig.debug { "Transactions submitted successfully" }
        }.onFailure {
            Twig.error(it) { "Transactions submission failed" }
        }.getOrElse {
            SubmitResult.SimpleTrxFailure.SimpleTrxFailureOther(it) to null
        }
    }

    @Suppress("TooGenericExceptionThrown")
    private suspend fun getZecSend(transaction: Transaction?): ZecSend {
        if (transaction == null) throw NullPointerException("Transaction is null")

        val address = transaction.destinationAddress.split(":").last()

        val recipientAddressState =
            RecipientAddressState.new(
                address = address,
                // TODO [#342]: Verify Addresses without Synchronizer
                // TODO [#342]: https://github.com/zcash/zcash-android-wallet-sdk/issues/342
                type = getSynchronizer().validateAddress(address)
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

            is ZecSendExt.ZecSendValidation.Invalid -> {
                // We do not expect this validation to fail, so logging is enough here
                // An error popup could be reasonable here as well
                Twig.warn { "Send failed with: ${zecSendValidation.validationErrors}" }

                throw RuntimeException("Validation failed")
            }
        }
    }
}
