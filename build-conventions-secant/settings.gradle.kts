@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        val isRepoRestrictionEnabled = true

        google {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("androidx.navigation")
                    includeGroup("com.android.tools")
                    includeGroup("com.google.testing.platform")
                    includeGroupByRegex("androidx.*")
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
        mavenCentral {
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("androidx.navigation")
                    excludeGroup("com.android.tools")
                    excludeGroup("com.google.testing.platform")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                    excludeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
    }
}

rootProject.name = "build-conventions-secant"
