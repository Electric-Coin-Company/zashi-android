package co.electriccoin.zcash.di

import co.electriccoin.zcash.ui.common.usecase.ApplyTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.ApplyTransactionFulltextFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.CancelProposalFlowUseCase
import co.electriccoin.zcash.ui.common.usecase.ConfirmProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneProposalPCZTEncoderUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneShieldProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateOrUpdateTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.CreateZip321ProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteContactUseCase
import co.electriccoin.zcash.ui.common.usecase.DeleteTransactionNoteUseCase
import co.electriccoin.zcash.ui.common.usecase.DeriveKeystoneAccountUnifiedAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.FlipTransactionBookmarkUseCase
import co.electriccoin.zcash.ui.common.usecase.GetBackupPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.GetContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.GetCurrentFilteredTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetCurrentTransactionsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetExchangeRateUseCase
import co.electriccoin.zcash.ui.common.usecase.GetMetadataUseCase
import co.electriccoin.zcash.ui.common.usecase.GetPersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.GetProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSupportUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionDetailByIdUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransactionMetadataUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZashiSpendingKeyUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.MarkTxMemoAsReadUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToAddressBookUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveAddressBookContactsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveClearSendUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveConfigurationUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveOnAccountChangedUseCase
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveTransactionSubmitStateUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletStateUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveZashiAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystonePCZTUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneSignInRequestUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneUrToZashiAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.PrefillSendUseCase
import co.electriccoin.zcash.ui.common.usecase.RefreshFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.RescanBlockchainUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetInMemoryDataUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetSharedPrefsDataUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetTransactionFiltersUseCase
import co.electriccoin.zcash.ui.common.usecase.SaveContactUseCase
import co.electriccoin.zcash.ui.common.usecase.SelectWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.SendEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.SendSupportEmailUseCase
import co.electriccoin.zcash.ui.common.usecase.SendTransactionAgainUseCase
import co.electriccoin.zcash.ui.common.usecase.SensitiveSettingsVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.ShareImageUseCase
import co.electriccoin.zcash.ui.common.usecase.SharePCZTUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateContactUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateContactNameUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ViewTransactionDetailAfterSuccessfulProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.ViewTransactionsAfterSuccessfulProposalUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321BuildUriUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase
import co.electriccoin.zcash.ui.util.closeableCallback
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.onClose

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
        factoryOf(::ValidateContactAddressUseCase)
        factoryOf(::ValidateContactNameUseCase)
        factoryOf(::SaveContactUseCase)
        factoryOf(::UpdateContactUseCase)
        factoryOf(::DeleteContactUseCase)
        factoryOf(::GetContactByAddressUseCase)
        factoryOf(::ObserveContactByAddressUseCase)
        singleOf(::ObserveContactPickedUseCase)
        factoryOf(::CopyToClipboardUseCase)
        factoryOf(::ShareImageUseCase)
        factoryOf(::Zip321BuildUriUseCase)
        factoryOf(::Zip321ParseUriValidationUseCase)
        factoryOf(::ObserveWalletStateUseCase)
        factoryOf(::IsCoinbaseAvailableUseCase)
        factoryOf(::GetZashiSpendingKeyUseCase)
        factoryOf(::ObservePersistableWalletUseCase)
        factoryOf(::GetBackupPersistableWalletUseCase)
        factoryOf(::GetSupportUseCase)
        factoryOf(::SendEmailUseCase)
        factoryOf(::SendSupportEmailUseCase)
        factoryOf(::IsFlexaAvailableUseCase)
        singleOf(::SensitiveSettingsVisibleUseCase)
        factoryOf(::ObserveWalletAccountsUseCase)
        factoryOf(::SelectWalletAccountUseCase)
        factoryOf(::ObserveSelectedWalletAccountUseCase)
        factoryOf(::ObserveZashiAccountUseCase)
        factoryOf(::GetZashiAccountUseCase)
        factoryOf(::CreateKeystoneAccountUseCase)
        factoryOf(::DeriveKeystoneAccountUnifiedAddressUseCase)
        factoryOf(::ParseKeystoneUrToZashiAccountsUseCase)
        factoryOf(::GetExchangeRateUseCase)
        factoryOf(::GetSelectedWalletAccountUseCase)
        singleOf(::ObserveClearSendUseCase)
        singleOf(::PrefillSendUseCase)
        factoryOf(::GetCurrentTransactionsUseCase)
        factoryOf(::GetCurrentFilteredTransactionsUseCase) onClose ::closeableCallback
        factoryOf(::CreateProposalUseCase)
        factoryOf(::CreateZip321ProposalUseCase)
        factoryOf(::CreateKeystoneShieldProposalUseCase)
        factoryOf(::ParseKeystonePCZTUseCase)
        factoryOf(::ParseKeystoneSignInRequestUseCase)
        factoryOf(::CancelProposalFlowUseCase)
        factoryOf(::ObserveProposalUseCase)
        factoryOf(::SharePCZTUseCase)
        factoryOf(::CreateKeystoneProposalPCZTEncoderUseCase)
        factoryOf(::ObserveOnAccountChangedUseCase)
        factoryOf(::ViewTransactionsAfterSuccessfulProposalUseCase)
        factoryOf(::ViewTransactionDetailAfterSuccessfulProposalUseCase)
        factoryOf(::NavigateToCoinbaseUseCase)
        factoryOf(::ObserveTransactionSubmitStateUseCase)
        factoryOf(::GetProposalUseCase)
        factoryOf(::ConfirmProposalUseCase)
        factoryOf(::GetWalletRestoringStateUseCase)
        factoryOf(::ApplyTransactionFiltersUseCase)
        factoryOf(::ResetTransactionFiltersUseCase)
        factoryOf(::ApplyTransactionFulltextFiltersUseCase)
        factoryOf(::GetTransactionFiltersUseCase)
        factoryOf(::GetTransactionDetailByIdUseCase)
        factoryOf(::SendTransactionAgainUseCase)
        factoryOf(::ObserveAddressBookContactsUseCase)
        factoryOf(::ResetInMemoryDataUseCase)
        factoryOf(::ResetSharedPrefsDataUseCase)
        factoryOf(::NavigateToAddressBookUseCase)
        factoryOf(::GetTransactionMetadataUseCase)
        factoryOf(::FlipTransactionBookmarkUseCase)
        factoryOf(::DeleteTransactionNoteUseCase)
        factoryOf(::CreateOrUpdateTransactionNoteUseCase)
        factoryOf(::MarkTxMemoAsReadUseCase)
        factoryOf(::GetMetadataUseCase)
    }
