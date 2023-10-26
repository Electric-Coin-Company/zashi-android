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
Automated deployment to Google Play configured with the [Gradle Play Publisher plugin](https://github.com/Triple-T/gradle-play-publisher).
To perform a deployment:
1. Configure a Google Cloud service account and API key with the correct permissions
1. Configure Gradle properties
    1. `ZCASH_GOOGLE_PLAY_SERVICE_ACCOUNT` - Set the Google Play Service Account enabled in the Google Cloud console
    1. `ZCASH_GOOGLE_PLAY_SERVICE_KEY_FILE_PATH` - Set to the path of the service key in JSON format
    1. `ZCASH_GOOGLE_PLAY_DEPLOY_TRACK` - Set to `internal`
    1. `ZCASH_GOOGLE_PLAY_DEPLOY_STATUS` - Set to `completed`
1. Run the Gradle task `./gradlew :app:publishBundle`

To generate a build with a correct version that can be deployed manually later:
1. Configure a Google Cloud service account and API key with the correct permissions
1. Configure Gradle properties
    1. `ZCASH_GOOGLE_PLAY_SERVICE_ACCOUNT` - Set the Google Play Service Account enabled in the Google Cloud console
    1. `ZCASH_GOOGLE_PLAY_SERVICE_KEY_FILE_PATH` - Set to the path of the service key in JSON format
    1. `ZCASH_GOOGLE_PLAY_DEPLOY_TRACK` - Set to `internal` (this is the default value)
    1. `ZCASH_GOOGLE_PLAY_DEPLOY_STATUS` - Set to `draft` (this is the default value)
1. Run the Gradle tasks `./gradlew :app:processReleaseVersionCodes :app:bundleRelease`

Note that the above instructions are for repeat deployments.  If you do not yet have an app listing, you'll need to create that manually.

Note that the artifacts can be manually saved from their output directory under the app/build directory

## Manual Deployment
To manually deploy a build of the app
1. Configure Gradle properties
    1. `ZCASH_VERSION_CODE` - Set to the integer version code of the app.  A simple monotonically increasing number is recommended.1
    1. `ZCASH_VERSION_NAME` - Set to a human-readable version number, such as 1.0.1.
1. Run the Gradle task `./gradlew :app:bundleRelease`
1. Collect the build artifacts under `app/build` and manually deploy them through the Google Play web interface