package co.electriccoin.zcash.ui.common.model

sealed interface SubmitResult {
    data class Success(
        val txIds: List<String>
    ) : SubmitResult

    data class Failure(
        val txIds: List<String>,
        val code: Int,
        val description: String?
    ) : SubmitResult

    data class GrpcFailure(
        val txIds: List<String>
    ) : SubmitResult

    data class Partial(
        val txIds: List<String>,
        val statuses: List<String>
    ) : SubmitResult
}
