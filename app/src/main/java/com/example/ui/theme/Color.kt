package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// SBS Pro Signature Purple & Orange Palette
val SbsPurplePrimary = Color(0xFF4E1656)
val SbsPurpleDark = Color(0xFF380C3F)
val SbsPurpleLight = Color(0xFF7A2984)
val SbsPurpleContainer = Color(0xFFF3E5F5)

val SbsOrange = Color(0xFFFF6F00)
val SbsOrangeLight = Color(0xFFFF9E40)
val SbsOrangeDark = Color(0xFFC43E00)
val SbsGreen = Color(0xFF2E7D32)
val SbsRed = Color(0xFFE53935)

val SbsBackground = Color(0xFFF4F6F9)
val SbsSurface = Color(0xFFFFFFFF)
val SbsCardBg = Color(0xFFFAFAFB)
val SbsDivider = Color(0xFFE0E0E0)

val SbsTextPrimary = Color(0xFF212121)
val SbsTextSecondary = Color(0xFF666666)
val SbsTextLight = Color(0xFF9E9E9E)

// History & Report Tile Colors matching screenshot 3
val TileHistoryBlue = Color(0xFF0288D1)
val TileSmsRed = Color(0xFFE53935)
val TilePrepaidOrange = Color(0xFFFB8C00)
val TileBillPayBrown = Color(0xFF6D4C41)
val TileReceiveCyan = Color(0xFF00ACC1)
val TilePaymentGreen = Color(0xFF2E7D32)
val TileRecentPurple = Color(0xFF8E24AA)

val TileProfitLossPink = Color(0xFFE91E63)
val TileDailyReportBlue = Color(0xFF1976D2)
val Tile30DaysLilac = Color(0xFF9575CD)
val TileThisMonthSlate = Color(0xFF7E57C2)
val TileLastMonthPurple = Color(0xFF5E35B1)
val Tile3MonthDeepPurple = Color(0xFF4527A0)
val TileYearlyGreen = Color(0xFF43A047)
val TileCustomRangeViolet = Color(0xFFAB47BC)
val TileResellerBrown = Color(0xFF5D4037)

// Dynamic Color Themes for SBS Pro
enum class SbsThemeOption(
    val title: String,
    val subtitle: String,
    val primary: Color,
    val accent: Color,
    val container: Color,
    val darkSurface: Color
) {
    PURPLE_CLASSIC(
        title = "SBS Classic Purple",
        subtitle = "Original signature brand colors",
        primary = Color(0xFF4E1656),
        accent = Color(0xFFFF6F00),
        container = Color(0xFFF3E5F5),
        darkSurface = Color(0xFF380C3F)
    ),
    ROYAL_BLUE(
        title = "Royal Telecom Blue",
        subtitle = "Deep oceanic blue & cyan",
        primary = Color(0xFF0D47A1),
        accent = Color(0xFFFF9100),
        container = Color(0xFFE3F2FD),
        darkSurface = Color(0xFF0A2E68)
    ),
    EMERALD_GREEN(
        title = "Emerald Telecom",
        subtitle = "Lush green & sunset orange",
        primary = Color(0xFF00695C),
        accent = Color(0xFFFF6D00),
        container = Color(0xFFE0F2F1),
        darkSurface = Color(0xFF003D33)
    ),
    CRIMSON_RED(
        title = "Crimson Express",
        subtitle = "Bold energetic red & amber",
        primary = Color(0xFFC62828),
        accent = Color(0xFFFFB300),
        container = Color(0xFFFFEBEE),
        darkSurface = Color(0xFF8E0000)
    ),
    BKASH_PINK(
        title = "Magenta MFS",
        subtitle = "Mobile financial services style",
        primary = Color(0xFFE2136E),
        accent = Color(0xFFFFD600),
        container = Color(0xFFFCE4EC),
        darkSurface = Color(0xFF9C0046)
    ),
    NAGAD_SUNSET(
        title = "Sunset Orange",
        subtitle = "Vibrant fiery warm palette",
        primary = Color(0xFFD84315),
        accent = Color(0xFF29B6F6),
        container = Color(0xFFFBE9E7),
        darkSurface = Color(0xFF870000)
    ),
    DEEP_INDIGO(
        title = "Deep Indigo",
        subtitle = "Sleek professional modern navy",
        primary = Color(0xFF283593),
        accent = Color(0xFF00E676),
        container = Color(0xFFE8EAF6),
        darkSurface = Color(0xFF1A237E)
    ),
    MIDNIGHT_DARK(
        title = "Midnight Carbon",
        subtitle = "High contrast AMOLED black & gold",
        primary = Color(0xFF212121),
        accent = Color(0xFFFFC107),
        container = Color(0xFF424242),
        darkSurface = Color(0xFF121212)
    )
}
