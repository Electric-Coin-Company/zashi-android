package cash.z.ecc.ui.screen.debug.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.ui.screen.common.Body
import cash.z.ecc.ui.screen.common.Header
import cash.z.ecc.ui.screen.common.NavigationButton
import cash.z.ecc.ui.screen.common.PinkProgress
import cash.z.ecc.ui.screen.common.PrimaryButton
import cash.z.ecc.ui.screen.common.SecondaryButton
import cash.z.ecc.ui.screen.common.TertiaryButton
import cash.z.ecc.ui.screen.onboarding.model.Index
import cash.z.ecc.ui.screen.onboarding.model.Progress
import cash.z.ecc.ui.screen.onboarding.view.Callout
import cash.z.ecc.ui.theme.ZcashTheme

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
    Surface {
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
        }
    }
}
