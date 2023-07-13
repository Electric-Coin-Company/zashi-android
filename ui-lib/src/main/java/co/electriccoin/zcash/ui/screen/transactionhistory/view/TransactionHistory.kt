package co.electriccoin.zcash.ui.screen.transactionhistory.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.TransactionOverview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@Preview
@Composable
fun TransactionHistoryPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            val list = listOf(TransactionOverviewFixture.new(memoCount = 2, isSentTransaction = true), TransactionOverviewFixture.new())
            TransactionHistory(transactionSnapshot = list.toPersistentList(), onBack = {}, onTransactionDetail = {}, onItemLongClick = {})
        }
    }
}

@Composable
fun TransactionHistory(transactionSnapshot: ImmutableList<TransactionOverview>, onBack: () -> Unit, onTransactionDetail: (Long) -> Unit, onItemLongClick: (TransactionOverview) -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        Box {
            IconButton(onClick = onBack, modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.receive_back_content_description))
            }
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(id = R.dimen.back_icon_size)), contentAlignment = Alignment.Center) {
                TitleLarge(text = stringResource(id = R.string.ns_transaction_history), textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.screen_standard_margin)))
        LazyColumn {
            items(transactionSnapshot) { transactionOverview ->
                TransactionOverviewHistoryRow(transactionOverview = transactionOverview, onItemClick = {onTransactionDetail(it.id)}, onItemLongClick = onItemLongClick)
            }
        }
    }
}
