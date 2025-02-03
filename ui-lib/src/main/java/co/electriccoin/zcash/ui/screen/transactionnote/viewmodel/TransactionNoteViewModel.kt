package co.electriccoin.zcash.ui.screen.transactionnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.CreateOrUpdateTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionMetadataUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResourceColor
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionnote.TransactionNote
import co.electriccoin.zcash.ui.screen.transactionnote.model.TransactionNoteState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class TransactionNoteViewModel(
    private val transactionNote: TransactionNote,
    private val navigationRouter: NavigationRouter,
    private val getTransactionNote: GetTransactionMetadataUseCase,
    private val createOrUpdateTransactionNote: CreateOrUpdateTransactionNoteUseCase,
    private val deleteTransactionNote: DeleteTransactionNoteUseCase
) : ViewModel() {
    val hideBottomSheetRequest = MutableSharedFlow<Unit>()

    private val bottomSheetHiddenResponse = MutableSharedFlow<Unit>()

    private val noteText = MutableStateFlow("")
    private val foundNote = MutableStateFlow<String?>(null)

    val state: StateFlow<TransactionNoteState> =
        combine(noteText, foundNote) { noteText, foundNote ->
            createState(noteText, foundNote)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(noteText = "", note = null)
        )

    private fun createState(
        noteText: String,
        note: String?
    ): TransactionNoteState {
        val isNoteTextTooLong = noteText.length > MAX_NOTE_LENGTH

        return TransactionNoteState(
            onBack = ::onBack,
            onBottomSheetHidden = ::onBottomSheetHidden,
            title =
                if (note == null) {
                    stringRes(R.string.transaction_note_add_note_title)
                } else {
                    stringRes(R.string.transaction_note_edit_note_title)
                },
            note =
                TextFieldState(
                    value = stringRes(noteText),
                    error = stringRes("").takeIf { isNoteTextTooLong },
                    onValueChange = ::onNoteTextChanged
                ),
            noteCharacters =
                StyledStringResource(
                    resource = stringRes(R.string.transaction_note_max_length, noteText.length.toString()),
                    color = if (isNoteTextTooLong) StringResourceColor.NEGATIVE else StringResourceColor.DEFAULT
                ),
            primaryButton =
                ButtonState(
                    text = stringRes(R.string.transaction_note_add_note),
                    onClick = ::onAddOrUpdateNoteClick,
                    isEnabled = !isNoteTextTooLong && noteText.isNotEmpty()
                ).takeIf { note == null },
            secondaryButton =
                ButtonState(
                    text = stringRes(R.string.transaction_note_save_note),
                    onClick = ::onAddOrUpdateNoteClick,
                    isEnabled = !isNoteTextTooLong && noteText.isNotEmpty()
                ).takeIf { note != null },
            negative =
                ButtonState(
                    text = stringRes(R.string.transaction_note_delete_note),
                    onClick = ::onDeleteNoteClick,
                ).takeIf { note != null },
        )
    }

    init {
        viewModelScope.launch {
            val metadata = getTransactionNote(transactionNote.txId)
            foundNote.update { metadata.note.orEmpty() }
            noteText.update { metadata.note.orEmpty() }
        }
    }

    private fun onAddOrUpdateNoteClick() =
        viewModelScope.launch {
            createOrUpdateTransactionNote(txId = transactionNote.txId, note = noteText.value) {
                hideBottomSheet()
            }
        }

    private fun onDeleteNoteClick() =
        viewModelScope.launch {
            deleteTransactionNote(transactionNote.txId) {
                hideBottomSheet()
            }
        }

    private fun onNoteTextChanged(newValue: String) {
        noteText.update { newValue }
    }

    private suspend fun hideBottomSheet() {
        hideBottomSheetRequest.emit(Unit)
        bottomSheetHiddenResponse.first()
    }

    private fun onBottomSheetHidden() =
        viewModelScope.launch {
            bottomSheetHiddenResponse.emit(Unit)
        }

    private fun onBack() =
        viewModelScope.launch {
            hideBottomSheet()
            navigationRouter.back()
        }
}

private const val MAX_NOTE_LENGTH = 90
