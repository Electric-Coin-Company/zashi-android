@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.authentication

import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationResult
import co.electriccoin.zcash.ui.common.viewmodel.AuthenticationViewModel
import co.electriccoin.zcash.ui.screen.authentication.view.AppAccessAuthentication
import co.electriccoin.zcash.ui.screen.authentication.view.AuthenticationErrorDialog
import co.electriccoin.zcash.ui.screen.authentication.view.AuthenticationFailedDialog
import kotlin.time.Duration.Companion.milliseconds

private const val APP_ACCESS_TRIGGER_DELAY = 0
private const val DELETE_WALLET_TRIGGER_DELAY = 0
private const val EXPORT_PRIVATE_DATA_TRIGGER_DELAY = 0
private const val SEED_RECOVERY_TRIGGER_DELAY = 0
private const val SEND_FUNDS_DELAY = 0
private const val RETRY_TRIGGER_DELAY = 0

@Composable
internal fun MainActivity.WrapAuthentication(
    goSupport: () -> Unit,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
    useCase: AuthenticationUseCase,
) {
    WrapAuthenticationUseCases(
        activity = this,
        goSupport = goSupport,
        onSuccess = onSuccess,
        onCancel = onCancel,
        onFailed = onFailed,
        useCase = useCase
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapAuthenticationUseCases(
    activity: MainActivity,
    goSupport: () -> Unit,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
    useCase: AuthenticationUseCase,
) {
    when (useCase) {
        AuthenticationUseCase.AppAccess -> {
            Twig.debug { "App Access Authentication" }
            WrapAppAccessAuth(
                activity = activity,
                goToAppContent = onSuccess,
                goSupport = goSupport,
                onCancel = onCancel,
                onFailed = onFailed
            )
        }
        AuthenticationUseCase.ExportPrivateData -> {
            Twig.debug { "Export Private Data Authentication" }
            WrapAppExportPrivateDataAuth(
                activity = activity,
                goExportPrivateData = onSuccess,
                goSupport = goSupport,
                onCancel = onCancel,
                onFailed = onFailed
            )
        }
        AuthenticationUseCase.DeleteWallet -> {
            Twig.debug { "Delete Wallet Authentication" }
            WrapDeleteWalletAuth(
                activity = activity,
                goDeleteWallet = onSuccess,
                goSupport = goSupport,
                onCancel = onCancel,
                onFailed = onFailed
            )
        }
        AuthenticationUseCase.SeedRecovery -> {
            Twig.debug { "Seed Recovery Authentication" }
            WrapSeedRecoveryAuth(
                activity = activity,
                goSeedRecovery = onSuccess,
                goSupport = goSupport,
                onCancel = onCancel,
                onFailed = onFailed
            )
        }
        AuthenticationUseCase.SendFunds -> {
            Twig.debug { "Send Funds Authentication" }
            WrapSendFundsAuth(
                activity = activity,
                onSendFunds = onSuccess,
                goSupport = goSupport,
                onCancel = onCancel,
                onFailed = onFailed
            )
        }
    }
}

@Composable
private fun WrapDeleteWalletAuth(
    activity: MainActivity,
    goSupport: () -> Unit,
    goDeleteWallet: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
) {
    val authenticationViewModel by activity.viewModels<AuthenticationViewModel>()

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None).value

    when (authenticationResult) {
        AuthenticationResult.None -> {
            Twig.info { "Authentication result: initiating" }
            // Initial state
        }
        AuthenticationResult.Success -> {
            Twig.info { "Authentication result: successful" }
            authenticationViewModel.resetAuthenticationResult()
            goDeleteWallet()
        }
        AuthenticationResult.Canceled -> {
            Twig.info { "Authentication result: canceled" }
            authenticationViewModel.resetAuthenticationResult()
            onCancel()
        }
        AuthenticationResult.Failed -> {
            Twig.warn { "Authentication result: failed" }
            authenticationViewModel.resetAuthenticationResult()
            onFailed()
            Toast.makeText(activity, activity.getString(R.string.authentication_toast_failed), Toast.LENGTH_SHORT)
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
                        useCase = AuthenticationUseCase.DeleteWallet
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
            initialAuthSystemWindowDelay = DELETE_WALLET_TRIGGER_DELAY.milliseconds,
            useCase = AuthenticationUseCase.DeleteWallet
        )
    }
}

@Composable
private fun WrapAppExportPrivateDataAuth(
    activity: MainActivity,
    goSupport: () -> Unit,
    goExportPrivateData: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
) {
    val authenticationViewModel by activity.viewModels<AuthenticationViewModel>()

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None).value

    when (authenticationResult) {
        AuthenticationResult.None -> {
            Twig.info { "Authentication result: initiating" }
            // Initial state
        }
        AuthenticationResult.Success -> {
            Twig.info { "Authentication result: successful" }
            authenticationViewModel.resetAuthenticationResult()
            goExportPrivateData()
        }
        AuthenticationResult.Canceled -> {
            Twig.info { "Authentication result: canceled" }
            authenticationViewModel.resetAuthenticationResult()
            onCancel()
        }
        AuthenticationResult.Failed -> {
            Twig.warn { "Authentication result: failed" }
            authenticationViewModel.resetAuthenticationResult()
            onFailed()
            Toast.makeText(activity, stringResource(id = R.string.authentication_toast_failed), Toast.LENGTH_SHORT)
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
                        useCase = AuthenticationUseCase.ExportPrivateData
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
            initialAuthSystemWindowDelay = EXPORT_PRIVATE_DATA_TRIGGER_DELAY.milliseconds,
            useCase = AuthenticationUseCase.ExportPrivateData
        )
    }
}

