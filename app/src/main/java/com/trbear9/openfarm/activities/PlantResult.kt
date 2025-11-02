package com.trbear9.openfarm.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.trbear9.internal.Data
import com.trbear9.openfarm.ResultPagingSource
import com.trbear9.openfarm.Util
import com.trbear9.openfarm.debug
import com.trbear9.openfarm.info
import com.trbear9.openfarm.inputs
import com.trbear9.openfarm.util.Screen
import com.trbear9.plants.api.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion

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
@Preview()
fun SoilResultScreen(
    onBack: () -> Unit = {},
    nav: NavController? = null,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var query by remember { mutableStateOf("") }
    var finalQuery by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }
    var expandedSearch by remember { mutableStateOf(false) }
    var expandCat by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    var order by remember { mutableStateOf("Tertinggi") }
    var scroll = rememberScrollState()
    var completerScroll = rememberScrollState()

    var loaded by remember {
        mutableStateOf<Boolean>(
            if(inputs.soilResult == null) true
            else inputs.soilResult.response?.loaded ?: false)
    }
    var collected by remember { mutableStateOf<Boolean>(inputs.soilResult.collected ?: true) }
    var current by remember { mutableStateOf<String>(inputs.soilResult?.response?.current ?: "Tanduran") }
    var progress by remember { mutableIntStateOf(inputs.soilResult?.response?.progress?.toInt() ?: 0) }
    var target by remember { mutableIntStateOf(inputs.soilResult?.response?.target?.toInt() ?: 0) }
    var predicted by remember { mutableStateOf<Boolean>(inputs.soilResult?.response?.predicted == true) }
    var parameterLoaded by remember { mutableStateOf<Boolean>(inputs.soilResult?.response?.parameterLoaded == true) }

    var search = remember{ mutableStateSetOf<String>()}
    var focusRequester = remember{ FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        collected = inputs.soilResult?.collected ?: true
        Util.debug("It is already collected? $collected")
        if (inputs.soilResult != null && !collected) {
            Util.debug("Collecting")
            inputs.soilResult.res?.onCompletion {
                inputs.soilResult.collected = true
                inputs.soilResult.res = null
                Util.debug("Collected")
            }?.collect {
                loaded = it.loaded
                current = it.current
                progress = it.progress
                target = it.target
                predicted = it.predicted
                parameterLoaded = it.parameterLoaded
                inputs.soilResult.response = it
            }
        }
    }
    var completer = remember { mutableStateSetOf<String>() }
    LaunchedEffect(query) {
        completer.clear()
        completer(query, completer, 5)
        "updated ${completer.size} items".debug("PlantResult")
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
                        Box(
                            modifier = (if(expandedSearch) Modifier.fillMaxWidth() else Modifier.weight(1f))
                        ) {
                            BasicTextField (
                                value = query,
                                onValueChange = { input ->
                                    query = input
                                    search.clear()
                                    expandedSearch = true
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        expandedSearch = false
                                        keyboard?.hide()
                                        hasFocus = false
                                        finalQuery = query
                                        "ExpandedSearch: $expandedSearch".debug("PlantResult")
                                    },
                                ),
                                modifier = Modifier
                                    .height(35.dp)
                                    .padding(end = 12.dp)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { focusState ->
                                        hasFocus = focusState.hasFocus
                                    }
                                ,
                                decorationBox = { innerTextField ->
                                    BoxWithConstraints(
                                        Modifier
                                            .height(35.dp)
                                            .fillMaxWidth()
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
                                                    if(expandedSearch && query.isNotEmpty()) Row(horizontalArrangement = Arrangement.End,
                                                        modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                        IconButton(
                                                            onClick = { query = "" }
                                                        ) {
                                                            Icon(
                                                                Icons.Default.Close,
                                                                "Clear",
                                                                tint = Color.Gray
                                                            )
                                                        }
                                                    }
                                                    innerTextField()
                                                }
                                        }
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))
                        Row(
                            Modifier.clickable { expandCat = !expandCat },
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
                                        expandedSearch = false
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Terendah") },
                                    onClick = {
                                        order = "Terendah"
                                        expandCat = false
                                        expandedSearch = false
                                    },
                                )
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
                                            expandedSearch = false
