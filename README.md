# 🛍️ Swipe Assignment V31 

A modern Android app built using **Jetpack Compose**, following a clean **MVVM + Repository architecture**, and powered by **Koin**, **Room**, **Retrofit**, and **WorkManager**.

This project was built as part of an internship challenge for **Swipe (YC startup)**. It demonstrates offline-first data handling, background sync, and a smooth Compose UI for managing product listings.

---

## 🚀 Features

✅ **Modern Android Architecture**  
- MVVM + Repository pattern with clear domain, data, and UI layers.  
- Unidirectional data flow with Kotlin Flows and coroutines.  

✅ **Offline-first Experience**  
- Products are stored locally with Room.  
- Failed uploads are queued and auto-synced when network returns.  

✅ **Background Sync**  
- Uses WorkManager for periodic background uploads.  
- Automatically reconciles offline and server data.  

✅ **Koin Dependency Injection**  
- Koin modules for network, database, repositories, ViewModels, and workers.  

✅ **Beautiful Compose UI**  
- Material 3 design with dynamic theming.  
- Smooth transitions, gradient cards, and responsive layouts.  

✅ **Notifications**  
- Android Notification API alerts users when uploads succeed or sync completes.  

✅ **ProGuard-Optimized Release**  
- Safe minification and resource shrinking with tuned rules for all major libraries.  

---

## 🧱 Architecture Overview

```text
com.iamashad.ashad_swipe
│
├── data/               # Network + local data sources
│   ├── db/             # Room entities, DAO, database
│   ├── remote/         # Retrofit APIs and DTOs
│
├── domain/             # Business logic layer
│   ├── model/          # Core domain models
│   ├── repo/           # Repository + Resource wrapper
│
├── userinterface/      # UI layer (Jetpack Compose)
│   ├── add/            # Add Product screen + ViewModel
│   ├── list/           # Product List screen + ViewModel
│
├── widgets/            # Reusable composables
│   ├── bars/           # Top bars
│   ├── dialogs/        # Alert dialogs
│   ├── fabs/           # Floating buttons
│
├── util/               # Utilities (notifications, theming, formatting)
│
├── work/               # WorkManager workers
│
├── di/                 # Dependency injection modules (Koin)
│
├── App.kt              # Application entry point
├── MainActivity.kt     # Main host activity
└── AppViewModel.kt     # Handles theme + splash readiness
```

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **UI** | Jetpack Compose, Material 3, Coil 3 |
| **Architecture** | MVVM + Repository pattern |
| **DI** | Koin |
| **Networking** | Retrofit 3, Kotlinx Serialization |
| **Persistence** | Room (KSP), DataStore |
| **Async / Background** | Coroutines + WorkManager |
| **Design & UX** | Material You (dynamic theming), Lottie |
| **Other** | NotificationManager, Palette API for image gradients |

---

## 🧩 Project Setup

### 1️⃣ Prerequisites
- Android Studio **Ladybug+ (2025.1 or newer)**
- **JDK 17**
- **Gradle 8.9+**
- Android SDK **24–36**

---

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/<your-username>/ashad-swipe.git
cd ashad-swipe
```

### 3️⃣ Build the App
```bash
./gradlew assembleDebug
```

For release build (with minify & shrink):
```bash
./gradlew assembleRelease
```

### 4️⃣ Run the App

Plug in a device or start an emulator (Android 8.0+).

Run from Android Studio or via:

adb shell am start -n com.iamashad.ashad_swipe/.MainActivity

### 🔄 Background Sync

Offline uploads saved via PendingUploadEntity.

UploadPendingWorker runs every 15 min (network-connected).

When online, it uploads pending products and triggers notifications.

### 🧠 Dependency Injection
| Module             | Provides                        |
| ------------------ | ------------------------------- |
| `networkModule`    | Retrofit + OkHttp + JSON        |
| `databaseModule`   | Room database + DAOs            |
| `repositoryModule` | Repository implementation       |
| `viewModelModule`  | ViewModels for Add/List screens |
| `workModule`       | WorkManager workers             |


Initialized in App.kt during application startup.

### 🖌️ UI Highlights

Dynamic Material 3 theming via wallpaper colors

Gradient-based product cards using Palette API

Smooth animations with Compose motion APIs

Offline badges and shine effects for feedback



