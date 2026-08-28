# Aura - Mood-Tracking Audio Journal 🎙️✨

Aura is a modern, privacy-focused Android application designed to help users track their emotional well-being through voice recordings. By combining audio journaling with mood analysis, Aura provides a unique way to reflect on your day and visualize emotional patterns over time.

---

## 📱 Features

- **Voice Journaling**: Record your thoughts and feelings with a high-quality audio recorder.
- **Mood Association**: Tag each recording with a specific mood (Happy, Sad, Neutral, etc.) to track emotional context.
- **Visual Statistics**: Explore emotional trends through interactive mood frequency charts and heatmaps.
- **Glance Widgets**: Quick access to recording features directly from your home screen using Jetpack Glance.
- **Offline First**: All data is stored locally using Room and internal storage, ensuring maximum privacy.
- **Modern UI**: A sleek, Material 3 interface with full support for Edge-to-Edge displays.

---

## 🏗️ Architecture

Aura is built following **Clean Architecture** principles and a **Unidirectional Data Flow (UDF)** pattern, ensuring the codebase is scalable, testable, and maintainable.

### Layers
- **Core**: Contains shared logic, the design system (Material 3 components), and global data infrastructure (Database, DataStore).
- **Features**: Modularized by functional area (Record, Player, Statistics, etc.), each containing its own domain logic and data sources.
- **Screens**: Implementation of the UI layer using ViewModels that expose state via `StateFlow` and handle user interactions through defined `Actions` and `Events`.

### Key Patterns
- **MVVM / MVI-like**: ViewModels manage `UiState` and process `Actions`, providing a predictable state management flow.
- **Dependency Injection**: Powered by **Koin** using modern DSLs (`viewModelOf`, `singleOf`) for clean and lightweight injection.
- **Repository Pattern**: Abstracting data sources to allow for easy switching between local and (potentially) remote storage.

---

## 🛠️ Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Language**: [Kotlin](https://kotlinlang.org/) (Coroutines, Flow, Serialization)
- **Dependency Injection**: [Koin](https://insert-koin.io/)
- **Database**: [Room 3](https://developer.android.com/training/data-storage/room)
- **Local Storage**: [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Home Screen Widgets**: [Jetpack Glance](https://developer.android.com/jetpack/compose/glance)
- **Logging**: [Timber](https://github.com/JakeWharton/timber)
- **Media**: Android Media APIs for low-latency audio recording and playback.

---

## ⚙️ Development Conventions

- **Android 15 (API 37)**: Built targeting the latest Android features and security standards.
- **Edge-to-Edge**: Full implementation of immersive UI patterns.
- **Kotlin DSL**: Gradle builds configured with Kotlin DSL for better type safety and IDE support.
- **Version Catalog**: Centralized dependency management via `libs.versions.toml`.
- **Desugaring**: Enabled to support modern Java APIs on older Android versions (Min SDK 24).

---

## 📁 Project Structure

```text
aura/
├── app/
│   ├── src/main/java/com/raulastete/aura/
│   │   ├── app/             # Application class, DI modules, Navigation
│   │   ├── core/            # Database, Design System, Shared presentation logic
│   │   ├── features/        # Business logic modules (Record, Player, Settings)
│   │   ├── screens/         # Feature-specific UI screens and ViewModels
│   │   └── widgets/         # Glance Home Screen widgets
└── gradle/                  # Dependency management
```

---

## 🚀 Getting Started

1. Clone the repository.
2. Open in **Android Studio Ladybug (or newer)**.
3. Sync Gradle and run the `:app` module.

---

## 🗺️ Roadmap
- [ ] Dark mode optimization.
- [ ] Adaptive layout for tablets and foldables.
- [ ] Migration to Navigation 3.
- [ ] Exploring Kotlin Multiplatform (KMP) support.

---

**Developed with ❤️ by Raul Astete**
