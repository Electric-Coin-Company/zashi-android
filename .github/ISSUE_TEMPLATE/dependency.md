---
name: Dependency update
about: Update existing dependency to a new version.
title: ''
labels: dependencies
assignees: ''

---

For a Gradle dependency:
1. Update the dependency version in the root `gradle.properties`
1. Update the dependency locks
    1. For Gradle plugins: `./gradlew dependencies --write-locks`
    1. For Gradle dependencies: `./gradlew resolveAndLockAll --write-locks`
1. Verify no unexpected entries appear in the lockfiles. _A supply chain attack could occur during this stage. The lockfile narrows the supply chain attack window to this very moment (as opposed to every time a build occurs)_
1. Are there any new APIs or possible migrations for this dependency?

For Gradle itself:
1. Run `./gradle wrapper --gradle-version $X`
1. Add `distributionSha256Sum=` in `gradle/wrapper/gradle-wrapper.properties`, referencing [Gradle Release Checksums](https://gradle.org/release-checksums/)
1. Update the continuous integration server environment variables with the updated SHA for the Gradle wrapper, referencing [Gradle Release Checksums](https://gradle.org/release-checksums/). _Note: Bitrise builds for other branches may temporarily fail since only a single checksum at a time is currently supported. The wrapper is not updated with every Gradle version so in practice this problem should occur infrequently._
1. Are there any new APIs or possible migrations?
