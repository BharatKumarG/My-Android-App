# 21stDevTodo

This project is configured to be built by GitHub Actions. The workflow in `.github/workflows/android-build.yml` will run `./gradlew assembleRelease` and upload produced APK files as artifacts.

Note: The repository does not include Gradle wrapper binaries. GitHub Actions will download Gradle automatically. For a signed release, add a keystore and signing config, and provide secrets in repo settings.
