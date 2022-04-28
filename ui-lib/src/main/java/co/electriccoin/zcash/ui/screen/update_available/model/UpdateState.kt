@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.model

enum class UpdateState {
    Prepared,
    Running,
    Failed,
    Done,
    Canceled
}
