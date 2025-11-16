package com.trbear9.openfarm.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pseudoankit.coachmark.UnifyCoachmark
import com.trbear9.internal.Data
import com.trbear9.openfarm.Util
import com.trbear9.openfarm.debug
import com.trbear9.openfarm.error
import com.trbear9.openfarm.inputs
import kotlinx.coroutines.delay
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SearchLayout(nav: NavController? = null, from: String = "home") {
    val scroll = rememberScrollState()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable{ mutableStateOf("All") }
    var query by rememberSaveable{ mutableStateOf("") }
    var finalQuery by rememberSaveable{ mutableStateOf("") }
    var completer = rememberSaveable{ mutableListOf<String>() }
    val focusRequester = remember{ FocusRequester() }

    var focus by rememberSaveable{mutableStateOf(false)}
    val keyboard = LocalSoftwareKeyboardController.current

    fun search(){
        if(query.isEmpty()) return
        inputs.searchResult.plantByCategory = mutableStateMapOf()
        inputs.searchResult.plants = mutableSetOf()
        Data.searchByScienceName(9999, query = query){
            inputs.searchResult.plants!!.add(it)
            Data.plant[it]?.category?.forEach { cat ->
                inputs.searchResult.plantByCategory?.computeIfAbsent(Util.translateCategory(cat)) {
                    mutableSetOf()
                }?.add(Data.normalize[it]?:"null")
            }
            inputs.searchResult.plantByCategory?.computeIfAbsent("All") {
                mutableSetOf()
            }?.add(Data.normalize[it]?:"null")
//            "Found $it".debug("PlantSearch")
        }
        Data.searchByCommonName(9999, query = query){
            val name = Data.namaUmumToNamaIlmiah[it]!!
            inputs.searchResult.plants!!.add(name)
            Data.plant[it]?.category?.forEach { cat ->
                inputs.searchResult.plantByCategory?.computeIfAbsent(Util.translateCategory(cat)) {
                    mutableSetOf()
                }?.add(Data.normalize[name]?:"null")
            }
            inputs.searchResult.plantByCategory?.computeIfAbsent("All") {
                mutableSetOf()
            }?.add(Data.normalize[name]?:"null")
//            "Found $name".debug("PlantSearch")
        }
        Util.getCategory().forEach {
            "$it loaded ${inputs.searchResult.plantByCategory!!.get(it)?.size} items".debug("PlantSearch")
        }
        keyboard?.hide()
        finalQuery = query
        focus = false
    }

    LaunchedEffect(Unit){
//        runBlocking {
//            delay(900)
//            focusRequester.requestFocus()
//        }
    }

    LaunchedEffect(query) {
        completer.clear()
        focus = true
        if(query.isNotEmpty()) {
            Data.searchByCommonName(9999, query = query) {
                completer.add(it)
            }
            Data.searchByScienceName(9999, query = query) {
                completer.add(it)
            }
        }
    }
    val source = CompleterSearchSource(completer)
    val pagerFlow = remember(query) {
        Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)) {
            source
        }.flow
    }
    val items = pagerFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .weight(4f)
                        ) {
                            BasicTextField(
                                value = query,
                                onValueChange = { input ->
                                    if (input.all { !it.isDigit() }) {
                                        query = input
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(35.dp)
                                    .align(Alignment.Center)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged {
                                        focus = it.isFocused
                                    }
                                ,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        search()
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    BoxWithConstraints(
                                        Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        val size = (maxHeight.value / 2).sp
                                        Column(verticalArrangement = Arrangement.Center) {
                                            if (query.isEmpty() && !focus) {
                                                Text(
                                                    "Masukan nama tanaman",
                                                    color = Color.Gray,
                                                    fontSize = size,
                                                    modifier = Modifier
                                                        .padding(start = 10.dp)
                                                )
                                            } else Box(
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
                        }
                        Box(Modifier.fillMaxHeight().weight(1f)) {
                            Column(verticalArrangement = Arrangement.Center) {
                                IconButton(
                                    onClick = { expanded = !expanded },
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Back",
                                        modifier = Modifier.fillMaxSize(0.5f)
                                    )
                                }
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
                                            focus = false
//                                            query = ""
                                        }
                                    )
                                    Util.getCategory().forEach {
                                        DropdownMenuItem(
                                            text = { Text(it) },
                                            onClick = {
                                                expanded = false
                                                selected = it
                                                focus = false
//                                                query = ""
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav?.navigate(from)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        val pagerFlow =
            if (finalQuery.isEmpty())
                remember(selected) {
                    Pager(PagingConfig(pageSize = 7)) {
                        CompleterSearchSource(
                            Data.plantByTag[selected]?.toMutableList() ?: mutableListOf()
                        )
                    }.flow
                }
            else
                remember(finalQuery, selected) {
                    Pager(PagingConfig(pageSize = 7)) {
                        CompleterSearchSource(inputs.searchResult.plantByCategory!![selected]?.toMutableList()?:mutableListOf())
                    }.flow
                }
        val plants: LazyPagingItems<String> =
            pagerFlow.collectAsLazyPagingItems()

        UnifyCoachmark {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(plants.itemCount) { i ->
                    val plant = Data.plant[plants[i]!!]
                    if (plant != null) PlantCardDisplayer(0, plant)
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
        }
        if (focus && query.isNotEmpty()) {
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
                    LazyColumn(
                        horizontalAlignment = Alignment.Start
                    ) {
                        items(items.itemCount) { i ->
                            Text(
                                text = items[i]!!,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    query = items[i]!!
                                }.padding(start = 16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

class CompleterSearchSource(val completer: MutableList<String>) : PagingSource<Int, String>(){
    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        val page = params.key ?: 0
        val pageSize = 20

        return try {
            delay(500)
            val nextKey = if (page.toDouble() >= (completer.size/pageSize)) null else page + 1
            val item = completer.subList(page * pageSize,
                min((page + 1) * pageSize, completer.size)
            ).toList()
            LoadResult.Page(
                data = item,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            "Error when loading page $page: ${e.message}".error("Search")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}