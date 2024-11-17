# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [1.2.2 (788)] - 2024-11-17

### Added
- Address book encryption
- Android auto backup support for address book encryption
- The device authentication feature on the Zashi app launch has been added
- Zashi app now supports Spanish language. It can be changed in the System settings options.
- The Flexa SDK has been adopted to enable payments using the embedded Flexa UI
- New Sending, Success, Failure, and GrpcFailure subscreens of the Send Confirmation screen have been added
- New Copy Transaction IDs feature has been added to the MultipleTransactionFailure screen

### Changed
- Shielded transactions are properly indicated in transaction history
- The in-app update logic has been fixed and is now correctly requested with every app launch
- The Not enough space and In-app udpate screens have been redesigned
- External links now open in in-app browser
- All the Settings screens have been redesigned
- Adopted Zcash SDK version 2.2.6

### Fixed
- Address book toast now correctly shows on send screen when adding both new and known addresses to text field
- The application now correctly navigates to the homepage after deleting the current wallet and creating a new or
  recovering an older one
- The in-app update logic has been fixed and is now correctly requested with every app launch