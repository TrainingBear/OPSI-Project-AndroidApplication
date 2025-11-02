package com.trbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.openfarm.util.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Guide(nav: NavController? = null) {
    val scroll: ScrollState = rememberScrollState()
    val context: Context = LocalContext.current
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4D4D79),
                                Color(0xFF466E5E),
                                Color(0xFF5D8A8A)  // ulang atau variasi
                            ), end = Offset.Zero, start = Offset.Infinite
                        )
                    )
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .wrapContentHeight(),
//                        .border(width = 3.25.dp, color = Color(0x60000000)),
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(8.dp))
//                                .background(Color(0x80EAEAEA))
                                .size(40.dp),
                            onClick = { nav?.navigate(Screen.home) },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back arrow",
                                tint = Color(0xFFFFFFFF),
                                modifier = Modifier
                                    .size(40.dp)
//                                    .padding(5.dp)
                            )
                        }
                    },
                    title = {
                        var query: String by remember { mutableStateOf("") }
                        TextField(
                            value = query,
                            placeholder = {
                                Text(
                                    text = "Temukan...",
                                    textAlign = TextAlign.Start,
                                    overflow = TextOverflow.Visible,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(y = (-10).dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Ascii
                            ),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 18.dp, top = 15.dp, bottom = 15.dp)
                                .clip(RoundedCornerShape(40.dp)),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0x92FFFFFF),
                                focusedContainerColor = Color(0xFFF8F8F8)
                            ),
                            onValueChange = { input: String ->
                                query = input
                                //TODO LATER
                            }
                        )
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF9C9CBB),
                            Color(0xFF88B688),
                            Color(0xFF85BEBE)  // ulang atau variasi
                        )
                    )
                )
//                .border(width = 2.5.dp, color = Color(0x60000000))
        ) {
            Box(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x7AFFFFFF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 120.dp)
                ) {
                    Text(
                        text = "Track your progress",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 35.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp, 20.dp, 0.dp, 37.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(160.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF7BD6FF))
                        ) {
                            LinearProgressIndicator(
                                progress = {
                                    9f / 11f //TODO lu backend ini
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .matchParentSize(), // isi penuh tinggi kotak
                                color = Color(0xFF2196F3),
                                trackColor = Color.Transparent,
                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            )
                        }
                        Text(
                            text = "9/11", //TODO ubah2 tar
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Image,
                    tint = Color.Black,
                    contentDescription = "png",
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterEnd)
                        .padding(
                            end
                            = 25.dp
                        )
                )
            }
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxSize()
                    .padding(start = 18.dp, end = 18.dp, top = 210.dp, bottom = 18.dp)
                    .height(180.dp)
                    .background(Color(0x56D3D3D3))
            ) {

            }
        }
    }
}