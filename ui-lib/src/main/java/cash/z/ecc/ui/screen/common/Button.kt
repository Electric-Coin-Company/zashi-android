package cash.z.ecc.ui.screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.ui.theme.ZcashTheme

@Preview
@Composable
fun ButtonComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Column {
                PrimaryButton(onClick = { }, text = "Primary")
                SecondaryButton(onClick = { }, text = "Secondary")
                TertiaryButton(onClick = { }, text = "Tertiary")
                NavigationButton(onClick = { }, text = "Navigation")
            }
        }
    }
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ),
        colors = buttonColors(backgroundColor = MaterialTheme.colors.primary)
    ) {
        Text(style = MaterialTheme.typography.button, text = text, color = MaterialTheme.colors.onPrimary)
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ),
        colors = buttonColors(backgroundColor = MaterialTheme.colors.secondary)
    ) {
        Text(style = MaterialTheme.typography.button, text = text, color = MaterialTheme.colors.onSecondary)
    }
}

@Composable
fun NavigationButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ),
        colors = buttonColors(backgroundColor = MaterialTheme.colors.secondary)
    ) {
        Text(style = MaterialTheme.typography.button, text = text, color = MaterialTheme.colors.onSecondary)
    }
}

@Composable
fun TertiaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
        colors = buttonColors(backgroundColor = ZcashTheme.colors.tertiary)
    ) {
        Text(style = MaterialTheme.typography.button, text = text, color = ZcashTheme.colors.onTertiary)
    }
}

@Composable
fun DangerousButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ),
        colors = buttonColors(backgroundColor = ZcashTheme.colors.dangerous)
    ) {
        Text(style = MaterialTheme.typography.button, text = text, color = ZcashTheme.colors.onDangerous)
    }
}
