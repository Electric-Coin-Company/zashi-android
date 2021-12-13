# Build Integrity
Multiple tools can be put in place to enhance build integrity and reduce the risk of supply chain issues. These tools include:
 * Policy — We try to minimize third party dependencies, especially when they are not provided by Google and JetBrains. We also try to minimize the number of Gradle plugins.
 * Checklists — Our [pull request checklist](../.github/pull_request_template.md) specifies only running code from contributors after reviewing the changes first. Our [dependency update checklist](../.github/ISSUE_TEMPLATE/dependency.md) specifies verifying lock file changes during dependency updates.
 * Fixed dependency versions — For our dependency declarations, we use exact dependency versions in gradle.properties instead of version ranges.
 * Dependency locking
     * Gradle buildscript (e.g. plugins) dependencies are locked
     * Kotlin Multiplatform modules have dependency locking enabled
     * Android modules do not have dependency locking for transitive dependencies enabled. [Issue #55](https://github.com/zcash/secant-android-wallet/issues/55) tracks this feature request.
 * Dependency hash or signature verification
     * Gradle — The SHA256 for Gradle is stored in [gradle/wrapper/gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties) which is verified when Gradle is downloaded for the first time
     * Gradle Wrapper — The SHA256 for the Gradle Wrapper is verified on the continuous integration server
     * Dependencies — Verification is not currently enabled for buildscript or compile dependencies

# Dependency locking
## Buildscript
To update build script dependency locks:
`./gradlew dependencies --write-locks`

## Kotlin modules
To update Kotlin module dependency locks:
`./gradlew resolveAndLockAll --write-locks`
