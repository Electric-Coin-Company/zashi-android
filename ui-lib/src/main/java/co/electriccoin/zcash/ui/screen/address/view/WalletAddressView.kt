@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.address.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressesFixture
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.ListHeader
import co.electriccoin.zcash.ui.design.component.ListItem
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.runBlocking

@Preview("WalletAddresses")
@Composable
private fun ComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            WalletAddresses(
                runBlocking { WalletAddressesFixture.new() },
                onBack = {}
            )
        }
    }
}

@Composable
fun WalletAddresses(walletAddresses: WalletAddresses, onBack: () -> Unit) {
    Column {
        WalletDetailTopAppBar(onBack)
        WalletDetailAddresses(
            walletAddresses,
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun WalletDetailTopAppBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.wallet_address_title)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.wallet_address_back_content_description)
                )
            }
        }
    )
}

private val BIG_INDICATOR_WIDTH = 24.dp
private val SMALL_INDICATOR_WIDTH = 16.dp

@Composable
private fun WalletDetailAddresses(
    walletAddresses: WalletAddresses,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Image(
                painter = ColorPainter(ZcashTheme.colors.highlight),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(BIG_INDICATOR_WIDTH)
            )

            Column(Modifier.fillMaxWidth()) {
                ExpandableRow(
                    title = stringResource(R.string.wallet_address_unified),
                    content = walletAddresses.unified.address,
                    isInitiallyExpanded = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = ZcashTheme.dimens.spacingDefault,
                            vertical = ZcashTheme.dimens.spacingTiny
                        )
                )

                Box(Modifier.height(IntrinsicSize.Min)) {
                    Divider(modifier = Modifier.fillMaxHeight())
                    ListHeader(
                        text = stringResource(R.string.wallet_address_header_includes),
                        modifier = Modifier.padding(all = ZcashTheme.dimens.spacingSmall)
                    )
                }

                SaplingAddress(
                    saplingAddress = walletAddresses.sapling.address,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                )

                TransparentAddress(
                    transparentAddress = walletAddresses.transparent.address,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                )
            }
        }
    }
}

// Note: The addresses code below has opportunities to be made more DRY.
// Refactoring that is being held off until issue #160 is fixed, since knowledge
// of row position will be needed.

@Composable
private fun SaplingAddress(
    saplingAddress: String,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        SmallIndicator(ZcashTheme.colors.addressHighlightSapling)

        ExpandableRow(
            title = stringResource(R.string.wallet_address_sapling),
            content = saplingAddress,
            isInitiallyExpanded = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ZcashTheme.dimens.spacingDefault,
                    vertical = ZcashTheme.dimens.spacingTiny
                )
        )
    }
}

@Composable
private fun TransparentAddress(
    transparentAddress: String,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        SmallIndicator(ZcashTheme.colors.addressHighlightTransparent)
        ExpandableRow(
            title = stringResource(R.string.wallet_address_transparent),
            content = transparentAddress,
            isInitiallyExpanded = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ZcashTheme.dimens.spacingDefault,
                    vertical = ZcashTheme.dimens.spacingTiny
                )
        )
    }
}

@Composable
private fun ExpandableRow(
    title: String,
    content: String,
    isInitiallyExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    var expandedState by rememberSaveable { mutableStateOf(isInitiallyExpanded) }

    Column(
        Modifier
            .clickable { expandedState = !expandedState }
            .then(modifier) // To have proper ripple effect
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.defaultMinSize(minHeight = 48.dp)) {
            ListItem(text = title)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(MINIMAL_WEIGHT)
            )
            ExpandableArrow(expandedState)
        }
        if (expandedState) {
            Body(content)
        }
    }
}

@Composable
private fun SmallIndicator(color: Color) {
    // TODO [#160]: Border is not the right implementation here, as it causes double thickness for the middle item
    Image(
        modifier = Modifier
            .fillMaxHeight()
            .width(SMALL_INDICATOR_WIDTH)
            .border(1.dp, ZcashTheme.colors.addressHighlightBorder),
        painter = ColorPainter(color),
        contentDescription = ""
    )
}

private const val NINETY_DEGREES = 90f

@Composable
private fun ExpandableArrow(isExpanded: Boolean) {
    Icon(
        imageVector = Icons.Filled.ArrowDropDownCircle,
        contentDescription = if (isExpanded) {
            stringResource(id = R.string.wallet_address_hide)
        } else {
            stringResource(id = R.string.wallet_address_show)
        },
        modifier = if (isExpanded) {
            Modifier
        } else {
            Modifier.rotate(NINETY_DEGREES)
        },
        tint = MaterialTheme.colorScheme.onBackground
    )
}
