package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.SeedPhrase
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

// TODO [#1001]: Row size should probably change for landscape layouts
// TODO [#1001]: https://github.com/Electric-Coin-Company/zashi-android/issues/1001
const val CHIP_GRID_COLUMN_SIZE = 12

@Preview
@Composable
private fun ChipGridPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            ChipGrid(
                SeedPhrase.new(WalletFixture.Alice.seedPhrase).split.toPersistentList(),
                onGridClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun ChipGridDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        BlankSurface {
            ChipGrid(
                SeedPhrase.new(WalletFixture.Alice.seedPhrase).split.toPersistentList(),
                onGridClick = {}
            )
        }
    }
}

@Composable
fun ChipGrid(
    wordList: ImmutableList<String>,
    onGridClick: () -> Unit,
    modifier: Modifier = Modifier,
    allowCopy: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier =
                Modifier
                    .wrapContentWidth()
                    .testTag(CommonTag.CHIP_LAYOUT)
                    .then(
                        if (allowCopy) {
                            Modifier
                                .clickable(
                                    interactionSource = interactionSource,
                                    // Disable ripple
                                    indication = null,
                                    onClick = onGridClick
                                )
                        } else {
                            Modifier
                        }
                    )
        ) {
            wordList.chunked(CHIP_GRID_COLUMN_SIZE).forEachIndexed { chunkIndex, chunk ->
                // TODO [#1043]: Correctly align numbers and words on Recovery screen
                // TODO [#1043]: https://github.com/Electric-Coin-Company/zashi-android/issues/1043
                Column(
                    modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingDefault)
                ) {
                    chunk.forEachIndexed { subIndex, word ->
                        ChipIndexed(
                            index = Index(chunkIndex * CHIP_GRID_COLUMN_SIZE + subIndex),
                            text = word,
                            modifier = Modifier.padding(ZcashTheme.dimens.spacingXtiny)
                        )
                    }
                }
            }
        }
    }
}
