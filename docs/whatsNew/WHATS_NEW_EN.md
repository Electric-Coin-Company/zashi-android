# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Supported section titles:
- Added, Changed, Fixed, Removed

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [2.0.3 (965)] - 2025-05-19

### Changed:
- Zashi no longer includes transparent receivers in Unified Addresses.
- The Receive screen now displays a rotating, shielded-only UA which gets freshly generated each time you open the Receive screen.
- All transactions sent to your different rotating Shielded Addresses will remain part of one wallet balance under the same seed phrase.
- Wallets and exchanges that don’t support sending funds to shielded receivers will require transparent address.

## [2.0.2 (962)] - 2025-05-14

### Changed:
- When entering amount in USD, we floor the Zatoshi amount automatically to the nearest 5000 Zatoshi to prevent creating unspendable dust notes in your wallet.
- We updated primary & secondary button position to follow UX best practices.
- We updated Receive screen design.
- We updated the Send and Receive screen icons across the app based on your feedback.
- We improved copy in a few places.
- We also made a few other UI tweaks.

### Fixed:
- We made a few bug fixes.

## [2.0.1 (941)] - 2025-04-29

### Added:
- Zashi 2.0 is here!
- New Wallet Status Widget helps you navigate Zashi with ease and get more info upon tap.

### Changed:
- Redesigned Home Screen and streamlined app navigation.
- Balances redesigned into a new Spendable component on the Send screen.
- Revamped Restore flow.
- Create Wallet with a tap! New Wallet Backup flow moved to when your wallet receives first funds.
- Firebase Crashlytics are fully opt-in. Help us improve Zashi, or don’t, your choice.
- Scanning a ZIP 321 QR code now opens Zashi!

## [2.0.0 (934)] - 2025-04-25

### Added:
- Zashi 2.0 is here!
- New Wallet Status Widget helps you navigate Zashi with ease and get more info upon tap.

### Changed:
- Redesigned Home Screen and streamlined app navigation.
- Balances redesigned into a new Spendable component on the Send screen.
- Revamped Restore flow.
- Create Wallet with a tap! New Wallet Backup flow moved to when your wallet receives first funds.
- Firebase Crashlytics are fully opt-in. Help us improve Zashi, or don’t, your choice.
- Scanning a ZIP 321 QR code now opens Zashi!

## [1.5.2 (932)] - 2025-04-23

### Added:
- We added an option for Playstore users to opt out of sharing crash reports via Firebase Crashlytics. You can find this new setting in the Advanced Settings -> Crash Reporting.

## [1.5.2 (929)] - 2025-04-09

### Fixed
- 1.5 Bug Fix release!
- We fixed a migration issue impacting some users on 1.5 app version.
- We also removed the redundant Security Warning screen which was incorrectly informing the user about crash 
  reporting not included in Zashi Android FOSS version.


## [1.5.2 (926)] - 2025-04-03

### Fixed:
- 1.5 Bug Fix release!
- We fixed a migration issue impacting some users on 1.5 app version.

## [1.5.1 (925)] - 2025-03-31

### Added:
- Transparent Funds Rescue - Zashi can now help you recover funds from fully transparent wallets like Ledger. We recommend importing your transparent hardware wallet recovery phrase into a Keystone hardware wallet and then pairing it with Zashi using the Keystone integration.

### Fixed:
- We fixed a long-standing note commitment tree issue that affected a small number of users. Zashi is now able to allow stuck funds to be spent.

## [1.5 (923)] - 2025-03-27

### Added:
- Transparent Funds Rescue - Zashi can now help you recover funds from fully transparent wallets like Ledger. We recommend importing your transparent hardware wallet recovery phrase into a Keystone hardware wallet and then pairing it with Zashi using the Keystone integration.

### Fixed:
- We fixed a long-standing note commitment tree issue that affected a small number of users. Zashi is now able to allow stuck funds to be spent.

## [1.4 (876)] - 2025-03-04

### Added
- Export your last year's transaction history with a new Export Tax File feature.
- Bookmark transactions, and add private notes.
- Filter for Received, Sent, Memos, Notes, and Bookmarked transactions.
- Download Zashi from F-Droid and GitHub.

### Changed
- Discover redesigned Transaction History!
- Access Keystone from the Integrations screen.
- Enjoy improved transaction signing experience.

### Fixed
- No more failures of Keystone Send, we fixed it.
- We also fixed the Tax File feature.

## [1.4 (873)] - 2025-03-03

### Added
- Export your last year's transaction history with a new Export Tax File feature.
- Bookmark transactions, and add private notes to them.
- Filter for Received, Sent, Memos, Notes, and Bookmarked transactions.
- Download Zashi Android from F-Droid and GitHub.

### Changed
- Discover completely redesigned Transaction History!
- Access Keystone from the Integrations screen.
- Enjoy improved transaction signing experience.

### Fixed
- No more failures of Keystone Send, we fixed the issue!

## [1.3.3 (839)] - 2025-01-23

### Changed
- We refactored the QR code image logic to work with the newer ZashiQr component.
- The colors of the QR code image on the SignTransaction screen are now the same for both color themes to improve 
  scanning by the Keystone device.
- We improved the block synchronization progress logic to return an uncompleted percentage in case the Synchronizer 
  is still in the SYNCING state.
- We updated the Keystone SDK to version 0.7.10, which brings a significant QR code scanning improvement.

### Fixed
- We fixed the logic for the Disconnected popup for cases when the app is backgrounded.
- We also resolved an issue with the app's navigation stack not getting cleared up after clicking on the View 
  Transactions button.

