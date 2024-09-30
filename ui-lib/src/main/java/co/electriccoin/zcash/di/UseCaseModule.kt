package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.usecase.GetPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.RefreshFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::ObserveSynchronizerUseCase)
        singleOf(::GetSynchronizerUseCase)
        singleOf(::ObserveFastestServersUseCase)
        singleOf(::ObserveSelectedEndpointUseCase)
        singleOf(::RefreshFastestServersUseCase)
        singleOf(::PersistEndpointUseCase)
        singleOf(::ValidateEndpointUseCase)
        singleOf(::GetPersistableWalletUseCase)
        singleOf(::GetSelectedEndpointUseCase)
        singleOf(::ObserveConfigurationUseCase)
        singleOf(::RescanBlockchainUseCase)
        singleOf(::GetTransparentAddressUseCase)
        singleOf(::ObserveAddressBookContactsUseCase)
    }
