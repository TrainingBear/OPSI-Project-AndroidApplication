package com.trbear9.openfarm.activities

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.internal.Data
import com.trbear9.openfarm.MA
import com.trbear9.openfarm.Util
import com.trbear9.plants.api.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class SoilResult {
    var plants: SnapshotStateMap<Int, MutableSet<String>>? = null
    var plantByCategory: SnapshotStateMap<String, SnapshotStateMap<Int, MutableSet<String>>>? = null
    var res: Flow<Response>? = null
    var collected: Boolean = false
    var response: Response? = null
}

class SearchResult {
    var plants: MutableSet<String>? = null
    var plantByCategory: SnapshotStateMap<String, MutableSet<String>>? = null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ResultScreen(
    soilResult: SoilResult? = null,
    searchResult: SearchResult? = null,
    onBack: () -> Unit = {},
    nav: NavController? = null
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }
    var expandedSearch by remember { mutableStateOf(false) }
    var expandCat by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    var order by remember { mutableStateOf("Tertinggi") }
    var scroll = rememberScrollState()

    var loaded by remember {
        mutableStateOf<Boolean>(
            if(soilResult == null) true
            else soilResult.response?.loaded ?: false)
    }
    var collected by remember { mutableStateOf<Boolean>(MA.soilResult.collected ?: true) }
    var current by remember { mutableStateOf<String>(soilResult?.response?.current ?: "Tanduran") }
    var progress by remember { mutableIntStateOf(soilResult?.response?.progress?.toInt() ?: 0) }
    var target by remember { mutableIntStateOf(soilResult?.response?.target?.toInt() ?: 0) }
    var predicted by remember { mutableStateOf<Boolean>(soilResult?.response?.predicted == true) }
    var parameterLoaded by remember { mutableStateOf<Boolean>(soilResult?.response?.parameterLoaded == true) }

    var search = remember{ mutableStateSetOf<String>()}
    var focusRequester = remember{ FocusRequester() }
    var empty = remember{mutableStateMapOf<Int, MutableSet<String>>()}

