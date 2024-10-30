package co.electriccoin.zcash.ui.screen.support.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.SendSupportEmailUseCase
import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.support.model.FeedbackEmoji
import co.electriccoin.zcash.ui.screen.support.model.FeedbackEmojiState
import co.electriccoin.zcash.ui.screen.support.model.FeedbackState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val sendSupportEmail: SendSupportEmailUseCase
) : ViewModel() {
    private val feedback = MutableStateFlow("")
    private val selectedEmoji = MutableStateFlow(FeedbackEmoji.FIFTH)
    private val isDialogShown = MutableStateFlow(false)

    val onBackNavigationCommand = MutableSharedFlow<Unit>()

    val state =
        combine(feedback, selectedEmoji) { feedbackText, emoji ->
            FeedbackState(
                onBack = ::onBack,
                emojiState =
                    FeedbackEmojiState(
                        selection = emoji,
                        onSelected = { new -> selectedEmoji.update { new } }
                    ),
                feedback =
                    TextFieldState(
                        value = stringRes(feedbackText),
                        onValueChange = { new -> feedback.update { new } }
                    ),
                sendButton =
                    ButtonState(
                        text = stringRes(R.string.support_send),
                        isEnabled = feedbackText.isNotEmpty(),
                        onClick = ::onSendClicked
                    )
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    val dialogState =
        isDialogShown.map { isShown ->
            AlertDialogState(
                title = stringRes(R.string.support_confirmation_dialog_title),
                text = stringRes(R.string.support_confirmation_explanation),
                confirmButtonState =
                    ButtonState(
                        text = stringRes(R.string.support_confirmation_dialog_ok),
                        onClick = ::onConfirmSendFeedback
                    ),
                dismissButtonState =
                    ButtonState(
                        text = stringRes(R.string.support_confirmation_dialog_cancel),
                        onClick = { isDialogShown.update { false } }
                    ),
                onDismissRequest = { isDialogShown.update { false } }
            ).takeIf { isShown }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private fun onConfirmSendFeedback() =
        viewModelScope.launch {
            isDialogShown.update { false }
            sendSupportEmail(
                emoji = selectedEmoji.value,
                message = stringRes(feedback.value)
            )
        }

    private fun onSendClicked() {
        isDialogShown.update { true }
    }

    private fun onBack() =
        viewModelScope.launch {
            onBackNavigationCommand.emit(Unit)
        }
}
