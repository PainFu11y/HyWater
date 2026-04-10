package com.example.hywater.presentation.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.model.FountainStatus
import com.example.hywater.presentation.common.UiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onAddFountainClick: () -> Unit,
    onMarkerClick: (Long) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val fountainsState by viewModel.fountainsState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val locationError by viewModel.locationError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // ── Location permissions ──────────────────────────────────────────────────
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            viewModel.fetchUserLocation()
        } else {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    // ── Show location error via Snackbar ─────────────────────────────────────
    LaunchedEffect(locationError) {
        locationError?.let { snackbarHostState.showSnackbar(it) }
    }

    // ── Camera ───────────────────────────────────────────────────────────────
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(48.8566, 2.3522), 12f) // default: Paris
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Box {
                // "Add Fountain" FAB
                ExtendedFloatingActionButton(
                    onClick = onAddFountainClick,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Fountain") },
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissions.allPermissionsGranted
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false, // we provide our own FAB below
                    zoomControlsEnabled = false
                )
            ) {
                // Render fountain markers
                if (fountainsState is UiState.Success) {
                    val fountains = (fountainsState as UiState.Success<List<Fountain>>).data
                    fountains.forEach { fountain ->
                        FountainMarker(
                            fountain = fountain,
                            onClick = { onMarkerClick(fountain.id) }
                        )
                    }
                }
            }

            // "My Location" secondary FAB (bottom-start)
            if (locationPermissions.allPermissionsGranted) {
                FloatingActionButton(
                    onClick = { viewModel.fetchUserLocation() },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 16.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "My location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Loading overlay while the initial cache is empty
            if (fountainsState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

// ── Fountain marker ───────────────────────────────────────────────────────────

@Composable
private fun FountainMarker(
    fountain: Fountain,
    onClick: () -> Unit
) {
    val hue = if (fountain.status == FountainStatus.APPROVED) {
        BitmapDescriptorFactory.HUE_AZURE
    } else {
        BitmapDescriptorFactory.HUE_YELLOW // pending = yellow so moderators notice
    }

    Marker(
        state = MarkerState(position = LatLng(fountain.latitude, fountain.longitude)),
        title = fountain.description.take(40),
        snippet = fountain.status.label,
        icon = BitmapDescriptorFactory.defaultMarker(hue),
        onClick = {
            onClick()
            true // consume the event so the default info window doesn't open
        }
    )
}
