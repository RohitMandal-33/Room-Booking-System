package com.swifttechnology.bookingsystem.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

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
    val dashboardBg: Color,
    val loginBtnBg: Color,
    val loginBtnBorder: Color,
    val neutral100: Color,
    val neutral200: Color,
    val neutral300: Color,
    val neutral400: Color,
    val neutral700: Color,
    val deepBlack: Color,
    val whitePure: Color,
    val yellow100: Color,
    val green100: Color,
    val purple100: Color,
    val textBody: Color,
    val bookRoomInputBackground: Color,
    val bookRoomPlaceholder: Color,
    val bookRoomLabel: Color,
    val divider: Color,
    val dividerLight: Color,
    val surfaceLight: Color,
    val textHint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val primaryLight: Color,
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        dashboardBg = Color.Unspecified,
        loginBtnBg = Color.Unspecified,
        loginBtnBorder = Color.Unspecified,
        neutral100 = Color.Unspecified,
        neutral200 = Color.Unspecified,
        neutral300 = Color.Unspecified,
        neutral400 = Color.Unspecified,
        neutral700 = Color.Unspecified,
        deepBlack = Color.Unspecified,
        whitePure = Color.Unspecified,
        yellow100 = Color.Unspecified,
        green100 = Color.Unspecified,
        purple100 = Color.Unspecified,
        textBody = Color.Unspecified,
        bookRoomInputBackground = Color.Unspecified,
        bookRoomPlaceholder = Color.Unspecified,
        bookRoomLabel = Color.Unspecified,
        divider = Color.Unspecified,
        dividerLight = Color.Unspecified,
        surfaceLight = Color.Unspecified,
        textHint = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        primaryLight = Color.Unspecified,
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
    divider = Divider,
    dividerLight = DividerLight,
    surfaceLight = SurfaceLight,
    textHint = TextHint,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    primaryLight = PrimaryLight,
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
    divider = DividerDark,
    dividerLight = DividerLightDark,
    surfaceLight = SurfaceLightDark,
    textHint = TextHintDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    primaryLight = PrimaryLightDark,
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
