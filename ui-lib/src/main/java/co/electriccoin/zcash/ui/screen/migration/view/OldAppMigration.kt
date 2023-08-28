package co.electriccoin.zcash.ui.screen.migration.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.PersistableWallet
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.migration.model.AppMigrationState

@Composable
@Preview
fun OldAppMigrationPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            OldAppMigration(
                appMigrationState = AppMigrationState.Idle,
                onStartMigration = { },
                onExit = { },
                onDataRecovered = { }
            )
        }
    }
}

@Composable
fun OldAppMigration(
    appMigrationState: AppMigrationState,
    onStartMigration: () -> Unit,
    onExit: () -> Unit,
    onDataRecovered: (PersistableWallet) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        Spacer(Modifier.height(dimensionResource(id = R.dimen.back_icon_size)))
        Image(
            painter = painterResource(id = R.drawable.ic_nighthawk_logo),
            contentDescription = "logo", contentScale = ContentScale.Inside,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        TitleLarge(
            text = stringResource(id = R.string.ns_nighthawk),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
        BodyMedium(
            text = stringResource(id = R.string.ns_migration_app_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = ZcashTheme.colors.surfaceEnd
        )
        Spacer(modifier = Modifier.height(40.dp))

        when (appMigrationState) {
            is AppMigrationState.Error -> {
                BodyMedium(
                    text = stringResource(id = R.string.ns_migration_app_error),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(60.dp))
                Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
                TertiaryButton(
                    onClick = onExit,
                    text = stringResource(id = R.string.ns_exit),
                    modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(min = 180.dp)
                )
            }

            AppMigrationState.Idle -> {
                BodyMedium(
                    text = stringResource(id = R.string.ns_migration_app_info),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(60.dp))
                PrimaryButton(
                    onClick = onStartMigration,
                    text = stringResource(id = R.string.ns_continue),
                    modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(min = 180.dp)
                )
                TertiaryButton(
                    onClick = onExit,
                    text = stringResource(id = R.string.ns_exit),
                    modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(min = 180.dp)
                )
            }

            is AppMigrationState.DataRecovered -> {
                Twig.info { "DataRecovered ${appMigrationState.persistableWallet}" }
                onDataRecovered(appMigrationState.persistableWallet)
            }
        }
    }
}
