package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Composable
fun ZashiSeedWordText(
    prefix: String,
    state: SeedWordTextState,
    modifier: Modifier = Modifier,
    prefixContent: @Composable (Modifier, String) -> Unit = { mod, text -> ZashiSeedWordPrefixContent(text, mod) },
    content: @Composable (Modifier, String) -> Unit = { mod, text -> ZashiSeedWordTextContent(text, mod) }
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ZashiColors.Surfaces.bgSecondary,
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            prefixContent(Modifier, prefix)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                content(
                    Modifier.weight(1f),
                    state.text
                )
            }
        }
    }
}

@Composable
fun ZashiSeedWordPrefixContent(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier then Modifier.padding(start = 12.dp),
        text = text,
        color = ZashiColors.Text.textTertiary,
        style = ZashiTypography.textXs,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
fun ZashiSeedWordTextContent(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier then Modifier.padding(start = 32.dp, top = 8.dp, bottom = 10.dp),
    ) {
        Text(
            modifier = Modifier,
            text = text,
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textMd,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Immutable
data class SeedWordTextState(val text: String)

@Composable
@PreviewScreens
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiSeedWordText(
                modifier = Modifier.fillMaxWidth(),
                prefix = "11",
                state =
                    SeedWordTextState(
                        text = "asdasdasd",
                    )
            )
        }
    }
