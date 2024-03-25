package co.electriccoin.zcash.ui.screen.sendconfirmation.ext

import android.content.Context
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import co.electriccoin.zcash.ui.R

fun List<TransactionSubmitResult>.toSupportString(context: Context): String {
    return buildString {
        appendLine(context.getString(R.string.send_confirmation_multiple_report_statuses))

        this@toSupportString.forEachIndexed { index, result ->
            when (result) {
                is TransactionSubmitResult.Success -> {
                    appendLine(
                        context.getString(
                            R.string.send_confirmation_multiple_report_status_success,
                            index + 1
                        )
                    )
                }

                is TransactionSubmitResult.Failure -> {
                    appendLine(
                        context.getString(
                            R.string.send_confirmation_multiple_report_status_failure,
                            index + 1,
                            result.grpcError.toString(),
                            result.code,
                            result.description,
                        )
                    )
                }

                is TransactionSubmitResult.NotAttempted -> {
                    appendLine(
                        context.getString(
                            R.string.send_confirmation_multiple_report_status_not_attempt,
                            index + 1
                        )
                    )
                }
            }
        }
    }
}