    LaunchedEffect(Unit) {
        collected = soilResult?.collected ?: true
        Util.debug("It is already collected? $collected")
        if (soilResult != null && !collected) {
            Util.debug("Collecting")
            soilResult.res?.onCompletion {
                soilResult.collected = true
                soilResult.res = null
                Util.debug("Collected")
            }?.collect {
                loaded = it.loaded
                current = it.current
                progress = it.progress
                target = it.target
                predicted = it.predicted
                parameterLoaded = it.parameterLoaded
                soilResult.response = it
            }
        }
    }
    var completer = remember { mutableStateSetOf<String>() }
    LaunchedEffect(query) {
        completer.clear()
        var i = 0
        if (query.isNotEmpty()) {
            outer@ for (it in soilResult?.plants ?: empty) {
                iter@ for (plant in it.value) {
                    val commonName = Data.namaIlmiahToNamaUmum[plant]
                    val prefix = query.lowercase()
                    if (commonName != null && (commonName.startsWith(prefix)
                                || plant.contains(prefix))) {
                        completer.add(plant)
                    }
                    i++
                    if (i >= 5) {
                        break@outer
                        break@iter
                    }
                }
            }
        }
    }
    var cat = remember(selected, soilResult) { soilResult?.plantByCategory?.get(selected) }
    val plantCat by produceState<List<Pair<Int, MutableSet<String>>>?>(initialValue = null, cat, order) {
        if (cat != null) {
            value = withContext(Dispatchers.Default) {
                if (order == "Tertinggi") cat.toList().asReversed()
                else cat.toList()
            }
        } else value = emptyList()
    }

     Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5)),
                title = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(soilResult != null) {
                            DropdownMenu(
                                expanded = expandedSearch,
                                onDismissRequest = { expandedSearch = false },
                            ) {
                                completer.forEach {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = it,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Light
                                            )
                                        },
                                        onClick = { query = it }
                                    )
                                }
                            }
                            BasicTextField(
                                value = query,
                                onValueChange = {input ->
                                    query = input
                                    search.clear()
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        search.clear()
                                        if(query.isNotEmpty()) Data.search(query = query){
                                            search.add(it)
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(35.dp)
                                    .padding(end = 12.dp)
                                    .align(Alignment.CenterVertically)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { focusState ->
                                        Log.d("Focus", "Focused: ${focusState.isFocused}")
                                        expandedSearch = focusState.isFocused
                                    },
                                decorationBox = { innerTextField ->
                                    BoxWithConstraints(
                                        Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        val size = (maxHeight.value / 2).sp
                                        Column(verticalArrangement = Arrangement.Center) {
                                            if (query.isEmpty() && !hasFocus) {
                                                Text(
                                                    "Masukan nama tanaman",
                                                    color = Color.Gray,
                                                    fontSize = size,
                                                    modifier = Modifier
                                                        .padding(start = 10.dp)
                                                )
                                            } else
                                                Box(
                                                    Modifier
                                                        .wrapContentSize()
                                                        .padding(start = 10.dp)
                                                ) {
                                                    innerTextField()
                                                }
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Row(Modifier.clickable { expandCat = !expandCat },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = order,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Order",
                                    tint = Color.Yellow,
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                                DropdownMenu(
                                    expanded = expandCat,
                                    onDismissRequest = { expandCat = false },
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
                        }
                        Row(
                            modifier = Modifier
                                .clickable { expanded = !expanded },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = selected.take(10) + if (selected.length > 10) ".." else "",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Filter",
                                modifier = Modifier.size(30.dp),
                                tint = Color.Black
                            )
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
                                            query = ""
                                        }
                                    )
                                    Util.getCategory().forEach {
                                        DropdownMenuItem(
                                            text = { Text(it) },
                                            onClick = {
                                                expanded = false
                                                selected = it
                                                query = ""
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if(searchResult == null) {
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
            }
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!loaded || plantCat == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (!predicted) "Menganalisa tanahmu.."
                        else if (!parameterLoaded) "Mencari rata-rata suhu di daerah mu"
                        else if (plantCat == null) "Loading.. "
                        else "Mencari data $current",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else if (soilResult != null && soilResult.plants?.isEmpty() == false) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val map = soilResult.plantByCategory
                    if(search.isNotEmpty()){
                        items(search.toList()){ q ->
                            Data.plantByTag[q]?.forEach {
                                PlantCardDisplayer(0, Data.plant[it])
                            }
                        }
                    }
                    else if (map == null || map[selected].isNullOrEmpty())
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
                    else {
                        items(
                            items = plantCat!!,
                            key = { (_, plants) -> plants.hashCode() },
                        ) { (score, plants) ->
                            plants.forEach { plant ->
                                PlantCardDisplayer(score, Data.plant[plant])
                            }
                        }

                    }
                }
            }
            else if(false && (searchResult != null && searchResult.plants?.isEmpty() == false)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val plants = searchResult?.plants
                    val plantByCategory = searchResult?.plantByCategory
                    if (selected == "All" && plants != null) {
                        items(plants.toList()) { plant ->
                            plant.forEach {
                                PlantCardDisplayer(ref = Data.plant[plant])
                            }
                        }
                    } else {
                        if (plantByCategory == null || plantByCategory.isEmpty()) {
                            item {
                                Column(verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
                                        textAlign = TextAlign.Center,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(20.dp)
                                    )
                                }
                            }
                        } else {
                            val map = plantByCategory[selected]
                            if (map != null) items(map.toList()) {
                                PlantCardDisplayer(ref = Data.plant[it])
                            }
                        }
                    }
                }
            }
            else {
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
                    Button(
                        onClick = {
                            nav?.navigate("camera")
                        },
                        modifier = Modifier
                            .padding(top = 20.dp)
                    ) {
                        Text(
                            text = "Scan tanahmu!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }
    }
}