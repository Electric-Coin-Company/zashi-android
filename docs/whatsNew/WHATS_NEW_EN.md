# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Supported section titles:
- Added, Changed, Fixed, Removed

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

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