@Composable
private fun WrapSeedRecoveryAuth(
    activity: MainActivity,
    goSupport: () -> Unit,
    goSeedRecovery: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
) {
    val authenticationViewModel by activity.viewModels<AuthenticationViewModel>()

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None).value

    when (authenticationResult) {
        AuthenticationResult.None -> {
            Twig.info { "Authentication result: initiating" }
            // Initial state
        }
        AuthenticationResult.Success -> {
            Twig.info { "Authentication result: successful" }
            authenticationViewModel.resetAuthenticationResult()
            goSeedRecovery()
        }
        AuthenticationResult.Canceled -> {
            Twig.info { "Authentication result: canceled" }
            authenticationViewModel.resetAuthenticationResult()
            onCancel()
        }
        AuthenticationResult.Failed -> {
            Twig.warn { "Authentication result: failed" }
            authenticationViewModel.resetAuthenticationResult()
            onFailed()
            Toast.makeText(activity, stringResource(id = R.string.authentication_toast_failed), Toast.LENGTH_SHORT)
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
                        useCase = AuthenticationUseCase.SeedRecovery
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
            initialAuthSystemWindowDelay = SEED_RECOVERY_TRIGGER_DELAY.milliseconds,
            useCase = AuthenticationUseCase.SeedRecovery
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun WrapSendFundsAuth(
    activity: MainActivity,
    goSupport: () -> Unit,
    onSendFunds: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
) {
    val authenticationViewModel by activity.viewModels<AuthenticationViewModel>()

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None).value

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
            onFailed()
            Toast.makeText(activity, stringResource(id = R.string.authentication_toast_failed), Toast.LENGTH_SHORT)
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
    activity: MainActivity,
    goSupport: () -> Unit,
    goToAppContent: () -> Unit,
    onCancel: () -> Unit,
    onFailed: () -> Unit,
) {
    val authenticationViewModel by activity.viewModels<AuthenticationViewModel>()

    val welcomeAnimVisibility = authenticationViewModel.showWelcomeAnimation.collectAsStateWithLifecycle().value

    AppAccessAuthentication(welcomeAnimVisibility = welcomeAnimVisibility)

    val authenticationResult =
        authenticationViewModel.authenticationResult
            .collectAsStateWithLifecycle(initialValue = AuthenticationResult.None).value

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
            Toast.makeText(activity, stringResource(id = R.string.authentication_toast_canceled), Toast.LENGTH_SHORT)
                .show()
            onCancel()
        }
        AuthenticationResult.Failed -> {
            Twig.warn { "Authentication result: failed" }
            onFailed()
            AuthenticationFailedDialog(
                onDismiss = {
                    authenticationViewModel.resetAuthenticationResult()
                    onCancel()
                },
                onRetry = {
                    authenticationViewModel.resetAuthenticationResult()
                    authenticationViewModel.authenticate(
                        activity = activity,
                        initialAuthSystemWindowDelay = RETRY_TRIGGER_DELAY.milliseconds,
                        useCase = AuthenticationUseCase.AppAccess
                    )
                },
                onSupport = {
                    authenticationViewModel.resetAuthenticationResult()
                    goSupport()
                }
            )
        }
        is AuthenticationResult.Error -> {
            Twig.error {
                "Authentication result: error: ${authenticationResult.errorCode}: ${authenticationResult.errorMessage}"
            }
            AuthenticationErrorDialog(
                onDismiss = {
                    authenticationViewModel.resetAuthenticationResult()
                    onCancel()
                },
                onRetry = {
                    authenticationViewModel.resetAuthenticationResult()
                    authenticationViewModel.authenticate(
                        activity = activity,
                        initialAuthSystemWindowDelay = RETRY_TRIGGER_DELAY.milliseconds,
                        useCase = AuthenticationUseCase.AppAccess
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
            initialAuthSystemWindowDelay = APP_ACCESS_TRIGGER_DELAY.milliseconds,
            useCase = AuthenticationUseCase.AppAccess
        )
    }
}

sealed class AuthenticationUseCase {
    data object AppAccess : AuthenticationUseCase()

    data object SeedRecovery : AuthenticationUseCase()

    data object DeleteWallet : AuthenticationUseCase()

    data object ExportPrivateData : AuthenticationUseCase()

    data object SendFunds : AuthenticationUseCase()
}
