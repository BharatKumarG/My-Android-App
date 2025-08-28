pluginManagement {
    repositories {
        google()          // 👈 Required for Android Gradle Plugin
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "My-Android-App"
include(":app")
