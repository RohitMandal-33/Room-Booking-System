package com.swifttechnology.bookingsystem.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = SurfaceVariant,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = SurfaceVariantDark,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    error = Error
)

@Immutable
data class CustomColors(
    val dashboardBg: androidx.compose.ui.graphics.Color,
    val loginBtnBg: androidx.compose.ui.graphics.Color,
    val loginBtnBorder: androidx.compose.ui.graphics.Color,
    val neutral100: androidx.compose.ui.graphics.Color,
    val neutral200: androidx.compose.ui.graphics.Color,
    val neutral300: androidx.compose.ui.graphics.Color,
    val neutral400: androidx.compose.ui.graphics.Color,
    val neutral700: androidx.compose.ui.graphics.Color,
    val deepBlack: androidx.compose.ui.graphics.Color,
    val whitePure: androidx.compose.ui.graphics.Color,
    val yellow100: androidx.compose.ui.graphics.Color,
    val green100: androidx.compose.ui.graphics.Color,
    val purple100: androidx.compose.ui.graphics.Color,
    val textBody: androidx.compose.ui.graphics.Color,
    val bookRoomInputBackground: androidx.compose.ui.graphics.Color,
    val bookRoomPlaceholder: androidx.compose.ui.graphics.Color,
    val bookRoomLabel: androidx.compose.ui.graphics.Color,
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        dashboardBg = androidx.compose.ui.graphics.Color.Unspecified,
        loginBtnBg = androidx.compose.ui.graphics.Color.Unspecified,
        loginBtnBorder = androidx.compose.ui.graphics.Color.Unspecified,
        neutral100 = androidx.compose.ui.graphics.Color.Unspecified,
        neutral200 = androidx.compose.ui.graphics.Color.Unspecified,
        neutral300 = androidx.compose.ui.graphics.Color.Unspecified,
        neutral400 = androidx.compose.ui.graphics.Color.Unspecified,
        neutral700 = androidx.compose.ui.graphics.Color.Unspecified,
        deepBlack = androidx.compose.ui.graphics.Color.Unspecified,
        whitePure = androidx.compose.ui.graphics.Color.Unspecified,
        yellow100 = androidx.compose.ui.graphics.Color.Unspecified,
        green100 = androidx.compose.ui.graphics.Color.Unspecified,
        purple100 = androidx.compose.ui.graphics.Color.Unspecified,
        textBody = androidx.compose.ui.graphics.Color.Unspecified,
        bookRoomInputBackground = androidx.compose.ui.graphics.Color.Unspecified,
        bookRoomPlaceholder = androidx.compose.ui.graphics.Color.Unspecified,
        bookRoomLabel = androidx.compose.ui.graphics.Color.Unspecified,
    )
}

private val LightCustomColors = CustomColors(
    dashboardBg = DashboardBg,
    loginBtnBg = LoginBtnBg,
    loginBtnBorder = LoginBtnBorder,
    neutral100 = Neutral100,
    neutral200 = Neutral200,
    neutral300 = Neutral300,
    neutral400 = Neutral400,
    neutral700 = Neutral700,
    deepBlack = DeepBlack,
    whitePure = WhitePure,
    yellow100 = Yellow100,
    green100 = Green100,
    purple100 = Purple100,
    textBody = TextBody,
    bookRoomInputBackground = BookRoomInputBackground,
    bookRoomPlaceholder = BookRoomPlaceholder,
    bookRoomLabel = BookRoomLabel,
)

private val DarkCustomColors = CustomColors(
    dashboardBg = DashboardBgDark,
    loginBtnBg = LoginBtnBgDark,
    loginBtnBorder = LoginBtnBorderDark,
    neutral100 = Neutral100Dark,
    neutral200 = Neutral200Dark,
    neutral300 = Neutral300Dark,
    neutral400 = Neutral400Dark,
    neutral700 = Neutral700Dark,
    deepBlack = WhitePure, // Swap text color
    whitePure = DeepBlack, // Swap background color
    yellow100 = Yellow100Dark,
    green100 = Green100Dark,
    purple100 = Purple100Dark,
    textBody = TextBodyDark,
    bookRoomInputBackground = BookRoomInputBackgroundDark,
    bookRoomPlaceholder = BookRoomPlaceholderDark,
    bookRoomLabel = BookRoomLabelDark,
)

/**
 * Custom Colors extension property access
 */
val MaterialTheme.customColors: CustomColors
    @Composable
    get() = LocalCustomColors.current

/**
 * Root design-system theme wrapper.
 * All screens/composables should be wrapped inside this.
 * Mirrors iOS MeetingRoomTheme.
 */
@Composable
fun MeetingRoomBookingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(
        LocalCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
