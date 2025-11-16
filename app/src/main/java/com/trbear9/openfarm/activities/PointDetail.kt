package com.trbear9.openfarm.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.openfarm.util.DataStore
import com.trbear9.openfarm.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PointDetail(
    nav: NavController? = null,
    title: String = "LOREM IPSUM DOLOR",
    subtitle: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vehicular, mauris ut faucets tincidunt",
    id: String = " ",
    credits: List<String>? = listOf("play.jasperproject.com", "www.google.com"),
    details: List<Triple<Pair<Painter, String?>?, String, String>> = listOf(
        Triple(
            Pair(rememberVectorPainter(Icons.Default.Image), "Somewhere"),
            "lorem ipsum sit amet test",
            """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vehicula, mauris ut faucibus tincidunt
                |
                |A fawtawtiauawhawkja iaefrhgofpkl aofaef  eaofjiae awop awi as a as huihusurahr aw.
            """.trimMargin()
        )
    )
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFBDB5A4),
                        Color(0xFFB5A4C4),
                        Color(0xFF99B6AC)
                    ), end = Offset.Zero, start = Offset.Infinite
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
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
                            onClick = { nav?.navigateUp() },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "back arrow",
                                tint = Color.Black,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(Color(0x8BFFFFFF))
//                                    .padding(5.dp)
                            )
                        }
                    },
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
//                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.ExtraBold,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(start = 20.dp, end = 20.dp)
//                    .background(Color(0x23FFFFFF))
            ) {
                item {
                    Text(
                        text = subtitle,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                }
                for (perInfo in details) {
                    item { Card(perInfo) }
                }
                item {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxSize()
//                            .background(Color(0x41FFFFFF))
                            .padding(8.5.dp)
                    ) {
                        if (credits != null) {
                            Text(
                                text = "Credits for source of informations",
                                fontWeight = FontWeight.Bold
                            )
                            for (a: String in credits)
                                Text(text = "-\u00A0${a}")
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 45.dp)
                                .shadow(10.dp, RoundedCornerShape(20.dp), clip = true)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xBE16B613))
                                .clickable {
                                    DataStore.setBoolean(id, true)
                                    nav?.navigate(Screen.help)
                                }
                        ) {
                            Text(
                                text =
                                    if (DataStore.contains(id)) "Kembali"
                                    else "Selesaikan",
                                color = Color(0xFFF0F3D0),
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Card(perInfo: Triple<Pair<Painter, String?>?, String, String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0x41FFFFFF))
            .padding(bottom = 10.dp)
    ) {
        if (perInfo.first != null)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        color = Color(0x4D000000)
                    )
            ) {
                Image(
                    painter = perInfo.first!!.first,
                    contentDescription = "Image per point",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()

                )
                if (perInfo.first!!.second != null)
                    Text(
                        text = "sc: " + perInfo.first!!.second!!,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(end = 5.dp)
                    )
            }
        Text(
            text = perInfo.second,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        Text(
            text = perInfo.third,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
//            textAlign = TextAlign.Justify,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.5.dp)
        )
    }
}