## [1.3.2 (829)] - 2025-01-10

### Changed
- Zashi now displays the dark version of QR codes in the dark theme
- We improved the QR code scanner to respond faster
- We refactored the Send screens to work better for you

### Fixed
- And we also fixed the way how Zashi treats addresses within QR codes

## [1.3.1 (822)] - 2025-01-07

### Fixed
- We fixed a bug in the Coinbase Onramp feature which impacted users making purchases with their Coinbase account. 
  We now pass a correct transparent address to Coinbase and your ZEC gets sent directly to your Zashi wallet instead 
  of your Coinbase account.

## [1.3 (812)] - 2024-12-19

### Added
Zashi + Keystone Hardware Wallet integration is live!
- Connect your Keystone wallet with Zashi.
- Sign a transaction with your Keystone wallet.
- Includes both shielded and transparent ZEC support.

## [1.2.3 (799)] - 2024-11-26

### Added
- It is finally here! Flexa integration at your service!
- Pay with Flexa at supported merchants in the US, Canada, and El Salvador.
- It's waiting for you in Zashi Settings.

## [1.2.2 (789)] - 2024-11-18

### Added
- Hola! We taught Zashi to speak Spanish!
- We adopted SDK release 2.2.6 which should help speed up sending multiple transactions.
- We implemented encryption and remote storage for Address Book!
- We added device authentication to app launch.
- We added animated progress screen and new success and failure screens.

### Changed
- We made Settings and status screens pretty.
- Let us know how you like Zashi with the improved Send Feedback feature.

### Fixed
- We fixed the shield icon behaviour in Transaction History.

## [1.2.1 (760)] - 2024-10-22

### Added
- Tired of copy pasting addresses? We’ve added an Address Book feature!
- Introducing the “Request ZEC” feature: easily create a payment request and share it as a QR code!

### Changed
- The Receive tab got some love—we redesigned it based on your feedback.
- We tweaked the Send form.
- We updated the transaction history to simplify your experience.
- And that’s not all—the Scan UI has been redesigned, too.
- We also made many other minor UI/UX tweaks and fixes along the way. Enjoy!

## [1.2 (739)] - 2024-09-27

### Changed
- Adopted snapshot Zcash SDK version 2.2.5 which includes a fix for block synchronization issues caused by incorrect check of the block height component.

## [1.2 (735)] - 2024-09-20

### Added:
- All Zashi's error dialogs now have a Report button that prefills the error stack trace in a selected email client.

### Changed:
- The Message input field on the Send screen has been updated to provide a Return key on the software keyboard, and make auto-capitalization at the beginning of every sentence or a new line.

### Fixed:
- We fixed the Send Feedback feature and made it compatible with more e-mail clients.

## [1.2 (731)] - 2024-09-16

### Added
- We added an experimental feature which allows you to buy ZEC with Coinbase Onramp integration - find it in the Advanced Settings.
- No need to keep guessing which server performs best. We added a dynamic server switch, which identifies the best performing servers for you.
- We improved UX for unsent transactions. The SDK now checks whether there are any unsent transactions, and it attempts to resubmit such transactions.
- We also added support for Android 15.

### Changed
- We updated our Settings UI.

## [1.2 (729)] - 2024-09-13

### Added
- We added an experimental feature which allows you to buy ZEC with Coinbase Onramp integration - find it in the Advanced Settings.
- No need to keep guessing which server performs best. We added a dynamic server switch, which identifies the best performing servers for you.
- We improved UX for unsent transactions. The SDK now checks whether there are any unsent transactions, and it attempts to resubmit such transactions.
- We also added support for Android 15.

### Changed
- We updated our Settings UI. 

## [1.1.7 (718)] - 2024-09-06

### Added
- We added ZEC/USD currency conversion to Zashi without compromising your IP address.
- You can now view your balances and type in transaction amounts in both USD and ZEC.

### Changed
- We adopted the latest Zcash SDK version 2.2.0, which brings the ZIP 320 TEX addresses support, currency conversion feature that fetches ZEC/USD exchange rate over Tor, and support for restoring the full history from transparent-only wallets.

### Fixed
- We re-enabled app screenshot testing after we moved away from the AppCompat components.

## [1.1.6 (712)] - 2024-09-04

### Added
- We added ZEC/USD currency conversion to Zashi without compromising your IP address.
- You can now view your balances and type in transaction amounts in both USD and ZEC.

### Changed
- We adopted the latest Zcash SDK version 2.2.0, which brings the ZIP 320 TEX addresses support, currency conversion feature that fetches ZEC/USD exchange rate over Tor, and support for restoring the full history from transparent-only wallets.

### Fixed
- We re-enabled app screenshot testing after we moved away from the AppCompat components.

## [1.1.5 (706)] - 2024-08-09

### Changed
- We adopted the latest Zcash SDK version 2.1.3, which significantly improves block synchronization speed.
- We also improved the logic for fetching transparent transactions.

## [1.1.4 (700)] - 2024-07-23

### Added
- We added What’s New information to the About screen.
- We secured copying sensitive information into a device clipboard by masking it from the system visual confirmation.

### Changed
- We added a screen with syncing tips for successful Restore.
- We updated UI of the About screen.

### Fixed
- We fixed QR code scanning speed and reliability.
- We fixed UI on the Feedback screen, so the Send button is not hidden by keyboard anymore. 
- We also improved handling of Android configuration changes.
