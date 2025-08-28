# MyAndroidAppEnhanced

A modern Android ToDo application built with Kotlin, Jetpack Compose, and the latest Android development practices.

## Features

✨ **Smart Task Management** with priority levels and due dates  
📱 **Modern Compose UI** with Material3 design  
🔄 **MVVM Architecture** with Hilt DI and Room database  
⏰ **Task Reminders** with WorkManager notifications  
🗂️ **Search & Filter** tasks efficiently  
💾 **Data Persistence** with Room SQLite database  

## Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit  
- **Material3** - Design system and components
- **Room** - Local database persistence
- **Hilt** - Dependency injection
- **WorkManager** - Background task scheduling
- **Coroutines & Flow** - Asynchronous programming

## Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17
- Android SDK with API level 34
- Minimum SDK: API 24 (Android 7.0)

### Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

## CI/CD Pipeline

This project includes a GitHub Actions workflow that:
- ✅ Builds the APK on every push and pull request
- ✅ Runs unit tests  
- ✅ Caches Gradle dependencies for faster builds
- ✅ Uploads build artifacts
- ✅ Uses JDK 17 and latest Android build tools

## Project Structure

```
app/src/main/java/com/example/myandroidappenhanced/
├── data/
│   ├── database/          # Room entities, DAOs, converters
│   └── repository/        # Repository implementations
├── di/                    # Hilt modules  
├── ui/
│   ├── screens/           # Screen composables and ViewModels
│   └── theme/            # Theme, colors, typography
├── MainActivity.kt       # Entry point
└── MyAndroidAppEnhancedApplication.kt
```

## License

This project is licensed under the MIT License.
