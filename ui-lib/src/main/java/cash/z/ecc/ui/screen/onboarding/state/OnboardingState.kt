package cash.z.ecc.ui.screen.onboarding.state

import cash.z.ecc.ui.screen.onboarding.model.OnboardingStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @param initialState Allows restoring the state from a different starting point. This is
 * primarily useful on Android, for automated tests, and for iterative debugging with the Compose
 * layout preview. The default constructor argument is generally fine for other platforms.
 */
class OnboardingState(initialState: OnboardingStage = OnboardingStage.values().first()) {

    private val mutableState = MutableStateFlow(initialState)

    val current: StateFlow<OnboardingStage> = mutableState

    fun goNext() {
        mutableState.value = current.value.getNext()
    }

    fun goPrevious() {
        mutableState.value = current.value.getPrevious()
    }

    fun goToBeginning() {
        mutableState.value = OnboardingStage.values().first()
    }

    fun goToEnd() {
        mutableState.value = OnboardingStage.values().last()
    }
}
