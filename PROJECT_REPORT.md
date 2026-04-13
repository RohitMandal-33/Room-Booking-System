# Extensive Technical Report: Swift Technology Booking System

## 1. Executive Summary
This document provides a comprehensive technical deep-dive into the Swift Technology Booking System, an Android application built using modern industry standards. The project leverages **Kotlin**, **Jetpack Compose**, and a **Clean Architecture + MVVM** pattern to deliver a robust, maintainable, and highly performant room booking solution.

---

## 2. Architectural Philosophy: Clean Architecture & MVVM
The application is built on the principles of **Clean Architecture**, which prioritizes the independence of the business logic from external frameworks, UI, and databases.

### The Dependency Rule
Dependencies only point inwards. The core business logic (Domain) knows nothing about the UI (Presentation) or the Database/Network (Data).

### Layered Breakdown
1.  **Domain Layer (Entities, Use Cases, Repositories Interfaces):**
    - **Entities:** Pure Kotlin data classes representing the business models (e.g., `MeetingEvent`, `Room`, `Participant`).
    - **Use Cases (Interactors):** Classes that encapsulate a specific business rule or task (e.g., `LoginUseCase`, `GetBookingsUseCase`). This ensures that the ViewModel doesn't become bloated with logic.
    - **Repository Interfaces:** Define the contract for data operations.

2.  **Data Layer (Repository Implementations, API Services, DTOs, Mappers):**
    - **Repository Impl:** Implements the Domain's interfaces. It decides whether to fetch data from the network or a local cache.
    - **API Services (Retrofit):** Define the REST endpoints.
    - **DTOs (Data Transfer Objects):** Represent the JSON structure from the API.
    - **Mappers:** Functions that convert DTOs to Domain Entities. This isolation ensures that API changes only affect the Data layer.

3.  **Presentation Layer (UI, ViewModels, UI State):**
    - **ViewModels:** Maintain the state of the UI and handle user interactions by calling Use Cases. They use `StateFlow` to emit immutable state updates.
    - **Composables:** Declarative UI components that react to state changes.
    - **UI State:** Data classes that represent the entire state of a screen at any given time.

---

## 3. Project Structure: Package by Feature
The project uses a feature-based organization, which scales better than a layer-based organization.

```text
com.swifttechnology.bookingsystem/
├── app/                        # Application-wide Dagger Hilt configuration
├── core/                       # Shared infrastructure
│   ├── designsystem/           # Theme, Typography, Colors, Spacing
│   ├── network/                # Retrofit setup, Interceptors, Authenticators
│   ├── storage/                # Token management (DataStore)
│   └── util/                   # Common utilities (DateTime, Extensions)
├── features/                   # Business Features
│   ├── auth/                   # Login, Logout, Password Reset
│   ├── booking/                # Room Booking Flow, Calendar BottomSheet
│   ├── calendar/               # Main Calendar (Month/Week/Day Views)
│   ├── dashboard/              # Home screen with stats and previews
│   ├── meetingrooms/           # Room listing, Add/Edit rooms
│   └── ...
├── navigation/                 # Navigation Graph and Route definitions
└── shared/                     # Reusable UI components (Buttons, TextFields)
```

---

## 4. Deep Dive: The Network Module
The networking layer is the backbone of the app, handled by **Retrofit** and **OkHttp**.

### Interceptor: `TokenPlugin`
This interceptor acts as a middleware. It intercepts every outgoing request and injects the `Authorization` header.
```kotlin
override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    // Avoid adding tokens to public endpoints like /login or /verify-otp
    if (isPublic(originalRequest.url.encodedPath)) return chain.proceed(originalRequest)

    val token = runBlocking { tokenStorage.getAccessToken() }
    val authorizedRequest = originalRequest.newBuilder()
        .header("Authorization", "Bearer $token")
        .build()
    return chain.proceed(authorizedRequest)
}
```

### Authenticator: `TokenAuthenticator`
When the API returns a `401 Unauthorized`, this class is triggered. It attempts to refresh the access token using the refresh token stored in `DataStore`.
- If successful: The new token is saved, and the original request is retried.
- If it fails (e.g., refresh token expired): It triggers a logout.
- It uses a `Mutex` to prevent multiple concurrent requests from trying to refresh the token simultaneously.

### DTO vs. Domain Model Example
The app strictly separates API models from UI models.
```kotlin
// Data Layer (DTO)
data class RoomDTO(val id: Long, val room_name: String, val capacity: Int)

// Domain Layer (Entity)
data class Room(val id: Long, val name: String, val capacity: Int)

// Mapper Logic
fun RoomDTO.toDomain() = Room(id = id, name = room_name, capacity = capacity)
```

---

## 5. UI Implementation: Jetpack Compose
The UI is 100% declarative, avoiding XML entirely.

### Unidirectional Data Flow (UDF)
We use a strict UDF pattern to keep the UI predictable.
1.  **User Action:** User clicks "Book Room".
2.  **ViewModel:** `onBookRoomClicked()` is called. It updates `uiState` to `isLoading = true` and launches a coroutine.
3.  **Use Case:** The ViewModel calls `bookRoomUseCase(...)`.
4.  **UI State Update:** Upon success/failure, the ViewModel updates the `uiState`.
5.  **Composition:** Jetpack Compose detects the `StateFlow` change and re-renders the specific components that need updating.

### Complex Component: The Calendar
The Calendar feature (found in `features/calendar`) is a masterclass in custom Compose layouts. It supports:
- **Month View:** Grid-based layout showing meeting dots.
- **Week/Day View:** A custom-drawn time-grid.
- **State Management:** Uses `YearMonth` and `LocalDate` (Java 8 Time API) to manage navigation between dates.

### Design System
A custom design system is implemented in `core.designsystem`.
- **`MeetingRoomBookingTheme`:** A wrapper that provides `MaterialTheme` along with a `LocalCustomColors` provider for non-material colors (e.g., specific brand colors like `Yellow100`).
- **CompositionLocal:** Used to provide colors and spacing down the UI tree without manual prop drilling.

---

## 6. Dependency Injection with Hilt
Hilt is used for dependency injection, significantly reducing boilerplate.

- **`NetworkModule`:** Provides Singleton instances of `OkHttpClient`, `Retrofit`, and API Services.
- **Feature Modules:** Each feature has its own Hilt module (e.g., `BookingModule`) to bind repository interfaces to their implementations.
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {
    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository
}
```

---

## 7. Advanced Navigation
The app uses a custom navigation wrapper around `Navigation Compose`.
- **`ScreenRoutes`:** A central object containing all route strings.
- **`AppNavGraph`:** Defines the `NavHost` and maps routes to Composable screens.
- **Argument Passing:** Supports type-safe (ish) argument passing via URL-like syntax (e.g., `meeting_room_edit/{roomName}`).

---

## 8. Conclusion
The Swift Technology Booking System is a highly structured, enterprise-grade Android application. By strictly following Clean Architecture and utilizing the latest Jetpack Compose features, it ensures that the code remains maintainable as the project grows. The sophisticated network layer and unified state management via MVVM provide a seamless and reliable experience for the end-user.
