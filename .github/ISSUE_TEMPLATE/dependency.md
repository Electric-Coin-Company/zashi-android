---
name: Dependency update
about: Update existing dependency to a new version.
title: ''
labels: dependencies
assignees: ''

---

For a Gradle dependency:
1. Update the dependency version in the root `gradle.properties`.
2. Update the dependency locks
    1. For Gradle plugins `./gradlew dependencies --write-locks`
    2. For module dependencies, we do not have locking enabled yet
3. Are there any new APIs or possible migrations for this dependency?

For Gradle itself:
1. Update the Gradle version in `gradle/wrapper/gradle-wrapper.properties`
2. Update the SHA version, referencing https://gradle.org/release-checksums/
3. Update the Gradle wrapper by running `./gradlew wrapper`
