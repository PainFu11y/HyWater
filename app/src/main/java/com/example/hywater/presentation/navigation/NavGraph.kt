package com.example.hywater.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hywater.presentation.screens.add_fountain.AddFountainScreen
import com.example.hywater.presentation.screens.add_fountain.CameraScreen
import com.example.hywater.presentation.screens.fountain_detail.FountainDetailScreen
import com.example.hywater.presentation.screens.map.MapScreen

@Composable
fun HyWaterNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {

        composable(Screen.Map.route) {
            MapScreen(
                onAddFountainClick = { navController.navigate(Screen.Camera.route) },
                onMarkerClick = { fountainId ->
                    navController.navigate(Screen.FountainDetail.createRoute(fountainId))
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onPhotoCaptured = { photoUri ->
                    // Pass the URI to AddFountainScreen via the back stack entry
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("photo_uri", photoUri.toString())
                    navController.navigate(Screen.AddFountain.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddFountain.route) {
            // Retrieve the photo URI stored by CameraScreen
            val photoUriString = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("photo_uri")

            AddFountainScreen(
                photoUriString = photoUriString,
                onSubmitSuccess = {
                    // Pop the camera + add-fountain screens, return to map
                    navController.popBackStack(Screen.Map.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FountainDetail.route,
            arguments = listOf(
                navArgument(Screen.FountainDetail.ARG_FOUNTAIN_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val fountainId = backStackEntry.arguments
                ?.getLong(Screen.FountainDetail.ARG_FOUNTAIN_ID) ?: return@composable
            FountainDetailScreen(
                fountainId = fountainId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
