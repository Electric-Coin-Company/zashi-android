# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [1.1.8 (729)] - 2024-09-13

### Added
- Transaction resubmission feature has been added. It periodically searches for unmined sent transactions that
  are still within their expiry window and resubmits them if there are any.
- The Choose server screen now provides a new search for the three fastest servers feature
- Android 15 (Android SDK API level 35) support for 16 KB memory page size has been added
- Coinbase Onramp integration button has been added to the Advanced Settings screen

### Changed
- Choose server screen has been redesigned
- Settings and Advanced Settings screens have been redesigned
- Android `compileSdkVersion` and `targetSdkVersion` have been updated to version 35

### Fixed
- The issue of printing the stacktrace of errors in dialogs has been resolved

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
