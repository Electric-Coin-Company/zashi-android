package co.electriccoin.zcash.ui.screen.onboarding.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import co.electriccoin.zcash.ui.screen.onboarding.model.OnboardingStage
import co.electriccoin.zcash.ui.screen.onboarding.state.OnboardingState

/*
 * Android-specific ViewModel.  This is used to save and restore state across Activity recreations
 * outside of the Compose framework.
 */
class OnboardingViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
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
    val isImporting = savedStateHandle.getStateFlow(KEY_IS_IMPORTING, false)

    fun setIsImporting(isImporting: Boolean) {
        savedStateHandle[KEY_IS_IMPORTING] = isImporting
    }

    init {
        // viewModelScope is constructed with Dispatchers.Main.immediate, so this will
        // update the save state as soon as a change occurs.
        onboardingState.current.collectWith(viewModelScope) {
            savedStateHandle[KEY_STAGE] = it
        }
    }

    companion object {
        private const val KEY_STAGE = "stage" // $NON-NLS
        private const val KEY_IS_IMPORTING = "is_importing" // $NON-NLS
    }
}
