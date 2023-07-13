package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.Typography

@Preview
@Composable
private fun ButtonComposablePreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Column {
                PrimaryButton(onClick = { }, text = "Primary")
                SecondaryButton(onClick = { }, text = "Secondary")
                TertiaryButton(onClick = { }, text = "Tertiary")
                NavigationButton(onClick = { }, text = "Navigation")
                DottedBorderTextButton(onClick = { }, text = "Scan a payment code")
                OutlinedPrimaryButton(onClick = { }, text = "Border Button")
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
        colors = buttonColors(containerColor = ZcashTheme.colors.tertiary),
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
fun OutlinedPrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    borderStroke: BorderStroke = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .padding(outerPaddingValues)
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
        ),
        shape = RoundedCornerShape(8.dp),
        border = borderStroke
    ) {
        Text(text = text, style = Typography.bodyMedium, color = Color.White)
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

@Composable
fun DottedBorderTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    @DrawableRes startIcon: Int? = null
) {
    Box(
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(44))
                .padding(2.dp)
        )
    ) {
        val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
        Canvas(modifier = Modifier
            .matchParentSize()
            .background(color = colorResource(id = R.color.ns_navy))
        ) {
            drawRoundRect(color = borderColor, style = stroke, cornerRadius = CornerRadius(x = 40f))
        }
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 12.dp, end = 12.dp),
        ) {
            if (startIcon != null) {
                Icon(painter = painterResource(id = startIcon), contentDescription = null, tint = borderColor)
                Spacer(modifier = Modifier.width(8.dp))
            }
            BodyMedium(text = text, textAlign = TextAlign.Center, color = ZcashTheme.colors.surfaceEnd)
        }
    }
}
