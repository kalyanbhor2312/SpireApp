<img width="1080" height="2400" alt="Screenshot_20260925_115659" src="https://github.com/user-attachments/assets/d75cd204-7350-49d9-ad8c-49c4ca23e9aa" /># Spire - Android App Components and Data Handling

Spire is a comprehensive Android application designed to track and explore the world's tallest buildings. It serves as a showcase for modern Android development practices, focusing on robust data handling, offline-first architecture, and reactive UI patterns.

## 🏗 Architecture & Concepts

The app follows the **Clean Architecture** principles and the recommended **MVVM (Model-View-ViewModel)** architectural pattern.

### 1. Layers
-   **Presentation Layer**: Fragments and ViewModels using Jetpack components. ViewModels expose data via `Flow` and `LiveData` to ensure lifecycle-aware UI updates.
-   **Domain Layer**: Clean Kotlin data classes representing the core business logic (`Building`, `BuildingStatistics`, etc.).
-   **Data Layer**: Implements the **Repository Pattern**. `DefaultBuildingRepository` manages data synchronization between a local Room database and a remote REST API.

### 2. Core Concepts & Libraries
-   **Paging 3 (RemoteMediator)**: Implements efficient pagination. Data is fetched from the network and cached in the local Room database, which serves as the "Single Source of Truth."
-   **Room Persistence**: A robust local SQLite abstraction used for caching building data, city/country relations, and pagination keys.
-   **Retrofit & Gson**: Used for type-safe networking and JSON serialization/deserialization.
-   **Coroutines & Flow**: Facilitates asynchronous operations and provides a reactive stream of data from the database to the UI.
-   **Jetpack Navigation**: Handles fragment transitions, deep linking, and SafeArgs for type-safe argument passing.
-   **View Binding**: Ensures safe and efficient interaction with XML layouts.
-   **Coil**: An image loading library used for efficient, coroutine-based image loading.

---

## ✅ Implemented TODOs

The project implementation covers 50 specific tasks across the entire stack:

### Data Layer (Local)
-   **TODO #1-6**: Defined Room Entities (`Country`, `City`, `Building`, `RemoteKeys`) with proper primary keys, foreign keys, and indices to ensure data integrity and performance.
-   **TODO #7-21**: Implemented DAOs with complex SQL queries, including triple JOINs for relational data and aggregate functions (`SUM`, `COUNT`) for statistics.
-   **TODO #22**: Configured the `SpireDatabase` with TypeConverters for Enum handling.

### Data Layer (Remote & Synchronization)
-   **TODO #23-27**: Built DTO (Data Transfer Object) classes with `@SerializedName` to match API specifications and mapping logic to Domain models.
-   - **TODO #28**: Implemented `BuildingRemoteMediator` to synchronize network data with the local database, handling `REFRESH`, `APPEND`, and `PREPEND` load types.

### Repository & Business Logic
-   **TODO #29-37**: Implemented `DefaultBuildingRepository`, coordinating Paging data streams and handling manual data refreshes and status updates.
-   **TODO #47**: Integrated the real repository into the `SpireApplication` container.

### UI Layer (ViewModel & Adapter)
-   **TODO #38-42**: Implemented ViewModels (`Buildings`, `Detail`, `Visits`, `Statistics`) using `viewModelScope`, `switchMap`, and `asLiveData` for reactive UI states.
-   **TODO #43-44**: Created `BuildingPagingAdapter` (Paging 3) and `BuildingAdapter` (ListAdapter) with `DiffUtil` for smooth list animations.

### Navigation & UI Implementation
-   **TODO #46**: Set up the `NavController` with `BottomNavigationView` and `AppBarConfiguration` in `MainActivity`.
-   **TODO #48-50**: Finalized the Building Detail screen with image loading, data binding, and interactive visit status toggles.

---

## 🚀 How to Run
1.  Open the project in **Android Studio**.
2.  Perform a **Gradle Sync**.
3.  Run the app on an emulator or physical device (API 24+).
4.  Use the **Buildings** tab to explore, and **Pull-to-Refresh** to fetch the latest data from the cloud.
## Screenshots 
![Uploading Screenshot_20260925_115659.p<img width="760" height="800" alt="Screenshot_20260925_115732" src="https://github.com/user-attachments/assets/59942992-4a14-48c1-9e09-72763143c045" />
<i<img width="760" height="800" alt="Screenshot_20260925_115907" src="https://github.com/user-attachments/assets/b40d558b-9191-4553-8fc8-57fbacaf3684" />
<img width="760" height="800" alt="Screenshot_20260925_115850" src="https://github.com/user-attachments/assets/3f2c6f7b-9c52-456e-875d-7495d684acb7" />
<img width="760" height="800" alt="Screenshot_20260925_115803" src="https://github.com/user-attachments/assets/8776265b-16ec-46aa-aeda-b6b936cf86e2" />
<img width="760" height="800" alt="Screenshot_20260925_115747" src="https://github.com/user-attachments/assets/49c1901b-27d4-4fdb-8f75-ed8d668c52a5" />
mg width="760" height="800" alt="Screenshot_20260925_115717" src="https://github.com/user-attachments/assets/b8719264-1204-4776-9401-c347e4272d98" />
ng…]()


