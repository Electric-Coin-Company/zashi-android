@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.authentication

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationResult
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.screen.authentication.view.AppAccessAuthentication
import co.electriccoin.zcash.ui.screen.authentication.view.AuthenticationErrorDialog
import kotlin.time.Duration.Companion.milliseconds

private const val APP_ACCESS_TRIGGER_DELAY = 0
private const val SEND_FUNDS_DELAY = 0
internal const val RETRY_TRIGGER_DELAY = 0

@Composable
internal fun MainActivity.WrapAuthentication(
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onFail: () -> Unit,
    useCase: AuthenticationUseCase,
    goSupport: (() -> Unit)? = null,
) {
    WrapAuthenticationUseCases(
        activity = this,
        goSupport = goSupport,
        onSuccess = onSuccess,
        onCancel = onCancel,
        onFail = onFail,
        useCase = useCase
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapAuthenticationUseCases(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onFail: () -> Unit,
    useCase: AuthenticationUseCase,
    goSupport: (() -> Unit)? = null,
) {
    when (useCase) {
        AuthenticationUseCase.AppAccess -> {
            Twig.debug { "App Access Authentication" }
            WrapAppAccessAuth(
                activity = activity,
                goToAppContent = onSuccess,
                onCancel = onCancel,
                onFail = onFail
            )
        }
        AuthenticationUseCase.SendFunds -> {
            Twig.debug { "Send Funds Authentication" }
            WrapSendFundsAuth(
                activity = activity,
                onSendFunds = onSuccess,
                goSupport = goSupport ?: {},
                onCancel = onCancel,
                onFail = onFail
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun WrapSendFundsAuth(
    activity: FragmentActivity,
    goSupport: () -> Unit,
    onSendFunds: () -> Unit,
    onCancel: () -> Unit,
    onFail: () -> Unit,
) {
    val authenticationViewModel = koinActivityViewModel<AuthenticationViewModel>()

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None)
            .value

    when (authenticationResult) {
        AuthenticationResult.None -> {
            Twig.info { "Authentication result: initiating" }
            // Initial state
        }
        AuthenticationResult.Success -> {
            Twig.info { "Authentication result: successful" }
            authenticationViewModel.resetAuthenticationResult()
            onSendFunds()
        }
        AuthenticationResult.Canceled -> {
            Twig.info { "Authentication result: canceled" }
            authenticationViewModel.resetAuthenticationResult()
            onCancel()
        }
        AuthenticationResult.Failed -> {
            Twig.warn { "Authentication result: failed" }
            authenticationViewModel.resetAuthenticationResult()
            onFail()
            Toast
                .makeText(activity, stringResource(id = R.string.authentication_toast_failed), Toast.LENGTH_SHORT)
                .show()
        }
        is AuthenticationResult.Error -> {
            Twig.error {
                "Authentication result: error: ${authenticationResult.errorCode}: ${authenticationResult.errorMessage}"
            }
            AuthenticationErrorDialog(
                onDismiss = {
                    // Reset authentication states
                    authenticationViewModel.resetAuthenticationResult()
                    onCancel()
                },
                onRetry = {
                    authenticationViewModel.resetAuthenticationResult()
                    authenticationViewModel.authenticate(
                        activity = activity,
                        initialAuthSystemWindowDelay = RETRY_TRIGGER_DELAY.milliseconds,
                        useCase = AuthenticationUseCase.SendFunds
                    )
                },
                onSupport = {
                    authenticationViewModel.resetAuthenticationResult()
                    goSupport()
                },
                reason = authenticationResult
            )
        }
    }

    // Starting authentication
    LaunchedEffect(key1 = true) {
        authenticationViewModel.authenticate(
            activity = activity,
            initialAuthSystemWindowDelay = SEND_FUNDS_DELAY.milliseconds,
            useCase = AuthenticationUseCase.SendFunds
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun WrapAppAccessAuth(
    activity: FragmentActivity,
    goToAppContent: () -> Unit,
    onCancel: () -> Unit,
    onFail: () -> Unit,
) {
    val authenticationViewModel = koinActivityViewModel<AuthenticationViewModel>()

    val welcomeAnimVisibility = authenticationViewModel.showWelcomeAnimation.collectAsStateWithLifecycle().value

    val authFailed = authenticationViewModel.authFailed.collectAsStateWithLifecycle().value

    AppAccessAuthentication(
        onRetry = {
            authenticationViewModel.resetAuthenticationResult()
            authenticationViewModel.authenticate(
                activity = activity,
                initialAuthSystemWindowDelay = RETRY_TRIGGER_DELAY.milliseconds,
                useCase = AuthenticationUseCase.AppAccess
            )
        },
        showAuthLogo = authFailed,
        welcomeAnimVisibility = welcomeAnimVisibility
    )

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None)
            .value

    when (authenticationResult) {
        AuthenticationResult.None -> {
            Twig.debug { "Authentication result: initiating" }
            // Initial state
        }
        AuthenticationResult.Success -> {
            Twig.debug { "Authentication result: successful" }
            authenticationViewModel.resetAuthenticationResult()
            authenticationViewModel.setWelcomeAnimationDisplayed()
            goToAppContent()
        }
        AuthenticationResult.Canceled -> {
            Twig.info { "Authentication result: canceled: shutting down" }
            authenticationViewModel.resetAuthenticationResult()
            Toast
                .makeText(activity, stringResource(id = R.string.authentication_toast_canceled), Toast.LENGTH_SHORT)
                .show()
            onCancel()
        }
        AuthenticationResult.Failed -> {
            Twig.warn { "Authentication result: failed" }
            onFail()
        }
        is AuthenticationResult.Error -> {
            Twig.error {
                "Authentication result: error: ${authenticationResult.errorCode}: ${authenticationResult.errorMessage}"
            }
            onFail()
        }
    }

    // Starting authentication
    LaunchedEffect(key1 = true) {
        authenticationViewModel.authenticate(
            activity = activity,
            initialAuthSystemWindowDelay = APP_ACCESS_TRIGGER_DELAY.milliseconds,
            useCase = AuthenticationUseCase.AppAccess
        )
    }
}

sealed class AuthenticationUseCase {
    data object AppAccess : AuthenticationUseCase()

    data object SendFunds : AuthenticationUseCase()
}
