# Project Technical Report: Swift Technology Booking System

## 1. Introduction
This report provides a detailed technical overview of the Swift Technology Booking System Android application. It explores the architectural choices, project structure, and implementation details of key components, specifically focusing on Jetpack Compose, Kotlin, MVVM with Clean Architecture, and the networking stack.

## 2. Architectural Overview
The project follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** design pattern. This approach ensures a strict separation of concerns, making the codebase maintainable, testable, and scalable.

### Why Clean Architecture + MVVM?
- **Separation of Concerns:** Each layer has a specific responsibility, reducing coupling.
- **Testability:** Business logic (Domain layer) is isolated from UI and Framework dependencies, allowing for easy unit testing.
- **Maintainability:** Changes in the UI or data source (e.g., switching from Retrofit to another library) don't affect the core business logic.
- **Reactive UI:** MVVM with Jetpack Compose allows for a seamless, state-driven UI experience.

### Layer Breakdown
1.  **Domain Layer (Core Business Logic):**
    - Contains **Entities** (Models), **Repository Interfaces**, and **Use Cases**.
    - It is the innermost layer and has no dependencies on any other layer.
2.  **Data Layer (Infrastructure):**
    - Implements the Repository interfaces defined in the Domain layer.
    - Handles data sourcing from the Network (Retrofit) and Local Storage (DataStore).
    - Contains **DTOs** (Data Transfer Objects) and Mappers.
3.  **Presentation Layer (UI):**
    - Follows the MVVM pattern.
    - **ViewModels** manage UI state and interact with Use Cases.
    - **Composables** (Jetpack Compose) observe state and render the UI.

---

## 3. Project Structure
The project is organized by feature, which is a common practice for large-scale Android applications. This "Package by Feature" approach improves discoverability and modularity.

```text
app/src/main/java/com/swifttechnology/bookingsystem/
├── app/               # Application-level configuration (Hilt Modules)
├── core/              # Shared core logic (Network, Storage, Design System)
├── features/          # Feature-specific modules
│   ├── auth/          # Authentication feature
│   │   ├── data/      # Data layer (API, DTOs, Repository Impl)
│   │   ├── domain/    # Domain layer (Models, Repository Interface, Use Cases)
│   │   └── presentation/# UI layer (ViewModels, Screens, Components)
│   ├── booking/       # Room booking feature
│   ├── calendar/      # Calendar visualization feature
│   ├── dashboard/     # Main dashboard
│   └── ...            # Other features (meetingrooms, participants, etc.)
├── navigation/        # Navigation graph and route definitions
└── shared/            # Common UI components and utilities
```

---

## 4. MVVM & Clean Architecture Implementation
To illustrate the implementation, let's look at the **Authentication** feature.

### Domain Layer: Use Case
Use Cases encapsulate a single task. This `LoginUseCase` depends only on the `AuthRepository` interface.

```kotlin
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<Unit> =
        repository.login(email, password)
}
```

### Data Layer: Repository Implementation
The `AuthRepositoryImpl` handles the networking logic and token persistence.

```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthResult<Unit> {
        return try {
            val response = api.login(LoginRequestDTO(email = email, password = password))
            if (!response.success) {
                AuthResult.Error(response.message)
            } else {
                val tokens = response.data ?: return AuthResult.Error("Invalid response")
                tokenStorage.saveAccessToken(tokens.accessToken)
                tokens.refreshToken?.let { tokenStorage.saveRefreshToken(it) }
                AuthResult.Success(Unit)
            }
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }
    // ...
}
```

### Presentation Layer: ViewModel
The `LoginViewModel` maintains a `UiState` using `MutableStateFlow` and exposes it as an immutable `StateFlow` to the UI.

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = loginUseCase(uiState.value.email, uiState.value.password)
            // Update state based on result
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
```

---

## 5. UI Layer with Jetpack Compose
The UI is entirely declarative, built with Jetpack Compose. Screens are driven by state emitted by ViewModels.

### Unidirectional Data Flow (UDF)
1.  **Events:** The UI sends events (e.g., `onLoginClicked`) to the ViewModel.
2.  **State:** The ViewModel updates the `UiState`.
3.  **Display:** The UI observes the state and re-composes.

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        LoginTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            errorMessage = uiState.emailError
        )
        // ...
        Button(onClick = viewModel::onLoginClicked) {
            if (uiState.isLoading) CircularProgressIndicator() else Text("Login")
        }
    }
}
```

---

## 6. Network Layer Deep Dive
The networking stack is built using **Retrofit** and **OkHttp**, with **Hilt** managing the dependencies.

### Architecture of a Request
1.  **Retrofit Service:** Interfaces define API endpoints.
2.  **OkHttpClient:** Configured with Interceptors for cross-cutting concerns.
3.  **Hilt Modules:** `NetworkModule` provides the Singleton instances.

### Token Management & Security
The project uses a sophisticated mechanism for handling JWT tokens via OkHttp Interceptors:

1.  **TokenPlugin (Interceptor):** Automatically attaches the `Authorization: Bearer <token>` header to outgoing requests, except for public endpoints (like `/login`).
2.  **TokenAuthenticator (Authenticator):** This is triggered when the server returns a **401 Unauthorized** error. It attempts to refresh the access token using a refresh token and retries the original request seamlessly.

```kotlin
// Simplified TokenPlugin logic
class TokenPlugin(private val tokenStorage: Lazy<TokenStorage>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (isPublic(originalRequest)) return chain.proceed(originalRequest)

        val token = runBlocking { tokenStorage.get().getAccessToken() }
        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

---

## 7. Navigation
The app uses the **Jetpack Compose Navigation** library. `AppNavGraph` centralizes all route definitions.

```kotlin
@Composable
fun AppNavGraph(navController: NavHostController, ...) {
    NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {
        composable(ScreenRoutes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(ScreenRoutes.MAIN_APP)
            })
        }
        composable(ScreenRoutes.MAIN_APP) {
            MainAppScreen(...)
        }
        // ...
    }
}
```

---

## 8. Conclusion
The Swift Technology Booking System project is a modern Android application that strictly adheres to industry best practices. By combining Clean Architecture with MVVM and Jetpack Compose, it achieves a high degree of modularity and a responsive user experience. The robust network layer with automatic token refreshing ensures a secure and seamless interaction with the backend services.
