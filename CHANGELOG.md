# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [1.0 (650)] - 2024-05-07

### Added
- Delete Zashi feature has been added. It's accessible from the Advanced settings screen. It removes the wallet 
  secrets from Zashi and resets its state.
- Transaction messages are now checked and removed in case of duplicity  

### Changed
- We've improved the visibility logic of the little loader that is part of the Balances widget
- The App-Update screen UI has been reworked to align with the latest design guidelines

### Removed
- Concatenation of the messages on a multi-messages transaction has been removed and will be addressed using a new 
  design

### Fixed
- Transparent funds shielding action has been improved to address the latest user feedback
- Onboarding screen dynamic height calculation has been improved
- A few more minor UI improvements

## [1.0 (638)] - 2024-04-26

### Fixed
- Default server selection option

## [1.0 (636)] - 2024-04-26

### Changed
- We have added one more group of server options (zec.rocks) for increased coverage and reliability
- zec.rocks:443 is now default wallet option

## [1.0 (630)] - 2024-04-24

### Changed
- We have added more server options for increased coverage and reliability
- If you experience issues with the Zcash Lightwalletd Mainnet server selected by default, please switch to one of 
  the Ywallet servers: https://status.zcash-infra.com/

## [1.0 (628)] - 2024-04-23

### Changed
- The Scan QR code screen has been reworked to align with the rest of the screens
- The Send Form screen scrolls to the Send button on very small devices after the memo is typed

### Fixed
- Sending zero funds is allowed only for shielded recipient address type
- The Balances widget loader has been improved to better handle cases, like a wallet with only transparent funds

## [0.2.0 (609)] - 2024-04-18

### Added
- Advanced Settings screen that provides more technical options like Export private data, Recovery phrase, or 
  Choose server has been added
- A new Server switching screen has been added. Its purpose is to enable switching between predefined and custom 
  lightwalletd servers in runtime.
- The About screen now contains a link to the new Zashi Privacy Policy website
- The Send Confirmation screen has been reworked according to the new design
- Transitions between screens are now animated with a simple slide animation
- Proposal API from the Zcash SDK has been integrated together with handling error states for multi-transaction 
  submission
- New Restoring Your Wallet label and Synchronization widget have been added to all post-onboarding screens to notify 
  users about the current state of the wallet-restoring process

### Changed
- The Transaction History UI has been incorporated into the Account screen and completely reworked according to the 
  design guidelines
- Reworked Send screens flow and their look (e.g., Send Failure screen is now a modal dialog instead of a separate 
  screen)
- The sending and shielding funds logic has been connected to the new Proposal API from the Zcash SDK
- The error dialog contains an error description now. It's useful for tracking down the failure cause.
- A small circular progress indicator is displayed when the app runs block synchronization, and the available balance 
  is zero instead of reflecting a result value.
- Block synchronization statuses have been simplified to Syncing, Synced, and Error states only
- All internal dependencies have been updated

### Fixed
- Button sizing has been updated to align with the design guidelines and preserve stretching if necessary

### Removed
- The seed copy feature from the New wallet recovery and Seed recovery screens has been removed for security reasons

## [0.2.0 (560)] - 2024-02-27

### Added
- A periodic background block synchronization has been added. When the device is connected to the internet using an 
 unmetered connection and is plugged into the power, the background task will start to synchronize blocks randomly 
  between 3 and 4 a.m.

### Changed
- The Send screen form has changed its UI to align with the Figma design. All the form fields provide validations 
  and proper UI response.

## [0.2.0 (554)] - 2024-02-13

### Changed
- Update the Zcash SDK dependency to version 2.0.6, which adds more details on current balances

### Added
- The Balances screen now provides details on current balances like Change pending and Pending transactions
- The Balances screen adds a new Block synchronization progress bar and status, which were initially part of the 
  Account screen and redesigned
- The Balances screen supports transparent funds shielding within its new shielding panel

### Fixed
- Fixed character replacement in Zcash addresses on the Receive screen caused by ligatures in the app's primary font 
 using the secondary font. This will be revisited once a proper font is added.
- Improved spacing of titles of bottom navigation tabs, so they work better on smaller screens 

## [0.2.0 (541)] - 2024-01-30
- Update the Zcash SDK dependency to version 2.0.5, which improves the performance of block synchronization

## [0.2.0 (540)] - 2024-01-27

### Added
- The current balance UI on top of the Account screen has been reworked. It now displays the currently available 
  balance as well.
- The same current balance UI was incorporated into the Send and Balances screens. 
- The Send Error screen now contains a simple text with the reason for failure. The Send screen UI refactoring is 
  still in progress.

### Fixed
- Properly clearing focus from the Send text fields when moved to another screen

## [0.2.0 (530)] - 2024-01-16

### Changed
- The Not Enough Space screen used for notifying about insufficient free device disk space now provides the light 
theme by default
- The App Update screen UI was improved to align with the other implemented screens according to the new design. Its 
final design is still in progress.
- The Receive screen provides a new UI and features. The Unified and Transparent Zcash addresses are displayed on
this screen, together with buttons for copying the address and sharing the address's QR code.

### Removed
- Address Detail screen in favor of the Receive screen

## [0.2.0 (523)] - 2024-01-09

### Added
- Transaction history items now display Memos within the Android Toast, triggered by clicking the item
- Transaction history items add displaying transaction IDs; the ID element is also clickable

### Changed
- All project dependencies have been updated, including the Zcash SDK dependency

## [0.2.0 (517)] - 2023-12-21

### Changed
- Home screen navigation switched from the Side menu to the Bottom Navigation Tabs menu
- Re-enabled the possibility of installing different Zashi application build types on the same device simultaneously 
  (i.e., Mainnet, Testnet, Production, Debug)  
- Send screen form now validates a maximum amount for sending with respect to the available balance
- Send form now supports software keyboard confirm actions 
- And a few more miner UI improvements

### Fixed
- Resizing Send screen Form TextFields when focused
- Hidden Send screen Form TextFields behind the software keyboard when focused
- Monetary separators issues on the Send screen Form

## [0.2.0 (505)] - 2023-12-11

### Added
- Unfinished features show a "Not implemented yet" message after accessing in the app UI 

### Changed
- Home and Receive screens have their Top app bar UI changed
- Automatic brightness adjustment switched to an on-demand feature after a new button is clicked on the Receive screen 

### Removed
- Home screen side menu navigation was removed in favor of the Settings screen

## [0.2.0 (491)] - 2023-12-01

### Changed
- Updated user interface of these screens:
   - New Wallet Recovery Seed screen (accessible from onboarding) 
   - Seed Recovery screen (accessible from Settings)
   - Restore Seed screen for an existing wallet (accessible from onboarding)
   - Restore Seed Birthday Height screen for an existing wallet (accessible from onboarding)