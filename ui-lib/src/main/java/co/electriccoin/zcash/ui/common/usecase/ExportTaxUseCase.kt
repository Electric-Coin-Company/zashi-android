package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVE_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.RECEIVING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SEND_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SENT
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDED
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING
import co.electriccoin.zcash.ui.common.repository.TransactionExtendedState.SHIELDING_FAILED
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.util.FileShareUtil
import co.electriccoin.zcash.ui.util.FileShareUtil.ZASHI_INTERNAL_DATA_MIME_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import java.time.Year
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ExportTaxUseCase(
    private val transactionRepository: TransactionRepository,
    private val accountDataSource: AccountDataSource,
    private val versionInfoProvider: GetVersionInfoProvider,
    private val context: Context,
    private val navigationRouter: NavigationRouter,
) {
    suspend operator fun invoke() =
        withContext(Dispatchers.IO) {
            val previousYear =
                Year.now().minusYears(1)
                    .let {
                        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
                        it.format(formatter)
                    }

            val outputFile =
                File(
                    context.cacheDir,
                    when (accountDataSource.getSelectedAccount()) {
                        is KeystoneAccount -> "Keystone_Transaction_History_$previousYear.csv"
                        is ZashiAccount -> "Zashi_Transaction_History_$previousYear.csv"
                    }
                )

            writeCsvToFile(
                outputFile = outputFile,
                data = getCsvEntries()
            )

            runCatching {
                val intent =
                    FileShareUtil.newShareContentIntent(
                        context = context,
                        file = outputFile,
                        shareText = "Tax export",
                        sharePickerText = "Tax export",
                        versionInfo = versionInfoProvider(),
                        fileType = ZASHI_INTERNAL_DATA_MIME_TYPE
                    )

                context.startActivity(intent)
            }

            navigationRouter.back()
        }

    private fun writeCsvToFile(
        outputFile: File,
        data: List<CsvEntry>
    ) {
        outputFile.outputStream().bufferedWriter().use { writer ->
            writer.write(
                listOf(
                    "Date",
                    "Received Quantity",
                    "Received Currency",
                    "Sent Quantity",
                    "Sent Currency",
                    "Fee Amount",
                    "Fee Currency",
                    "Tag"
                ).joinToString(separator = CSV_SEPARATOR)
            )
            writer.newLine()

            data.forEach {
                writer.write(
                    listOf(
                        it.date,
                        it.receivedQuantity,
                        it.receivedCurrency,
                        it.sentQuantity,
                        it.sentCurrency,
                        it.feeAmount,
                        it.feeCurrency,
                        it.tag,
                    ).joinToString(separator = CSV_SEPARATOR)
                )
                writer.newLine()
            }

            writer.flush()
        }
    }

    private suspend fun getCsvEntries() =
        transactionRepository.getTransactions()
            .mapNotNull { transaction ->
                val previousYear = Year.now().minusYears(1)

                val date =
                    transaction.overview.blockTimeEpochSeconds
                        ?.let {
                            Instant.ofEpochSecond(it).atZone(ZoneId.of("UTC"))
                        } ?: return@mapNotNull null

                if (date.year != previousYear.value) return@mapNotNull null

                val dateString =
                    date.let {
                        val formatter =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
                                .withZone(ZoneOffset.UTC)

                        formatter.format(date)
                    } ?: return@mapNotNull null

                when (transaction.state) {
                    SENT,
                    SENDING -> {
                        val fee = transaction.overview.feePaid

                        val sentQuantity =
                            if (fee != null) {
                                stringRes(transaction.overview.netValue - fee)
                            } else {
                                stringRes(transaction.overview.netValue)
                            }

                        CsvEntry(
                            date = dateString,
                            receivedQuantity = "",
                            receivedCurrency = "",
                            sentQuantity = sentQuantity.getString(context),
                            sentCurrency = ZEC_SYMBOL,
                            feeAmount = stringRes(fee ?: Zatoshi(0)).getString(context),
                            feeCurrency = ZEC_SYMBOL,
                            tag = ""
                        )
                    }

                    SEND_FAILED -> null
                    RECEIVED,
                    RECEIVING -> {
                        CsvEntry(
                            date = dateString,
                            receivedQuantity = stringRes(transaction.overview.netValue).getString(context),
                            receivedCurrency = ZEC_SYMBOL,
                            sentQuantity = "",
                            sentCurrency = "",
                            feeAmount = "",
                            feeCurrency = "",
                            tag = ""
                        )
                    }

                    RECEIVE_FAILED -> null
                    SHIELDED,
                    SHIELDING,
                    SHIELDING_FAILED -> null
                }
            }
}

private data class CsvEntry(
    val date: String,
    val receivedQuantity: String,
    val receivedCurrency: String,
    val sentQuantity: String,
    val sentCurrency: String,
    val feeAmount: String,
    val feeCurrency: String,
    val tag: String,
)

private const val ZEC_SYMBOL = "ZEC"
private const val CSV_SEPARATOR = ","
