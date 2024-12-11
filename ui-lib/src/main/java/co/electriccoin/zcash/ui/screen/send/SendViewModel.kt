package co.electriccoin.zcash.ui.screen.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneZip321ProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendAddressBookState
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.zecdev.zip321.ZIP321
import kotlin.time.Duration.Companion.seconds

class SendViewModel(
    private val observeContactByAddress: ObserveContactByAddressUseCase,
    private val observeContactPicked: ObserveContactPickedUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
    private val getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val getZashiAccount: GetZashiAccountUseCase,
    private val createKeystoneTransactionProposal: CreateKeystoneProposalUseCase,
    private val createKeystoneZip321TransactionProposal: CreateKeystoneZip321ProposalUseCase
) : ViewModel() {
    val recipientAddressState = MutableStateFlow(RecipientAddressState.new("", null))

    val navigateCommand = MutableSharedFlow<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val sendAddressBookState =
        recipientAddressState.flatMapLatest { recipientAddressState ->
            observeContactByAddress(recipientAddressState.address).flatMapLatest { contact ->
                flow {
                    val exists = contact != null
                    val isValid = recipientAddressState.type?.isNotValid == false
                    val mode =
                        if (isValid) {
                            if (exists) {
                                SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK
                            } else {
                                SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK
                            }
                        } else {
                            SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK
                        }
                    val isHintVisible = !exists && isValid

                    emit(
                        SendAddressBookState(
                            mode = mode,
                            isHintVisible = isHintVisible,
                            onButtonClick = { onAddressBookButtonClicked(mode, recipientAddressState) }
                        )
                    )

                    if (isHintVisible) {
                        delay(3.seconds)
                        emit(
                            SendAddressBookState(
                                mode = mode,
                                isHintVisible = false,
                                onButtonClick = { onAddressBookButtonClicked(mode, recipientAddressState) }
                            )
                        )
                    }
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            SendAddressBookState(
                mode = SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK,
                isHintVisible = false,
                onButtonClick = {
                    onAddressBookButtonClicked(
                        mode = SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK,
                        recipient = recipientAddressState.value
                    )
                }
            )
        )

    init {
        viewModelScope.launch {
            observeContactPicked().collect {
                onRecipientAddressChanged(it)
            }
        }
    }

    private fun onAddressBookButtonClicked(
        mode: SendAddressBookState.Mode,
        recipient: RecipientAddressState
    ) {
        when (mode) {
            SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK ->
                viewModelScope.launch {
                    navigateCommand.emit(AddressBookArgs(AddressBookArgs.PICK_CONTACT))
                }

            SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK ->
                viewModelScope.launch {
                    navigateCommand.emit(AddContactArgs(recipient.address))
                }
        }
    }

    fun onRecipientAddressChanged(state: RecipientAddressState) {
        recipientAddressState.update { state }
    }

    fun onCreateZecSendClick(
        newZecSend: ZecSend,
        setZecSend: (ZecSend) -> Unit,
        goSendConfirmation: (ZecSend) -> Unit,
        setSendStage: (SendStage) -> Unit
    ) = viewModelScope.launch {
        when (getSelectedWalletAccount()) {
            is KeystoneAccount ->
                if (!createKeystoneTransactionProposal(newZecSend)) {
                    setSendStage(SendStage.SendFailure(""))
                }
            is ZashiAccount -> {
                Twig.debug { "Getting send transaction proposal" }
                runCatching {
                    getSynchronizer().proposeSend(getZashiAccount().sdkAccount, newZecSend)
                }.onSuccess { proposal ->
                    Twig.debug { "Transaction proposal successful: ${proposal.toPrettyString()}" }
                    val enrichedZecSend = newZecSend.copy(proposal = proposal)
                    setZecSend(enrichedZecSend)
                    goSendConfirmation(enrichedZecSend)
                }.onFailure {
                    Twig.error(it) { "Transaction proposal failed" }
                    setSendStage(SendStage.SendFailure(it.message ?: ""))
                }
            }
        }
    }

    fun onCreateZecSend321Click(
        zip321Uri: String,
        setZecSend: (ZecSend) -> Unit,
        setSendStage: (SendStage) -> Unit,
        goPaymentRequest: (ZecSend, String) -> Unit,
    ) = viewModelScope.launch {
        when (getSelectedWalletAccount()) {
            is KeystoneAccount ->
                if (!createKeystoneZip321TransactionProposal(zip321Uri)) {
                    setSendStage(SendStage.SendFailure(""))
                }
            is ZashiAccount ->
                onCreateZecSend321ZashiClick(
                    zip321Uri = zip321Uri,
                    setZecSend = setZecSend,
                    setSendStage = setSendStage,
                    goPaymentRequest = goPaymentRequest,
                )
        }
    }

    private suspend fun onCreateZecSend321ZashiClick(
        zip321Uri: String,
        setZecSend: (ZecSend) -> Unit,
        setSendStage: (SendStage) -> Unit,
        goPaymentRequest: (ZecSend, String) -> Unit,
    ) {
        val synchronizer = getSynchronizer()
        val account = getZashiAccount()

        val request =
            runCatching {
                // At this point there should by only a valid Zcash address coming
                ZIP321.request(zip321Uri, null)
            }.onFailure {
                Twig.error(it) { "Failed to validate address" }
            }.getOrElse {
                false
            }
        val payment =
            when (request) {
                // We support only one payment currently
                is ZIP321.ParserResult.Request -> {
                    request.paymentRequest.payments[0]
                }
                else -> return
            }

        val address =
            synchronizer
                .validateAddress(payment.recipientAddress.value)
                .toWalletAddress(payment.recipientAddress.value)

        val amount = payment.nonNegativeAmount.value.convertZecToZatoshi()

        val memo = Memo(payment.memo?.let { String(it.data, Charsets.UTF_8) } ?: "")

        val zecSend =
            ZecSend(
                destination = address,
                amount = amount,
                memo = memo,
                proposal = null
            )
        setZecSend(zecSend)

        runCatching {
            synchronizer.proposeFulfillingPaymentUri(account.sdkAccount, zip321Uri)
        }.onSuccess { proposal ->
            Twig.debug { "Transaction proposal from Zip321 Uri: ${proposal.toPrettyString()}" }
            val enrichedZecSend = zecSend.copy(proposal = proposal)
            setZecSend(enrichedZecSend)
            goPaymentRequest(enrichedZecSend, zip321Uri)
        }.onFailure {
            Twig.error(it) { "Transaction proposal from Zip321 Uri failed" }
            setSendStage(SendStage.SendFailure(it.message ?: ""))
        }
    }

    private suspend fun AddressType.toWalletAddress(value: String) =
        when (this) {
            AddressType.Unified -> WalletAddress.Unified.new(value)
            AddressType.Shielded -> WalletAddress.Sapling.new(value)
            AddressType.Transparent -> WalletAddress.Transparent.new(value)
            else -> error("Invalid address type")
        }
}
