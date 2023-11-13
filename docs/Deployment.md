# Signing
To create a release build, signing must be configured.  The following Gradle properties must be set:
1. `ZCASH_RELEASE_KEYSTORE_PATH`
1. `ZCASH_RELEASE_KEYSTORE_PASSWORD`
1. `ZCASH_RELEASE_KEY_ALIAS`
1. `ZCASH_RELEASE_KEY_ALIAS_PASSWORD`
1. Run `./gradlew :app:assembleRelease` to create a signed release APK, which can be tested and easily installed on an emulator or test device.  _Note that this APK cannot be deployed, because Google Play requires deployment in AAB format.  APK, however, is easier to manage for manually creating a build for testing._

Note that although these are called "release" keys, they may actually be the "upload" key if Google Play Signing is being used.

# Deployment
After signing is configured, it is possible to then configure deployment to Google Play.

## Automated Deployment
Automated deployment to Google Play is configured with custom
[Google Play publishing Gradle task](../build-conventions-secant/src/main/kotlin/secant.publish-conventions.gradle.kts).
To perform a deployment with this task:
1. Configure a Google Cloud service account and API key with the correct permissions
1. Configure a Google Play Publishing API key in Google Cloud console
1. Configure Gradle properties
    1. `ZCASH_GOOGLE_PLAY_SERVICE_ACCOUNT_KEY` - Set the Google Play Service Account enabled in the Google Cloud console
    1. `ZCASH_GOOGLE_PLAY_PUBLISHER_API_KEY` - Set the Google Play Publish API enabled in the Google Cloud console
    1. `ZCASH_GOOGLE_PLAY_DEPLOY_TRACK` - Set to `internal` or `alpha`
    1. `ZCASH_GOOGLE_PLAY_DEPLOY_STATUS` - Set to `draft` or `completed`
1. Run the Gradle task `./gradlew :app:publishBundle`

For more information about proper automated deployment setup, see 
[Google Play publishing Gradle task](../build-conventions-secant/src/main/kotlin/secant.publish-conventions.gradle.kts)
documentation and related [gradle.properties](../gradle.properties) attributes.

Note that the above instructions are for repeat deployments.  If you do not yet have an app listing, you'll need to create that manually.

Note that the artifacts can be manually saved from their output directory under the app/build directory

## Manual Deployment
To manually deploy a build of the app
1. Configure Gradle properties
    1. `ZCASH_VERSION_CODE` - Set to the integer version code of the app.  A simple monotonically increasing number is recommended.1
    1. `ZCASH_VERSION_NAME` - Set to a human-readable version number, such as 1.0.1.
1. Run the Gradle task `./gradlew :app:bundleRelease`
1. Collect the build artifacts under `app/build` and manually deploy them through the Google Play web interface