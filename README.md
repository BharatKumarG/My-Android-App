# MyAndroidAppEnhanced

A modern Android ToDo application built with Kotlin, Jetpack Compose, and the latest Android development practices.

## Features

✨ **Smart AI-Powered Quick Add**: Parse natural language input like "Call John tomorrow 9am" into structured tasks  
📱 **Modern Compose UI**: Beautiful, responsive Material3 design with smooth animations  
🔄 **MVVM Architecture**: Clean architecture with Hilt dependency injection and Room database  
⏰ **Smart Reminders**: WorkManager-powered notifications for task reminders  
🗂️ **Task Management**: Add, edit, delete, and organize tasks with priority levels  
💾 **Export/Import**: JSON-based task backup and restore functionality  
🎯 **Advanced Features**: Swipe-to-delete with undo, search, filtering, and smart suggestions  

## Architecture

- **UI Layer**: Jetpack Compose with Material3 design system
- **Presentation**: MVVM pattern with ViewModels and StateFlow
- **Domain**: Repository pattern for data abstraction
- **Data**: Room database with type converters
- **DI**: Hilt for dependency injection
- **Background Work**: WorkManager for scheduling reminders
- **Serialization**: kotlinx.serialization for JSON operations

## Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material3** - Design system and components
- **Room** - Local database persistence
- **Hilt** - Dependency injection
- **WorkManager** - Background task scheduling
- **kotlinx.serialization** - JSON serialization
- **Coroutines & Flow** - Asynchronous programming

## Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17
- Android SDK with API level 34
- Minimum SDK: API 24 (Android 7.0)

### Building the Project

1. Clone the repository:
   ```bash
   git clone <your-repo-url>
   cd MyAndroidAppEnhanced
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

### Running Locally

1. Connect an Android device or start an emulator
2. Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## CI/CD Pipeline

This project includes a GitHub Actions workflow that:

- ✅ Builds the APK on every push and pull request
- ✅ Runs unit tests
- ✅ Caches Gradle dependencies for faster builds
- ✅ Uploads build artifacts
- ✅ Uses JDK 17 and latest Android build tools

### GitHub Actions Setup

The workflow is automatically triggered on:
- Pushes to `main` and `develop` branches
- Pull requests to `main` branch

No additional setup required - just push your code and the CI will run!

## Project Structure

```
app/
├── src/main/java/com/example/myandroidappenhanced/
│   ├── data/
│   │   ├── database/          # Room entities, DAOs, converters
│   │   └── repository/        # Repository implementations
│   ├── di/                    # Hilt modules
│   ├── ui/
│   │   ├── components/        # Reusable Compose components
│   │   ├── screens/           # Screen composables and ViewModels
│   │   └── theme/            # Theme, colors, typography
│   ├── utils/                # Utility classes and helpers
│   ├── workers/              # WorkManager background tasks
│   └── MainActivity.kt       # Entry point
├── src/main/res/             # Resources (strings, themes, etc.)
└── build.gradle.kts          # App-level Gradle configuration
```

## Key Features Implementation

### Smart Task Parser
Natural language processing to extract:
- Task title and description
- Due dates (today, tomorrow, next Monday, etc.)
- Time specifications (9am, 2:30pm, etc.)
- Priority levels (urgent, important, later, etc.)
- Reminder preferences

### Modern UI Components
- **TaskCard**: Expandable cards with completion toggle and actions
- **AddTaskDialog**: Full-featured task creation with smart suggestions
- **TaskListScreen**: Main interface with search, filtering, and summary cards

### Data Management
- **Room Database**: Local persistence with migrations support
- **Type Converters**: Automatic conversion for LocalDateTime and enums
- **Repository Pattern**: Clean separation between UI and data layers

### Background Processing
- **ReminderWorker**: Scheduled notifications using WorkManager
- **ReminderManager**: Centralized reminder scheduling and cancellation

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Testing

Run tests with:
```bash
./gradlew test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Material Design 3 for the beautiful design system
- Android Jetpack team for the modern development tools
- The open-source community for inspiration and best practices
