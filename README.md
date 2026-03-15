# 🍳 My Recipe App

An Android app for browsing and discovering recipes, built with Jetpack Compose.

## Features

- **Splash screen** — preloads recipe data in the background while displaying the app logo
- **Recipe list** — browse recipes with images and titles, with infinite scroll pagination
- **Search** — filter recipes by title in real time
- **Category filter** — filter recipes by category using chip buttons
- **Recipe detail** — view full recipe info including ingredients and instructions
- **Offline support** — recipes are cached in a local Room database and available without internet
- **Auto cache refresh** — cached data older than 1 hour is automatically refreshed from the API
- **Error handling** — network errors show a message with a retry button

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose
- **Networking**: Retrofit + Gson
- **Local database**: Room
- **Image loading**: Coil
- **Architecture**: MVVM (ViewModel + Repository)

## API

Data is fetched from [TheMealDB](https://www.themealdb.com/api.php) — a free, open recipe database.

## Project Structure

```
app/src/main/java/com/example/myrecipeapp/
├── database/       # Room DB (entities, DAO)
├── model/          # Data models
├── network/        # Retrofit API
├── repository/     # Data layer
├── ui/screen/      # Compose screens
├── ui/theme/       # Colors, typography
└── viewmodel/      # ViewModels
```

## Requirements

- Android 8.0 (Oreo / API 26) or higher
