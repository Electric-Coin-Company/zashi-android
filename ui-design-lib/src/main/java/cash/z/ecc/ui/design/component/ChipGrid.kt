package cash.z.ecc.ui.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import co.electriccoin.zcash.spackle.model.Index

// Note: Row size should probably change for landscape layouts
const val CHIP_GRID_ROW_SIZE = 3

@Composable
fun ChipGrid(wordList: List<String>) {
    Column(Modifier.testTag(CommonTag.CHIP_LAYOUT)) {
        wordList.chunked(CHIP_GRID_ROW_SIZE).forEachIndexed { chunkIndex, chunk ->
            Row(Modifier.fillMaxWidth()) {
                val remainder = (chunk.size % CHIP_GRID_ROW_SIZE)
                val singleItemWeight = 1f / CHIP_GRID_ROW_SIZE
                chunk.forEachIndexed { subIndex, word ->
                    Chip(
                        index = Index(chunkIndex * CHIP_GRID_ROW_SIZE + subIndex),
                        text = word,
                        modifier = Modifier.weight(singleItemWeight)
                    )
                }

                if (0 != remainder) {
                    Spacer(Modifier.weight((CHIP_GRID_ROW_SIZE - chunk.size) * singleItemWeight))
                }
            }
        }
    }
}
