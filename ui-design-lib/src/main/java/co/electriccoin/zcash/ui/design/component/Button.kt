package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.Typography

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
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .padding(outerPaddingValues)
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
        ),
        enabled = enabled,
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = colorResource(id = R.color.ns_navy),
            disabledContentColor = colorResource(id = R.color.ns_parmaviolet)
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            style = Typography.bodyMedium,
            text = text,
        )
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(outerPaddingValues)
        ),
        enabled = enabled,
        colors = buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            text = text,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun NavigationButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .padding(outerPaddingValues)
        ),
        colors = buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(style = MaterialTheme.typography.labelLarge, text = text, color = MaterialTheme.colorScheme.onSecondary)
    }
}

@Composable
fun TertiaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .padding(outerPaddingValues)
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
        ),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
        colors = buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            style = Typography.bodyMedium,
            text = text,
            color = ZcashTheme.colors.onTertiary
        )
    }
}

@Composable
fun DangerousButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(outerPaddingValues)
        ),
        colors = buttonColors(containerColor = ZcashTheme.colors.dangerous)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            text = text,
            color = ZcashTheme.colors.onDangerous
        )
    }
}
