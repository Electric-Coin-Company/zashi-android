package cash.z.ecc.ui.screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.ui.screen.onboarding.model.Index
import cash.z.ecc.ui.theme.MINIMAL_WEIGHT

const val CHIP_GRID_ROW_SIZE = 3

@Composable
fun ChipGrid(persistableWallet: PersistableWallet) {
    Column {
        persistableWallet.seedPhrase.split.chunked(CHIP_GRID_ROW_SIZE).forEachIndexed { chunkIndex, chunk ->
            Row(Modifier.fillMaxWidth()) {
                chunk.forEachIndexed { subIndex, word ->
                    Chip(
                        index = Index(chunkIndex * CHIP_GRID_ROW_SIZE + subIndex),
                        text = word,
                        modifier = Modifier.weight(MINIMAL_WEIGHT)
                    )
                }
            }
        }
    }
}
