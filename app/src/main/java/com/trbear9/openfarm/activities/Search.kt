package com.trbear9.openfarm.activities

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.internal.Data
import com.trbear9.openfarm.Util
import com.trbear9.openfarm.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SearchLayout(nav: NavController? = null, from: String = "home", searchResult: SearchResult = SearchResult()) {
    val scroll = rememberScrollState()
    val catscroll = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val cat = remember { mutableStateSetOf<String>("All") }
    var query by remember { mutableStateOf("") }
    var focusState by remember { mutableStateOf(false) }
    var completer = remember { mutableStateSetOf<String>() }

    LaunchedEffect(query) {
        completer.clear()
        if(query.isNotEmpty()) {
            Data.searchByCommonName(10, query = query) {
                completer.add(it)
            }
        }
    }

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
                                ,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        searchResult.plantByCategory = mutableStateMapOf()
                                        if (query.isNotEmpty()) {
                                            searchResult.plants = Data.plantByTag[query]
                                        }
                                        cat.forEach { cat ->
                                            val result = mutableSetOf<String>()
                                            Data.plantByTag[query]?.forEach {
                                                if (Data.plant[it]?.category?.contains(cat) == true) {
                                                    result.add(it)
                                                }
                                            }
                                            searchResult.plantByCategory!![cat.toString()] = result
                                        }
                                        nav?.navigate(Screen.searchResult)
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
                                            if (query.isEmpty()) {
                                                Text(
                                                    "Masukan nama tanaman",
                                                    color = Color.Gray,
                                                    fontSize = size,
                                                    modifier = Modifier
                                                        .padding(start = 10.dp)
                                                )
                                            }
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
                                    modifier = Modifier.width(150.dp)
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(400.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .verticalScroll(catscroll)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(
                                                    checked = cat.contains("All"),
                                                    onCheckedChange = {
                                                        if (it) cat += "All"
                                                        else cat -= "All"
                                                    },
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .padding(start = 10.dp)
                                                )
                                                Text(
                                                    text = "All",
                                                    fontWeight = FontWeight.Light,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier.padding(start = 10.dp)
                                                )
                                            }
                                            Util.getCategory().forEach { kat ->
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(
                                                        checked = cat.contains(kat),
                                                        onCheckedChange = {
                                                            if (it) cat += kat
                                                            else cat -= kat
                                                        },
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .padding(start = 10.dp)
                                                    )
                                                    Text(
                                                        text = kat,
                                                        fontWeight = FontWeight.Light,
                                                        fontSize = 16.sp,
                                                        modifier = Modifier.padding(start = 10.dp)
                                                    )
                                                }
                                            }
                                        }
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
    ) {
        Column(Modifier
            .fillMaxSize()
            .padding(it)
            .verticalScroll(scroll),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Coming soon!",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
            DropdownMenu(expanded = true, onDismissRequest = {}) {
                completer.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            completer.clear()
                            query = it
                        }
                    )
                }
            }
        }
    }
}