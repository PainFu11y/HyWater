package com.example.hywater.presentation.navigation

/**
 * Sealed class of all navigation routes.
 * Using a sealed class (instead of raw strings scattered through the code)
 * makes refactoring routes safe — the compiler catches every call site.
 */
sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object Camera : Screen("camera")
    data object AddFountain : Screen("add_fountain")

    data object FountainDetail : Screen("fountain_detail/{fountainId}") {
        fun createRoute(fountainId: Long) = "fountain_detail/$fountainId"
        const val ARG_FOUNTAIN_ID = "fountainId"
    }
}
