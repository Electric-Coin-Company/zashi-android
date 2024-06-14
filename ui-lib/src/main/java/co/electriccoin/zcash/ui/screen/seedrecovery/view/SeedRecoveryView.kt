package co.electriccoin.zcash.ui.screen.seedrecovery.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.SecureScreen
import co.electriccoin.zcash.ui.common.compose.shouldSecureScreen
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.test.CommonTag.WALLET_BIRTHDAY
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.ChipGrid
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.GridBgSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TopScreenLogoTitle
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import kotlinx.collections.immutable.toPersistentList

@Preview(name = "SeedRecovery", device = Devices.PIXEL_4)
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        SeedRecovery(
            PersistableWalletFixture.new(),
            onBack = {},
            onBirthdayCopy = {},
            onDone = {},
            onSeedCopy = {},
            versionInfo = VersionInfoFixture.new(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

// TODO [#998]: Check and enhance screen dark mode
// TODO [#998]: https://github.com/Electric-Coin-Company/zashi-android/issues/998

/**
 * @param onDone Callback when the user has confirmed viewing the seed phrase.
 */
@Composable
@Suppress("LongParameterList")
fun SeedRecovery(
    wallet: PersistableWallet,
    onBack: () -> Unit,
    onBirthdayCopy: () -> Unit,
    onDone: () -> Unit,
    onSeedCopy: () -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    versionInfo: VersionInfo,
) {
    GridBgScaffold(
        topBar = {
            SeedRecoveryTopAppBar(
                onBack = onBack,
                onSeedCopy = onSeedCopy,
                versionInfo = versionInfo,
                subTitleState = topAppBarSubTitleState,
            )
        }
    ) { paddingValues ->
        SeedRecoveryMainContent(
            wallet = wallet,
            onDone = onDone,
            onSeedCopy = onSeedCopy,
            onBirthdayCopy = onBirthdayCopy,
            versionInfo = versionInfo,
            // Horizontal paddings will be part of each UI element to minimize a possible truncation on very
            // small screens
            modifier =
                Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        )
    }
}

@Composable
private fun SeedRecoveryTopAppBar(
    onBack: () -> Unit,
    onSeedCopy: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier,
) {
    GridBgSmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = modifier,
        backText = stringResource(id = R.string.seed_recovery_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.seed_recovery_back_content_description),
        onBack = onBack,
        regularActions = {
            if (versionInfo.isDebuggable && !versionInfo.isRunningUnderTestService) {
                DebugMenu(
                    onCopyToClipboard = onSeedCopy
                )
            }
        },
    )
}

@Composable
private fun DebugMenu(onCopyToClipboard: () -> Unit) {
    Column(
        modifier = Modifier.testTag(SeedRecoveryTag.DEBUG_MENU_TAG)
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(stringResource(id = R.string.seed_recovery_copy))
                },
                onClick = {
                    onCopyToClipboard()
                    expanded = false
                }
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun SeedRecoveryMainContent(
    wallet: PersistableWallet,
    onSeedCopy: () -> Unit,
    onBirthdayCopy: () -> Unit,
    onDone: () -> Unit,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopScreenLogoTitle(
            title = stringResource(R.string.seed_recovery_header),
            logoContentDescription = stringResource(R.string.zcash_logo_content_description),
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingBig)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        BodySmall(
            text = stringResource(R.string.seed_recovery_description),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingBig)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        SeedRecoverySeedPhrase(
            persistableWallet = wallet,
            onSeedCopy = onSeedCopy,
            onBirthdayCopy = onBirthdayCopy,
            versionInfo = versionInfo,
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onClick = onDone,
            text = stringResource(R.string.seed_recovery_button_finished),
            modifier =
                Modifier
                    .padding(
                        bottom = ZcashTheme.dimens.spacingHuge,
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    )
                    .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SeedRecoverySeedPhrase(
    persistableWallet: PersistableWallet,
    onSeedCopy: () -> Unit,
    onBirthdayCopy: () -> Unit,
    versionInfo: VersionInfo,
) {
    if (shouldSecureScreen) {
        SecureScreen()
    }

    Column {
        ChipGrid(
            wordList = persistableWallet.seedPhrase.split.toPersistentList(),
            onGridClick = onSeedCopy,
            allowCopy = versionInfo.isDebuggable && !versionInfo.isRunningUnderTestService,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        persistableWallet.birthday?.let {
            val interactionSource = remember { MutableInteractionSource() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BodySmall(
                    text = stringResource(R.string.seed_recovery_birthday_height, it.value),
                    modifier =
                        Modifier
                            .testTag(WALLET_BIRTHDAY)
                            .padding(horizontal = ZcashTheme.dimens.spacingDefault)
                            .basicMarquee()
                            // Apply click callback to the text only as the wrapping layout can be much wider
                            .clickable(
                                interactionSource = interactionSource,
                                // Disable ripple
                                indication = null,
                                onClick = onBirthdayCopy
                            )
                )
            }
        }
    }
}
