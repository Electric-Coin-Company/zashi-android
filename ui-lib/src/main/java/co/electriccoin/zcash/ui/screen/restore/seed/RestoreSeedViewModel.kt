package co.electriccoin.zcash.ui.screen.restore.seed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.SeedTextFieldState
import co.electriccoin.zcash.ui.design.component.SeedWordTextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.RestoreSeedDialogState
import co.electriccoin.zcash.ui.screen.restore.height.RestoreBDHeight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RestoreSeedViewModel(
    private val navigationRouter: NavigationRouter
) : ViewModel() {

    @Suppress("MagicNumber")
    private val seedWords =
        MutableStateFlow(
            (0..23).map { index ->
                SeedWordTextFieldState(
                    value = stringRes(""),
                    onValueChange = { onValueChange(index, it) },
                    isError = false
                )
            }
        )

    private val isDialogVisible = MutableStateFlow(false)

    val dialogState =
        isDialogVisible
            .map { isDialogVisible ->
                RestoreSeedDialogState(
                    ::onCloseDialogClick
                ).takeIf { isDialogVisible }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val state: StateFlow<RestoreSeedState?> =
        seedWords
            .map { words ->
                createState(words)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    // /**
    //  * The complete word list that the user can choose from; useful for autocomplete
    //  */
    // val completeWordList =
    //     // This is a hack to prevent disk IO on the main thread
    //     flow<CompleteWordSetState> {
    //         // Using IO context because of https://github.com/Electric-Coin-Company/kotlin-bip39/issues/13
    //         val completeWordList =
    //             withContext(Dispatchers.IO) {
    //                 Mnemonics.getCachedWords(Locale.ENGLISH.language)
    //             }
    //
    //         emit(CompleteWordSetState.Loaded(completeWordList.toPersistentSet()))
    //     }.stateIn(
    //         viewModelScope,
    //         SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
    //         CompleteWordSetState.Loading
    //     )

    private fun createState(words: List<SeedWordTextFieldState>) =
        RestoreSeedState(
            seed = SeedTextFieldState(values = words),
            onBack = ::onBack,
            dialogButton = IconButtonState(icon = R.drawable.ic_info, onClick = ::onInfoButtonClick),
            nextButton =
                ButtonState(
                    stringRes(R.string.restore_button),
                    onClick = ::onNextClicked
                )
        )

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        isDialogVisible.update { true }
    }

    private fun onNextClicked() {
        navigationRouter.forward(RestoreBDHeight)
    }

    private fun onValueChange(
        index: Int,
        value: String
    ) {
        seedWords.update {
            val newSeedWords = it.toMutableList()
            newSeedWords[index] = newSeedWords[index].copy(value = stringRes(value))
            newSeedWords.toList()
        }
    }

    private fun onCloseDialogClick() {
        isDialogVisible.update { false }
    }
}