//                                            query = ""
                                        }
                                    )
                                    Util.getCategory().forEach {
                                        DropdownMenuItem(
                                            text = { Text(it) },
                                            onClick = {
                                                expanded = false
                                                selected = it
                                                expandedSearch = false
//                                                query = ""
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
            if(true) { //before: if searchresult == null
                var selected by remember { mutableIntStateOf(1) }
                NavigationBar {
                    NavigationBarItem(
                        selected = selected == 0,
                        onClick = {
                            selected = 0
                            onBack()
                            nav?.navigate(Screen.home)
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
                            nav?.navigate(Screen.soilStats)
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
            if (!loaded) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (!predicted) "Menganalisa tanahmu.."
                        else if (!parameterLoaded) "Mencari rata-rata suhu di daerah mu"
                        else "Mencari data $current",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else if (inputs.soilResult.plants?.isNotEmpty() == true) {
                val pagerFlow =
                    if (finalQuery.isEmpty())
                        remember(selected, order) {
                            Pager(PagingConfig(pageSize = 7)) {
                                ResultPagingSource(
                                    items = inputs.soilResult.plantByCategory?.get(selected)
                                        ?.flatten(order) ?: mutableListOf<Pair<Int, String>>()
                                )
                            }.flow
                        }
                    else
                        remember(finalQuery, order, selected) {
                            Pager(PagingConfig(pageSize = 7)) {
                                ResultPagingSource(items = search(query, order, selected))
                            }.flow
                        }
                val plants: LazyPagingItems<Pair<Int, String>> =
                    pagerFlow.collectAsLazyPagingItems()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    if (plants.itemCount == 0)
//                        item {
//                            Column {
//                                Text(
//                                    text = "Tidak dapat menemukan jenis tanaman dengan tanahmu",
//                                    textAlign = TextAlign.Center,
//                                    fontSize = 28.sp,
//                                    modifier = Modifier.padding(top = 100.dp)
//                                )
//                            }
//                        }
//                    else
                    items(plants.itemCount) { i ->
                        PlantCardDisplayer(plants[i]!!.first, Data.plant[plants[i]!!.second])
                        "Displaying ${Data.plant[plants[i]!!.second]?.commonName}".info("SoilResultScreen")
                    }

                    plants.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item { Text("Loading...") }
                            }

                            loadState.append is LoadState.Loading -> {
                                item { Text("Loading more...") }
                            }

                            loadState.append is LoadState.Error -> {
                                item { Text("Error loading more.") }
                            }
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
                    Button(
                        onClick = {
                            nav?.navigate(Screen.camera)
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
        "ExpandedSearch: $expandedSearch".debug("PlantResult")
        if (expandedSearch) {
            Box(
                modifier = Modifier
                    .fillMaxSize().padding(padding)
                    .background(Color.DarkGray.copy(alpha = 0.8f))
                    .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 10.dp),
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(completerScroll),
                        horizontalAlignment = Alignment.Start
                    ) {
                        completer.forEach {
                            Text(
                                text = it,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    query = it
                                }.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
fun SnapshotStateMap<Int, MutableSet<String>>.flatten(order: String): MutableList<Pair<Int, String>> {
    val reversedList = if(order == "Tertinggi"){
        this.toList().asReversed()
    } else this.toList()
    val result = mutableListOf<Pair<Int, String>>()
    reversedList.forEach {
        it.second.forEach { plant ->
            result.add(it.first to plant)
        }
    }
    return result
}

fun completer(query: String, holder: SnapshotStateSet<String>, limit: Int = Int.MAX_VALUE){
    inputs.soilResult.plants?:return
    var i = 0
    outer@ for (it in inputs.soilResult.plants ?: return) {
        outer2@ for (plant in it.value) {
            val commonName = Data.namaIlmiahToNamaUmum[plant.lowercase()]
            val prefix = query.lowercase()
            if (commonName != null && (commonName.startsWith(prefix) ||
                        commonName.endsWith(prefix) || commonName.contains(prefix))) {
                holder.add(commonName)
                if (i++ > limit) {
                    break@outer
                    break@outer2
                }
            }
        }
    }
}

fun search(query: String, order: String, category: String): MutableList<Pair<Int, String>> {
    val plants = (inputs.soilResult.plantByCategory?.get(category) ?: return mutableListOf())
            .flatten(order)
    val result = mutableListOf<Pair<Int, String>>()
    for (plant in plants) {
        val name = Data.namaIlmiahToNamaUmum[plant.second.lowercase()]
        val prefix = query.lowercase()
        if (name != null && (name.startsWith(prefix) ||
                    name.endsWith(prefix) || name.contains(prefix))) {
            result.add(plant.first to plant.second)
        }
    }
    return result
}