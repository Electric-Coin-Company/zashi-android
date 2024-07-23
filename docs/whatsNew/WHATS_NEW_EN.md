# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [1.1.4 (700)] - 2024-07-23

### Added
- A new What's New screen has been added, accessible from the About screen
- Copying sensitive information like addresses, transaction IDs, or wallet secrets into the device clipboard is now 
  masked out from the system visual confirmation, but it's still copied as expected.

### Changed
- The About screen has been redesigned to align with the new design guidelines
- The Restore Success dialog has been reworked into a separate screen, allowing users to opt out of the Keep screen 
  on while restoring option

### Fixed
- Support Screen now shows the Send button above keyboard instead of overlaying it
- QR code scanning speed and reliability have been improved to address the latest reported scan issue
- The handling of Android configuration changes has been improved
