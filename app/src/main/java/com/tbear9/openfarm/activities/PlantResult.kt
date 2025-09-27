package com.tbear9.openfarm.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trbear9.plants.api.blob.Plant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    plants: SnapshotStateMap<Int, List<Plant>>,
    loaded: Boolean,
    onBack: () -> Unit
) {
    val scroll = rememberScrollState()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Results") },
            navigationIcon = {
                IconButton(onClick = {onBack}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!loaded) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading plants...", fontSize = 16.sp)
                }
            } else Column(modifier = Modifier.verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!plants.isEmpty()) {
                    plants.forEach { (key, value) ->
                        value.forEach { plant ->
                            PlantCardDisplayer(key, plant)
                        }
                    }
                } else {
                    Text("Ooops!")
                    Text("Tidak dapat menemukan jenis tanaman dengan tanahmu")
                }
            }
        }
    }
}
