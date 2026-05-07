# 📝 NotesApp

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material3](https://img.shields.io/badge/Material_3-Design-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)
![SQLDelight](https://img.shields.io/badge/SQLDelight-Database-FF6F00?style=for-the-badge&logo=sqlite&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-4CAF50?style=for-the-badge)
![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

> A simple yet powerful cross-platform Notes application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**.  
> Manage your notes seamlessly with real-time search, dark mode support, and persistent local storage.

---

## 📸 Screenshots

> _Replace the images below with your actual screenshots. Place them inside the `screenshots/` folder at the root of your project._

| Notes List | Add / Edit Note | Search | Settings |
|:---:|:---:|:---:|:---:|
| ![Notes List](screenshots/screen_notes_list.png) | ![Add Edit](screenshots/screen_add_edit.png) | ![Search](screenshots/screen_search.png) | ![Settings](screenshots/screen_settings.png) |
| Browse all your notes | Create or edit a note | Real-time search by title or content | Toggle dark mode |

---

## ✨ Features

| Feature | Description |
|---|---|
| 📋 **CRUD Notes** | Create, read, update, and delete notes with ease |
| 🔍 **Real-time Search** | Instantly search notes by title or content via SQL queries |
| 🌙 **Dark Mode** | Toggle between light and dark themes, saved persistently on-device |
| 🧭 **Navigation** | Smooth screen-to-screen navigation with argument passing (Note ID) |
| ⚡ **Reactive UI** | UI auto-updates using `StateFlow` from repository to Compose layer |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **UI Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) + Material 3 |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Local Database** | [SQLDelight](https://cashapp.github.io/sqldelight/) |
| **Preferences** | [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) by Russhwolf |
| **Navigation** | [Navigation Compose](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html) |
| **Concurrency** | Kotlin Coroutines & Flow |
| **Dependency Mgmt** | Version Catalog (`libs.versions.toml`) |

---

## 📁 Project Structure

```
composeApp/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/
│   │   │   ├── App.kt                           # App entry point, NavHost, MaterialTheme
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── DatabaseProvider.kt      # Database initialization
│   │   │   │   │   └── DatabaseDriverFactory.kt # Platform driver factory (expect/actual)
│   │   │   │   ├── repository/
│   │   │   │   │   └── NoteRepository.kt        # Data access layer (ViewModel ↔ SQLDelight)
│   │   │   │   └── settings/
│   │   │   │       └── SettingsManager.kt       # App preferences (e.g., Dark Mode)
│   │   │   └── ui/
│   │   │       ├── notes/
│   │   │       │   ├── NotesScreen.kt           # Notes list screen
│   │   │       │   ├── NotesViewModel.kt
│   │   │       │   ├── AddEditNoteScreen.kt     # Add / edit note screen
│   │   │       │   └── AddEditNoteViewModel.kt
│   │   │       └── settings/
│   │   │           ├── SettingsScreen.kt        # Settings screen
│   │   │           └── SettingsViewModel.kt
│   │   └── sqldelight/
│   │       └── Note.sq                          # Table schema & SQL queries (CRUD, Search)
│   └── androidMain/
│       └── kotlin/
│           ├── MainActivity.kt                  # Android entry point, SharedPreferencesSettings
│           └── data/local/
│               └── ...                          # Android-specific SQLDelight driver
├── build.gradle.kts                             # Build config, plugins (KMP, SQLDelight, Compose)
└── gradle/
    └── libs.versions.toml                       # Centralized dependency version management
```

---

## 🚀 Getting Started

### Prerequisites

Before running the project, make sure you have the following installed:

- **Android Studio** Koala or newer
- **JDK** 11 or 17
- **Android SDK 34** (Compile SDK)
- Kotlin Plugin compatible with the project's Kotlin version

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/your-username/NotesApp.git
cd NotesApp
```

**2. Open in Android Studio**

```
File → Open → Select the cloned project folder
```

**3. Sync Gradle**

Android Studio will prompt you automatically. Or trigger it manually:

```
File → Sync Project with Gradle Files
```

**4. Run the Application**

Select a target device or emulator from the toolbar, then click **▶ Run** or press `Shift + F10`.

---

## 🏗️ Architecture Overview

This project follows the **MVVM** pattern with a clean unidirectional data flow:

```
┌─────────────────────────────────────────────┐
│               UI Layer (Compose)             │
│         observes StateFlow / State           │
└─────────────────┬───────────────────────────┘
                  │ user events
┌─────────────────▼───────────────────────────┐
│             ViewModel Layer                  │
│       holds UI state via StateFlow           │
└─────────────────┬───────────────────────────┘
                  │ suspend functions / Flow
┌─────────────────▼───────────────────────────┐
│           Repository Layer                   │
│    NoteRepository (single source of truth)   │
└─────────────────┬───────────────────────────┘
                  │ SQL queries
┌─────────────────▼───────────────────────────┐
│        SQLDelight (Local Database)           │
│              Note.sq schema                  │
└─────────────────────────────────────────────┘
```

- **`App.kt`** — sets up `NavHost`, applies `MaterialTheme`, and initializes `SettingsManager`
- **`NoteRepository`** — single source of truth for all note data
- **ViewModels** — expose `StateFlow` to the UI and handle all business logic
- **`SettingsManager`** — wraps Multiplatform Settings to persist user preferences

---

## 🗄️ Database Schema

Defined in `src/commonMain/sqldelight/Note.sq`:

```sql
-- Table Definition
CREATE TABLE Note (
    id          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title       TEXT    NOT NULL,
    content     TEXT    NOT NULL,
    dateCreated INTEGER NOT NULL,  -- Unix timestamp (milliseconds)
    dateUpdated INTEGER NOT NULL   -- Unix timestamp (milliseconds)
);

-- Select all notes (ordered by latest updated)
selectAll:
SELECT * FROM Note
ORDER BY dateUpdated DESC;

-- Select a single note by ID
selectById:
SELECT * FROM Note
WHERE id = ?;

-- Search notes by title or content
search:
SELECT * FROM Note
WHERE title LIKE '%' || :query || '%'
   OR content LIKE '%' || :query || '%'
ORDER BY dateUpdated DESC;

-- Insert a new note
insert:
INSERT INTO Note (title, content, dateCreated, dateUpdated)
VALUES (?, ?, ?, ?);

-- Update an existing note
update:
UPDATE Note
SET title = ?, content = ?, dateUpdated = ?
WHERE id = ?;

-- Delete a note by ID
delete:
DELETE FROM Note
WHERE id = ?;
```

---

## ⚙️ Settings & Preferences

User preferences are managed via [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings).

| Preference | Key | Type | Default |
|---|---|---|---|
| Dark Mode | `dark_mode` | `Boolean` | `false` |

On **Android**, `SharedPreferencesSettings` is initialized inside `MainActivity.kt` and injected into `SettingsManager` for use across the app.

---

## 🤝 Contributing

Contributions are very welcome! Please follow these steps:

1. Fork this repository
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'feat: add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

Please ensure your code follows the existing MVVM architecture and Kotlin coding conventions.

---

## 📄 License

```
MIT License

Copyright (c) 2024 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

---

_Made with ❤️ using Kotlin Multiplatform — If you find this project helpful, consider giving it a ⭐ on GitHub!_
