package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.content.Intent
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.screen.sendconfirmation.ext.toSupportString
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SubmitResult
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
            EmailUtil.newMailActivityIntent(
                recipientAddress = address.getString(context),
                messageSubject = subject.getString(context),
                messageBody = message.getString(context)
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

        context.startActivity(intent)
    }

    suspend operator fun invoke(submitResult: SubmitResult) {
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
                else -> {
                    ""
                }
            }

        val mailIntent =
            EmailUtil.newMailActivityIntent(
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
}
