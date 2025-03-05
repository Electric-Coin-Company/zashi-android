---
name: App release
about: Instructions on Zashi app release process.
title: 'Release: '
labels: 'release'
assignees: ''

---
### Details about the release
<!-- Describe anything any details related to this app release. -->

### Release steps checklist
This is a brief checklist to ensure all the necessary pre-release and post-release steps have been done:

#### Google Play release:

- [ ] Update [CHANGELOG.md](../../CHANGELOG.md)
- [ ] Update all supported What's New release notes for Google Play:
  - [WHATS_NEW_EN.md](../../docs/whatsNew/WHATS_NEW_EN.md)
  - [WHATS_NEW_ES.md](../../docs/whatsNew/WHATS_NEW_ES.md)
- [ ] Create release notes for all supported languages for F-Droid. Note the new file name MUST be equal to the app 
  `versionCode`.
  - [EN changelog](../../fastlane/metadata/android/en-US/changelogs/)
  - [ES changelog](../../fastlane/metadata/android/es/changelogs/)
- [ ] Update the build version name **ZCASH_VERSION_NAME** in [gradle.properties](../../gradle.properties) (if required)
- [ ] Update the release priority field **ZCASH_IN_APP_UPDATE_PRIORITY** in [gradle.properties](../../gradle.properties) (if required)
- [ ] Check the latest Zcash SDK [version](https://repo.maven.apache.org/maven2/cash/z/ecc/android/zcash-android-sdk/) with the updated checkpoints is used
- [ ] Check the latest app dependencies are used, use `./gradlew dependencyUpdate`

#### Alternative stores release:

Later, once the Google Play build is public in the Production track:
- [ ] Tag the release commit (with format [version-name]-[version-code], e.g., 1.4-876)
- [ ] Open a new GitHub release (https://github.com/Electric-Coin-Company/zashi-android/releases)
