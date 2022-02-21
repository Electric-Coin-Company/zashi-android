package cash.z.ecc.ui.screen.debug.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.ui.design.component.Body
import cash.z.ecc.ui.design.component.Chip
import cash.z.ecc.ui.design.component.GradientSurface
import cash.z.ecc.ui.design.component.Header
import cash.z.ecc.ui.design.component.NavigationButton
import cash.z.ecc.ui.design.component.PinkProgress
import cash.z.ecc.ui.design.component.PrimaryButton
import cash.z.ecc.ui.design.component.SecondaryButton
import cash.z.ecc.ui.design.component.TertiaryButton
import cash.z.ecc.ui.screen.onboarding.view.Callout
import cash.z.ecc.ui.theme.ZcashTheme
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.spackle.model.Progress

@Preview
@Composable
fun ComposablePreview() {
    ZcashTheme(darkTheme = false) {
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
            PrimaryButton(onClick = { }, text = "Primary button")
            SecondaryButton(onClick = { }, text = "Secondary button")
            TertiaryButton(onClick = { }, text = "Tertiary button")
            Callout(Icons.Filled.Shield, contentDescription = "Shield")
            Callout(Icons.Filled.Person, contentDescription = "Person")
            Callout(Icons.Filled.List, contentDescription = "List")
            PinkProgress(progress = Progress(Index(1), Index(4)), Modifier.fillMaxWidth())
            Chip(Index(1), "edict")
        }
    }
}
