package co.electriccoin.zcash.ui.screen.chooseserver.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.type.ServerValidation
import cash.z.ecc.sdk.extension.isValid
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.RadioButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.SubHeader
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Preview("Choose Server")
@Composable
private fun PreviewChooseServer() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            ChooseServer(
                availableServers = emptyList<LightWalletEndpoint>().toImmutableList(),
                onBack = {},
                onServerChange = {},
                snackbarHostState = SnackbarHostState(),
                validationResult = ServerValidation.Valid,
                wallet = PersistableWalletFixture.new(),
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun ChooseServer(
    availableServers: ImmutableList<LightWalletEndpoint>,
    onBack: () -> Unit,
    onServerChange: (LightWalletEndpoint) -> Unit,
    snackbarHostState: SnackbarHostState,
    validationResult: ServerValidation,
    wallet: PersistableWallet,
) {
    Scaffold(
        topBar = {
            ChooseServerTopAppBar(onBack = onBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        ChooseServerMainContent(
            modifier =
                Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
                    .fillMaxWidth(),
            availableServers = availableServers,
            onServerChange = onServerChange,
            validationResult = validationResult,
            wallet = wallet,
        )
    }
}

@Composable
private fun ChooseServerTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        backText = stringResource(id = R.string.choose_server_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.choose_server_back_content_description),
        onBack = onBack,
        showTitleLogo = true,
        modifier = Modifier.testTag(ChooseServerTag.CHOOSE_SERVER_TOP_APP_BAR)
    )
}

@Composable
@Suppress("LongMethod")
private fun ChooseServerMainContent(
    availableServers: ImmutableList<LightWalletEndpoint>,
    onServerChange: (LightWalletEndpoint) -> Unit,
    validationResult: ServerValidation,
    wallet: PersistableWallet,
    modifier: Modifier = Modifier,
) {
    val options =
        availableServers.toMutableList().apply {
            if (contains(wallet.endpoint)) {
                // We define the custom server as secured by default
                add(LightWalletEndpoint("", -1, true))
            } else {
                // Adding previously chosen custom endpoint
                add(wallet.endpoint)
            }
        }.toImmutableList()

    val (selectedOption, setSelectedOption) =
        rememberSaveable {
            mutableIntStateOf(options.indexOf(wallet.endpoint))
        }

    val initialCustomServerValue =
        options.last().run {
            if (options.last().isValid()) {
                stringResource(R.string.choose_server_textfield_value, options.last().host, options.last().port)
            } else {
                ""
            }
        }
    val (customServerValue, setCustomServerValue) =
        rememberSaveable {
            mutableStateOf(initialCustomServerValue)
        }

    val (customServerError, setCustomServerError) =
        rememberSaveable {
            mutableStateOf<String?>(null)
        }

    val context = LocalContext.current

    LaunchedEffect(key1 = validationResult) {
        when (validationResult) {
            is ServerValidation.InValid -> {
                setCustomServerError(context.getString(R.string.choose_server_textfield_error))
            }
            else -> {
                // Expected state: do nothing
            }
        }
    }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        SubHeader(
            text = stringResource(id = R.string.choose_server_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        ServerList(
            options = options,
            selectedOption = selectedOption,
            setSelectedOption = setSelectedOption,
            customServerError = customServerError,
            setCustomServerError = setCustomServerError,
            customServerValue = customServerValue,
            setCustomServerValue = setCustomServerValue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        SaveButton(
            enabled = validationResult != ServerValidation.Running,
            options = options,
            customServerValue = customServerValue,
            onServerChange = {
                // Check if the selected is different from the current
                if (it != wallet.endpoint) {
                    onServerChange(it)
                }
            },
            selectedOption = selectedOption,
            setCustomServerError = setCustomServerError,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingXlarge)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("LongParameterList")
fun ServerList(
    options: ImmutableList<LightWalletEndpoint>,
    customServerError: String?,
    setCustomServerError: (String?) -> Unit,
    customServerValue: String,
    setCustomServerValue: (String) -> Unit,
    selectedOption: Int,
    setSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        options.forEachIndexed { index, endpoint ->
            val isSelected = index == selectedOption

            if (index == options.lastIndex) {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    LabeledRadioButton(
                        endpoint = endpoint,
                        changeClick = { setSelectedOption(index) },
                        name = stringResource(id = R.string.choose_server_custom),
                        selected = isSelected,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSelected) {
                        val focusManager = LocalFocusManager.current

                        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

                        FormTextField(
                            value = customServerValue,
                            onValueChange = {
                                setCustomServerError(null)
                                setCustomServerValue(it)
                            },
                            placeholder = {
                                Text(text = stringResource(R.string.choose_server_textfield_hint))
                            },
                            error = customServerError,
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus(true)
                                    }
                                ),
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Uri,
                                    imeAction = ImeAction.Done
                                ),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = ZcashTheme.dimens.spacingSmall)
                        )
                    }
                }
            } else {
                LabeledRadioButton(
                    endpoint = endpoint,
                    changeClick = { setSelectedOption(index) },
                    name = stringResource(id = R.string.choose_server_textfield_value, endpoint.host, endpoint.port),
                    selected = isSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun String.toEndpoint(delimiter: String): LightWalletEndpoint {
    val parts = split(delimiter)
    return LightWalletEndpoint(parts[0], parts[1].toInt(), true)
}

@Composable
fun LabeledRadioButton(
    name: String,
    endpoint: LightWalletEndpoint,
    selected: Boolean,
    changeClick: (LightWalletEndpoint) -> Unit,
    modifier: Modifier = Modifier
) {
    RadioButton(
        text = name,
        selected = selected,
        onClick = { changeClick(endpoint) },
        modifier = modifier
    )
}

// This regex validates server URLs with ports while ensuring:
// - Valid hostname format (excluding spaces and special characters)
// - Port numbers within the valid range (1-65535) and without leading zeros
// - Note that this does not cover other URL components like paths or query strings
val regex = "^(([^:/?#\\s]+)://)?([^/?#\\s]+):([1-9][0-9]{3}|[1-5][0-9]{2}|[0-9]{1,2})$".toRegex()

fun validateCustomServerValue(customServer: String): Boolean = regex.matches(customServer)

@Composable
@Suppress("LongParameterList")
fun SaveButton(
    enabled: Boolean,
    customServerValue: String,
    onServerChange: (LightWalletEndpoint) -> Unit,
    options: ImmutableList<LightWalletEndpoint>,
    selectedOption: Int,
    setCustomServerError: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    PrimaryButton(
        enabled = enabled,
        showProgressBar = !enabled,
        text = stringResource(id = R.string.choose_server_save),
        onClick = {
            val selectedServer =
                if (selectedOption == options.lastIndex) {
                    if (!validateCustomServerValue(customServerValue)) {
                        setCustomServerError(context.getString(R.string.choose_server_textfield_error))
                        return@PrimaryButton
                    }

                    customServerValue.toEndpoint(context.getString(R.string.choose_server_custom_delimiter))
                } else {
                    options[selectedOption]
                }

            Twig.info { "Choose Server: Selected server: $selectedServer" }

            onServerChange(selectedServer)
        },
        modifier = modifier
    )
}
