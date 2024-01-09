# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

### Changed
- The Not Enough Space screen used for notifying about insufficient free device disk space now provides the light 
  theme by default

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