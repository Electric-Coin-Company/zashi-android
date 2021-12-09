package cash.z.ecc.ui.screen.onboarding.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import cash.z.ecc.ui.screen.onboarding.model.OnboardingStage
import cash.z.ecc.ui.screen.onboarding.state.OnboardingState
import kotlinx.coroutines.flow.MutableStateFlow

/*
 * Android-specific ViewModel.  This is used to save and restore state across Activity recreations
 * outside of the Compose framework.
 */
class OnboardingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val onboardingState: OnboardingState = run {
        val initialValue = if (savedStateHandle.contains(KEY_STAGE)) {
            savedStateHandle.get<OnboardingStage>(KEY_STAGE)
        } else {
            null
        }

        if (null == initialValue) {
            OnboardingState()
        } else {
            OnboardingState(initialValue)
        }
    }

    // This is a bit weird being placed here, but onboarding currently is considered complete when
    // the user has a persisted wallet. Also import allows the user to go back to onboarding, while
    // creating a new wallet does not.
    val isImporting = run {
        val initialValue = savedStateHandle.get<Boolean?>(KEY_IS_IMPORTING) ?: false

        MutableStateFlow(initialValue)
    }

    init {
        // viewModelScope is constructed with Dispatchers.Main.immediate, so this will
        // update the save state as soon as a change occurs.
        onboardingState.current.collectWith(viewModelScope) {
            savedStateHandle.set(KEY_STAGE, it)
        }

        isImporting.collectWith(viewModelScope) {
            savedStateHandle.set(KEY_IS_IMPORTING, it)
        }
    }

    companion object {
        private const val KEY_STAGE = "stage" // $NON-NLS
        private const val KEY_IS_IMPORTING = "is_importing" // $NON-NLS
    }
}
