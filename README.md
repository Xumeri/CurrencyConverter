# CurrencyConverter

An intuitive Android application built in Java for converting global currencies, managing custom exchange rates, and exploring currency locations.

## Features

- **Currency Conversion:** Easily convert amounts between EUR and over 30 global currencies.
- **Offline Storage & Caching:** Caches exchange rates locally using an SQLite database (`SQLiteOpenHelper`).
- **Live Rate Updates:** Fetches daily live exchange rate data via `OkHttpClient` from the FloatRates API.
- **Background Synchronization:** Uses Android `WorkManager` for daily automated rate updates in the background.
- **Interactive Maps Integration:** Tap any currency in the list to open its issuing country's capital in Google Maps via Geo Intents.
- **Custom Rate Editing:** Enable Edit Mode to manually customize exchange rates for specific currencies.
- **User Preferences:** Preserves selected currencies and user inputs using `SharedPreferences`.

## Tech Stack & Architecture

- **Language:** Java
- **UI Framework:** Android Views / Material Toolbars / Spinners & ListViews
- **Networking:** OkHttpClient & JSON Parsing
- **Persistence:** SQLite Database & SharedPreferences
- **Background Operations:** WorkManager (`PeriodicWorkRequest`) & Notifications
- **External Integration:** Android Geo Intent (`com.google.android.apps.maps`)
