package co.electriccoin.zcash.ui.screen.account.model

import androidx.test.filters.SmallTest
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FiatCurrencyConversionRateState
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.totalBalance
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.test.getAppContext
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WalletDisplayValuesTest {
    @Test
    @SmallTest
    fun download_running_test() {
        val walletSnapshot =
            WalletSnapshotFixture.new(
                progress = PercentDecimal.ONE_HUNDRED_PERCENT,
                status = Synchronizer.Status.SYNCING,
                orchardBalance = WalletSnapshotFixture.ORCHARD_BALANCE,
                saplingBalance = WalletSnapshotFixture.SAPLING_BALANCE,
                transparentBalance = WalletSnapshotFixture.TRANSPARENT_BALANCE
            )
        val values =
            WalletDisplayValues.getNextValues(
                getAppContext(),
                walletSnapshot,
                false
            )

        assertNotNull(values)
        assertEquals(1f, values.progress.decimal)
        assertEquals(walletSnapshot.totalBalance().toZecString(), values.zecAmountText)
        assertTrue(values.statusText.startsWith(getStringResource(R.string.account_status_syncing_catchup)))
        // TODO [#578]: Provide Zatoshi -> USD fiat currency formatting
        // TODO [#578]: https://github.com/Electric-Coin-Company/zcash-android-wallet-sdk/issues/578
        assertEquals(FiatCurrencyConversionRateState.Unavailable, values.fiatCurrencyAmountState)
        assertEquals(
            getStringResource(R.string.fiat_currency_conversion_rate_unavailable),
            values.fiatCurrencyAmountText
        )
    }
}
