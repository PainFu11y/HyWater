package com.example.hywater.presentation.screens.add_fountain

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.hywater.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFountainScreen(
    photoUriString: String?,
    onSubmitSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddFountainViewModel = hiltViewModel()
) {
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var description by rememberSaveable { mutableStateOf("") }

    val photoUri = photoUriString?.let { Uri.parse(it) }

    // Resolve location as soon as the screen opens
    LaunchedEffect(Unit) { viewModel.resolveLocation() }

    // React to submission results
    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is UiState.Success -> onSubmitSuccess()
            is UiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Fountain") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Photo preview ────────────────────────────────────────────────
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Fountain photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                )
            }

            // ── GPS coordinates display ──────────────────────────────────────
            location?.let { (lat, lng) ->
                Text(
                    text = "Location: %.6f, %.6f".format(lat, lng),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } ?: Text(
                text = "Resolving location…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ── Description field ────────────────────────────────────────────
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Describe the fountain (location, water quality, etc.)") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // ── Submit button ────────────────────────────────────────────────
            Button(
                onClick = {
                    photoUri?.let { viewModel.submit(it, description) }
                },
                enabled = submitState !is UiState.Loading && photoUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (submitState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit for Moderation")
                }
            }
        }
    }
}
