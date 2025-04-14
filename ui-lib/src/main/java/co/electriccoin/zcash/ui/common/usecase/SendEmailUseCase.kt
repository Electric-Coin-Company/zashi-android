package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.content.Intent
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.screen.support.model.SupportInfoType
import co.electriccoin.zcash.ui.util.EmailUtil

class SendEmailUseCase(
    private val context: Context,
    private val getSupport: GetSupportUseCase
) {
    operator fun invoke(
        address: StringResource,
        subject: StringResource,
        message: StringResource
    ) {
        val intent =
            EmailUtil
                .newMailActivityIntent(
                    recipientAddress = address.getString(context),
                    messageSubject = subject.getString(context),
                    messageBody = message.getString(context)
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
        runCatching { context.startActivity(intent) }
    }

    suspend operator fun invoke(exception: Exception) {
        val fullMessage =
            EmailUtil.formatMessage(
                body = exception.stackTraceToString(),
                supportInfo = getSupport().toSupportString(SupportInfoType.entries.toSet())
            )
        val mailIntent =
            EmailUtil
                .newMailActivityIntent(
                    recipientAddress = context.getString(R.string.support_email_address),
                    messageSubject = context.getString(R.string.app_name),
                    messageBody = fullMessage
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
        runCatching { context.startActivity(mailIntent) }
    }


    suspend operator fun invoke(synchronizerError: SynchronizerError) {
        val fullMessage =
            EmailUtil.formatMessage(
                body = synchronizerError.getStackTrace(null),
                supportInfo = getSupport().toSupportString(SupportInfoType.entries.toSet())
            )
        val mailIntent =
            EmailUtil
                .newMailActivityIntent(
                    recipientAddress = context.getString(R.string.support_email_address),
                    messageSubject = context.getString(R.string.app_name),
                    messageBody = fullMessage
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
        runCatching { context.startActivity(mailIntent) }
    }

    suspend operator fun invoke(submitResult: SubmitResult.Failure) {
        val fullMessage =
            when (submitResult) {
                is SubmitResult.SimpleTrxFailure -> {
                    EmailUtil.formatMessage(
                        body = submitResult.toErrorMessage(),
                        supportInfo = submitResult.toErrorStacktrace()
                    )
                }

                is SubmitResult.MultipleTrxFailure -> {
                    EmailUtil.formatMessage(
                        prefix = context.getString(R.string.send_confirmation_multiple_report_text),
                        supportInfo = getSupport().toSupportString(SupportInfoType.entries.toSet()),
                        suffix = submitResult.results.toSupportString(context)
                    )
                }
            }

        val mailIntent =
            EmailUtil
                .newMailActivityIntent(
                    context.getString(R.string.support_email_address),
                    context.getString(R.string.app_name),
                    fullMessage
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

        runCatching {
            context.startActivity(mailIntent)
        }
    }

    private fun List<TransactionSubmitResult>.toSupportString(context: Context): String =
        buildString {
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
