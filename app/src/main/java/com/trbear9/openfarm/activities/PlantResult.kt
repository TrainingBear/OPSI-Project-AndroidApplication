package com.trbear9.openfarm.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.openfarm.MA
import com.trbear9.openfarm.Util
import com.trbear9.plants.api.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ResultScreen(
    res: Flow<Response>? = null,
    onBack: () -> Unit = {},
    nav: NavController? = null
) {
    var querry by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }
    var expandCat by remember { mutableStateOf(false) }
    var order by remember { mutableStateOf("Tertinggi") }
    var response by remember { mutableStateOf(Response()) }
    var scroll = rememberScrollState()

    var loaded by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf("Tanduran") }
    var progress by remember { mutableStateOf(0) }
    var target by remember { mutableStateOf(0) }
    var predicted by remember { mutableStateOf(false) }
    var parameterLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        res?.collect {
            loaded = it.loaded
            current = it.current
            progress = it.progress
            target = it.target
            predicted = it.predicted
            parameterLoaded = it.parameterLoaded
        }
        Util.debug("Is everything loaded? : ${response.loaded}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray),
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = querry,
                            onValueChange = { input ->
                                querry = input
                            },
                            label = { Text("Cari tanaman") },
                            placeholder = { Text("Kangkung") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier.wrapContentSize()
                                .clickable { expandCat = !expandCat }
                        ) {
                            Text(
                                text = order,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            DropdownMenu(
                                expanded = expandCat,
                                onDismissRequest = { expandCat = false },
                                modifier = Modifier.width(150.dp)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tertinggi") },
                                    onClick = {
                                        order = "Tertinggi"
                                        expandCat = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Terendah") },
                                    onClick = {
                                        order = "Terendah"
                                        expandCat = false
                                    },
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Order",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier.wrapContentSize()
                                .clickable { expanded = !expanded },
                        ) {
                            Row {
                                Text(
                                    text = selected.take(10) + if (selected.length > 10) ".." else "",
                                    fontSize = 20.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Filter",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                        Box {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.height(300.dp),
                                scrollState = scroll
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All") },
                                    onClick = {
                                        expanded = false
                                        selected = "All"
                                    }
                                )
                                Util.getCategory().forEach {
                                    DropdownMenuItem(
                                        text = { Text(it) },
                                        onClick = {
                                            expanded = false
                                            selected = it
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            var selected by remember { mutableIntStateOf(1) }
            NavigationBar {
                NavigationBarItem(
                    selected = selected == 0,
                    onClick = {
                        selected = 0
                        onBack()
                        nav?.navigate("home")
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selected == 1,
                    onClick = {
                        selected = 1
                        nav?.navigate("result")
                    },
                    icon = { Icon(Icons.Default.Park, contentDescription = "Hasil") },
                    label = { Text("Tanaman") }
                )
                NavigationBarItem(
                    selected = selected == 2,
                    onClick = {
                        selected = 2
                        nav?.navigate("tanah")
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
                    Text(
                        text = if (predicted) "Menganalisa tanahmu.."
                        else if (!parameterLoaded) "Mencari rata-rata suhu di daerah mu"
                        else "Mencari data tanaman $current (${progress}/${target})",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else if (MA.plants.isEmpty() == false) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selected == "All") items(
                        if (order == "Tertinggi") MA.plants.toList().asReversed()
                        else MA.plants.toList()
                    ) { (score, plant) ->
                        plant.forEach {
                            PlantCardDisplayer(score, it)
                        }
                    }
                    else if (MA.plantByCategory==null || MA.plantByCategory[selected].isNullOrEmpty())
                        item {
                            Column {
                                Text(
                                    text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                                    textAlign = TextAlign.Center,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(top = 100.dp)
                                )
                            }
                        }
                    else items(
                        if (order == "Tertinggi") MA.plantByCategory[selected]!!.toList().asReversed()
                        else MA.plantByCategory[selected]!!.toList()
                    ) { (score, plant) ->
                        plant.forEach {
                            PlantCardDisplayer(score, it)
                        }
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Ooops!",
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Filter(){
    var number by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.height(400.dp)
            .width(300.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = number,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || (it == ',' || it == '.') }) {
                        number = input
                    }
                },
                label = { Text("Query") },
                placeholder = { Text("Kangkung") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(20.dp)
                    .padding(horizontal = 32.dp)
                    .clickable { expanded = !expanded }
                    .background(Color.White)
            ) {
                Text(
                    text = "Category",
                    modifier = Modifier.align(Alignment.Center)
                        .fillMaxSize(),
                )
            }
            DropdownMenu(
                expanded = true,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = { expanded = false },
                )
                DropdownMenuItem(
                    text = { Text("Tanaman Sayur") },
                    onClick = { expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Tanaman Buah") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}
