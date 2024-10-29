package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteAddressBookUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteContactUseCase
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.common.usecase.GetBackupPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.GetContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.GetPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSupportUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveBackupPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletStateUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.RefreshFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.SaveContactUseCase
import co.electriccoin.zcash.ui.common.usecase.SendEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.SendSupportEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.ShareImageUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321BuildUriUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ProposalFromUriUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        factoryOf(::ObserveSynchronizerUseCase)
        factoryOf(::GetSynchronizerUseCase)
        factoryOf(::ObserveFastestServersUseCase)
        factoryOf(::ObserveSelectedEndpointUseCase)
        factoryOf(::RefreshFastestServersUseCase)
        factoryOf(::PersistEndpointUseCase)
        factoryOf(::ValidateEndpointUseCase)
        factoryOf(::GetPersistableWalletUseCase)
        factoryOf(::GetSelectedEndpointUseCase)
        factoryOf(::ObserveConfigurationUseCase)
        factoryOf(::RescanBlockchainUseCase)
        factoryOf(::GetTransparentAddressUseCase)
        factoryOf(::ObserveAddressBookContactsUseCase)
        factoryOf(::DeleteAddressBookUseCase)
        factoryOf(::ValidateContactAddressUseCase)
        factoryOf(::ValidateContactNameUseCase)
        factoryOf(::SaveContactUseCase)
        factoryOf(::UpdateContactUseCase)
        factoryOf(::DeleteContactUseCase)
        factoryOf(::GetContactByAddressUseCase)
        factoryOf(::ObserveContactByAddressUseCase)
        singleOf(::ObserveContactPickedUseCase)
        factoryOf(::GetAddressesUseCase)
        factoryOf(::CopyToClipboardUseCase)
        factoryOf(::ShareImageUseCase)
        factoryOf(::Zip321BuildUriUseCase)
        factoryOf(::Zip321ProposalFromUriUseCase)
        factoryOf(::Zip321ParseUriValidationUseCase)
        factoryOf(::ObserveWalletStateUseCase)
        factoryOf(::IsCoinbaseAvailableUseCase)
        factoryOf(::GetSpendingKeyUseCase)
        factoryOf(::ObservePersistableWalletUseCase)
        factoryOf(::ObserveBackupPersistableWalletUseCase)
        factoryOf(::GetBackupPersistableWalletUseCase)
        factoryOf(::GetSupportUseCase)
        factoryOf(::SendEmailUseCase)
        factoryOf(::SendSupportEmailUseCase)
    }
