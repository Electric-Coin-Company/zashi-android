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
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletStateUseCase
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.integrations.model.IntegrationsState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
import com.flexa.core.Flexa
import com.flexa.spend.Transaction
import com.flexa.spend.buildSpend
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntegrationsViewModel(
    getVersionInfo: GetVersionInfoProvider,
    getZcashCurrency: GetZcashCurrencyProvider,
    observeWalletState: ObserveWalletStateUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val getTransparentAddress: GetTransparentAddressUseCase,
    private val isFlexaAvailable: IsFlexaAvailableUseCase,
    private val isCoinbaseAvailable: IsCoinbaseAvailableUseCase,
    private val getSpendingKey: GetSpendingKeyUseCase,
    private val context: Context,
) : ViewModel() {
    val backNavigationCommand = MutableSharedFlow<Unit>()
    val flexaNavigationCommand = MutableSharedFlow<Unit>()
    val coinbaseNavigationCommand = MutableSharedFlow<String>()

    private val versionInfo = getVersionInfo()
    private val isDebug = versionInfo.let { it.isDebuggable && !it.isRunningUnderTestService }

    private val isEnabled =
        observeWalletState()
            .map {
                it != TopAppBarSubTitleState.Restoring
            }

    val state =
        isEnabled.map { isEnabled ->
            IntegrationsState(
                version = stringRes(R.string.integrations_version, versionInfo.versionName),
                disabledInfo = stringRes(R.string.integrations_disabled_info).takeIf { isEnabled.not() },
                onBack = ::onBack,
                items =
                    listOfNotNull(
                        ZashiSettingsListItemState(
                            // Set the wallet currency by app build is more future-proof, although we hide it from
                            // the UI in the Testnet build
                            icon = R.drawable.ic_integrations_coinbase,
                            text = stringRes(R.string.integrations_coinbase, getZcashCurrency.getLocalizedName()),
                            subtitle =
                                stringRes(
                                    R.string.integrations_coinbase_subtitle,
                                    getZcashCurrency.getLocalizedName()
                                ),
                            onClick = ::onBuyWithCoinbaseClicked
                        ).takeIf { isCoinbaseAvailable() },
                        ZashiSettingsListItemState(
                            // Set the wallet currency by app build is more future-proof, although we hide it from
                            // the UI in the Testnet build
                            isEnabled = isEnabled,
                            icon =
                                if (isEnabled) {
                                    R.drawable.ic_integrations_flexa
                                } else {
                                    R.drawable.ic_integrations_flexa_disabled
                                },
                            text = stringRes(R.string.integrations_flexa),
                            subtitle = stringRes(R.string.integrations_flexa_subtitle),
                            onClick = ::onFlexaClicked
                        ).takeIf { isFlexaAvailable() }
                    ).toImmutableList()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onBuyWithCoinbaseClicked() =
        viewModelScope.launch {
            val appId = BuildConfig.ZCASH_COINBASE_APP_ID

            when {
                appId.isEmpty() && isDebug ->
                    coinbaseNavigationCommand.emit("https://www.coinbase.com") // fallback debug url

                appId.isEmpty() && isDebug -> {
                    // should not happen
                }

                appId.isNotEmpty() -> {
                    val address = getTransparentAddress().address
                    val url =
                        "https://pay.coinbase.com/buy/select-asset?appId=$appId&addresses={\"${address}\":[\"zcash\"]}"
                    coinbaseNavigationCommand.emit(url)
                }

                else -> {
                    // should not happen
                }
            }
        }

    private fun onFlexaClicked() =
        viewModelScope.launch {
            flexaNavigationCommand.emit(Unit)
        }

    fun onFlexaResultCallback(transaction: Result<Transaction>) =
        viewModelScope.launch {
            Twig.debug { "Getting send transaction proposal" }
            runCatching {
                getSynchronizer().proposeSend(getSpendingKey().account, getZecSend(transaction.getOrNull()))
            }.onSuccess { proposal ->
                Twig.debug { "Transaction proposal successful: ${proposal.toPrettyString()}" }
                val result = submitTransactions(proposal = proposal, spendingKey = getSpendingKey())

                when (result.first) {
                    SubmitResult.Success -> {
                        Twig.debug { "Transaction successful $result" }
                        Flexa.buildSpend()
                            .transactionSent(
                                commerceSessionId = transaction.getOrNull()?.commerceSessionId.orEmpty(),
                                txSignature = result.second.orEmpty()
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
            synchronizer.createProposedTransactions(
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
                    SubmitResult.MultipleTrxFailure to null
                }
            } else {
                // All transaction submissions were successful
                SubmitResult.Success to
                    submitResults.filterIsInstance<TransactionSubmitResult.Success>()
                        .map { it.txIdString() }.firstOrNull()
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
