@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.model

enum class UpdateStage {
    Prepared,
    Running,
    Failed,
    Done,
    Canceled
}
