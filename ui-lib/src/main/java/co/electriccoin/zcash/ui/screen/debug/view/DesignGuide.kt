package co.electriccoin.zcash.ui.screen.debug.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.spackle.model.Progress
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.Chip
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.NavigationButton
import co.electriccoin.zcash.ui.design.component.PinkProgress
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SecondaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.onboarding.view.Callout

@Preview("DesignGuide")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        DesignGuide()
    }
}

@Composable
// Allowing magic numbers since this is debug-only
@Suppress("MagicNumber")
fun DesignGuide() {
    GradientSurface {
        Column {
            Header(text = "H1")
            Body(text = "body")
            NavigationButton(onClick = { }, text = "Back")
            NavigationButton(onClick = { }, text = "Next")
            PrimaryButton(onClick = { }, text = "Primary button", outerPaddingValues = PaddingValues(24.dp))
            SecondaryButton(onClick = { }, text = "Secondary button", outerPaddingValues = PaddingValues(24.dp))
            TertiaryButton(onClick = { }, text = "Tertiary button", outerPaddingValues = PaddingValues(24.dp))
            Callout(Icons.Filled.Shield, contentDescription = "Shield")
            Callout(Icons.Filled.Person, contentDescription = "Person")
            Callout(Icons.Filled.List, contentDescription = "List")
            PinkProgress(progress = Progress(Index(1), Index(4)), Modifier.fillMaxWidth())
            Chip(Index(1), "edict")
        }
    }
}
