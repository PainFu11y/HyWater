package com.example.hywater.presentation.screens.fountain_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.model.FountainStatus
import com.example.hywater.presentation.common.UiState
import com.example.hywater.presentation.theme.StatusApprovedContainer
import com.example.hywater.presentation.theme.StatusApprovedGreen
import com.example.hywater.presentation.theme.StatusPendingAmber
import com.example.hywater.presentation.theme.StatusPendingContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FountainDetailScreen(
    fountainId: Long,
    onBack: () -> Unit,
    viewModel: FountainDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(fountainId) { viewModel.load(fountainId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fountain Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                is UiState.Error -> Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )

                is UiState.Success -> FountainDetailContent(fountain = s.data)

                else -> Unit
            }
        }
    }
}

@Composable
private fun FountainDetailContent(fountain: Fountain) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Photo ────────────────────────────────────────────────────────────
        if (fountain.photoUrl != null) {
            AsyncImage(
                model = fountain.photoUrl,
                contentDescription = "Fountain photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Status chip ───────────────────────────────────────────────────
            StatusChip(status = fountain.status)

            // ── Description ───────────────────────────────────────────────────
            Text(
                text = fountain.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(4.dp))

            // ── Coordinates ───────────────────────────────────────────────────
            Text(
                text = "Coordinates: %.6f, %.6f".format(fountain.latitude, fountain.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ── Added date ────────────────────────────────────────────────────
            Text(
                text = "Added: ${fountain.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusChip(status: FountainStatus) {
    val (icon, containerColor, labelColor, label) = when (status) {
        FountainStatus.APPROVED -> Quadruple(
            Icons.Default.CheckCircle,
            StatusApprovedContainer,
            StatusApprovedGreen,
            "Approved"
        )
        FountainStatus.PENDING -> Quadruple(
            Icons.Default.Schedule,
            StatusPendingContainer,
            StatusPendingAmber,
            "Pending Moderation"
        )
    }

    SuggestionChip(
        onClick = {},
        label = { Text(label, color = labelColor) },
        icon = { Icon(icon, contentDescription = null, tint = labelColor) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor
        )
    )
}

// Simple data holder to make the destructuring in StatusChip readable
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
