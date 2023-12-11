package co.electriccoin.zcash.ui.screen.onboarding.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle

/*
 * Android-specific ViewModel.  This is used to save and restore state across Activity recreations
 * outside of the Compose framework.
 */
class OnboardingViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    // This is a bit weird being placed here, but onboarding currently is considered complete when
    // the user has a persisted wallet. Also import allows the user to go back to onboarding, while
    // creating a new wallet does not.
    val isImporting = savedStateHandle.getStateFlow(KEY_IS_IMPORTING, false)

    fun setIsImporting(isImporting: Boolean) {
        savedStateHandle[KEY_IS_IMPORTING] = isImporting
    }

    val showWelcomeAnimation = savedStateHandle.getStateFlow(KEY_SHOW_WELCOME_ANIMATION, true)

    fun setShowWelcomeAnimation(setShowWelcomeAnimation: Boolean) {
        savedStateHandle[KEY_SHOW_WELCOME_ANIMATION] = setShowWelcomeAnimation
    }

    companion object {
        private const val KEY_IS_IMPORTING = "is_importing" // $NON-NLS
        private const val KEY_SHOW_WELCOME_ANIMATION = "show_welcome_animation" // $NON-NLS
    }
}
