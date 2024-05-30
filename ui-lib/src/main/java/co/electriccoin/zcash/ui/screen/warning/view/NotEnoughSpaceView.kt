package co.electriccoin.zcash.ui.screen.warning.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GridBgColumn
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("NotEnoughSpace")
@Composable
private fun NotEnoughSpacePreview() {
    ZcashTheme(forceDarkMode = false) {
        NotEnoughSpaceView(
            storageSpaceRequiredGigabytes = 1,
            spaceRequiredToContinueMegabytes = 300
        )
    }
}

@Composable
fun NotEnoughSpaceView(
    storageSpaceRequiredGigabytes: Int,
    spaceRequiredToContinueMegabytes: Int
) {
    GridBgColumn(
        Modifier
            .fillMaxSize()
            .padding(ZcashTheme.dimens.screenHorizontalSpacingRegular)
            .verticalScroll(
                rememberScrollState()
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.not_enough_space),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Header(text = stringResource(id = R.string.not_enough_space_title))

        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Body(
            text = stringResource(id = R.string.not_enough_space_description, storageSpaceRequiredGigabytes),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingHuge))

        Small(
            text = stringResource(id = R.string.space_required_to_continue, spaceRequiredToContinueMegabytes),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
