package com.tbear9.openfarm.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tbear9.openfarm.activities.Camera.Companion.loaded
import com.tbear9.openfarm.activities.Camera.Companion.plants
import com.trbear9.plants.api.blob.Plant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    plants: SnapshotStateMap<Int, List<Plant>>,
    loaded: Boolean,
    onBack: () -> Unit,
    nav: NavController
) {
    Scaffold(
        topBar = {
            NavigationBar(modifier = Modifier.height(60.dp)) {
                Column(){

                }
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "OpenFarm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                }
            }
        },
        bottomBar = {
        var selected by remember { mutableIntStateOf(1) }
        NavigationBar {
            NavigationBarItem(
                selected = selected == 0,
                onClick = {
                    selected = 0
                    onBack()
                    nav.navigate("home")
                          },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = selected == 1,
                onClick = {
                    selected = 1
                    nav.navigate("result")
                          },
                icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                label = { Text("Tanaman") }
            )
            NavigationBarItem(
                selected = selected == 2,
                onClick = {
                    selected = 2
                    nav.navigate("tanah")
                          },
                icon = { Icon(Icons.Default.Grain, contentDescription = "Tanah") },
                label = { Text("Tanah") }
            )
        }
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
            } else if (!plants.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(plants.toList().asReversed()) {  (score, plant) ->
                        plant.forEach {
                            PlantCardDisplayer(score, it)
                        }
                    }
                }
            } else {
                Text(
                    text = "Ooops!",
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
