package com.trbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.openfarm.R
import com.trbear9.openfarm.util.DataStore
import com.trbear9.openfarm.util.Screen

object Guide {
    /** MUST BE NULLED AFTER USE */
    internal var guidePointer: // Data/Storage, (Title, Subtitle, Credits..?), ((Image, credit?)?, Info, infos...)..?
            Triple<String, Triple<String, String, List<String>?>, List<Triple<Pair<Painter, String?>?, String, String>>>? =
        null
}

/**
 * Guide/Tutorial progress list missions
 * */
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:width=411dp,height=891dp,dpi=160")
@Composable
fun Guide(nav: NavController? = null) {
//    val context = LocalContext.current
//    val pref = context.getSharedPreferences("learning_progress", Context.MODE_PRIVATE)
//    val achieved = (pref.getStringSet("completed", null) ?: emptySet()).size
    var query: String by remember { mutableStateOf("") }
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
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
//                                    .padding(5.dp)
                            )
                        }
                    },
                    title = {
                        TextField(
                            value = query,
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            placeholder = {
                                Text(
                                    text = "Temukan...",
                                    textAlign = TextAlign.Start,
                                    overflow = TextOverflow.Visible,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    modifier = Modifier
//                                        .fillMaxSize()
                                        .offset(y = (-8).dp)
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
                                .height(56.dp) // ✅ tambahkan tinggi minimum (umum untuk TextField Material3)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(40.dp)),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0x92FFFFFF),
                                focusedContainerColor = Color(0xFFF8F8F8)
                            ),
                            onValueChange = { input: String ->
                                query = input
                            }
                        )
                    }
                )
            }
        }
    ) {
        Column(
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
        ) {
            BoxWithConstraints(
                Modifier
                    .padding(18.dp)
                    .fillMaxWidth()
                    .weight(2.4f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x8BFFFFFF))
            ) {
                val maxHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    BoxWithConstraints(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        val fontSize = (maxHeight.value / 5f).sp
                        Text(
                            text = "Track your progress",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = fontSize,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = (maxHeight.value / 2).dp,
                                top = 20.dp
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.8f)
                            .padding(start = 20.dp, end = (maxHeight.value / 2).dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var achieved: Byte = 0
                        if(DataStore.contains("tanah")) //TODO jangan lupa diuncomment
                            achieved++
                        if(DataStore.contains("pupuk"))
                            achieved++
                        if(DataStore.contains("tanaman"))
                            achieved++
                        BoxWithConstraints(
                            modifier = Modifier
                                .height(maxHeight / 7)
                                .weight(.5f)
                                .padding(end = 7.5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF7BD6FF))
                        ) {
                            LinearProgressIndicator(
                                progress = {
                                    achieved / 3f
                                },
                                modifier = Modifier
                                    .fillMaxSize(),
                                color = Color(0xFF2196F3),
                                trackColor = Color.Transparent,
                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            )
                        }
                        BoxWithConstraints(Modifier.weight(.2f)) {
                            val fontSize = (maxHeight.value / 7f).sp
                            Text(
                                text = "$achieved/3",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = fontSize,
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Default.Image,
                    tint = Color.Black,
                    contentDescription = "png",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(maxHeight / 2)
//                            .align(Alignment.CenterEnd)
                )
            }
            Box(
                Modifier
                    .weight(6f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxSize()
                        .padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
                        .background(Color(0x56D3D3D3))
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x99AFAFAF))
                        ) {

                        }
                    }
                    item {
                        GuideCard(
                            nav, num = 1, id = "tanah",
                            title = "Tanah",
                            desc = "Apa itu tanah? Tanah liat? Tanah basah? Tanah kering?",
                            credits = listOf(
                                "https://en.wikipedia.org/wiki/Soil_pH",
                                "https://fnb.tech/id/what-is-soil-ph/",
                                "https://id.wikipedia.org/wiki/Tanah",
                                "https://agrotek.id/istilah/retensi-air/",
                                "https://en.wikipedia.org/wiki/Soil_water_(retention)"
                            ),
                            details = listOf(
                                Triple(
                                    null, //TODO isi tod klo mau
                                    "Pengertian",
                                    "Tanah sangat vital peranannya bagi semua kehidupan di bumi " +
                                            "karena tanah mendukung kehidupan tumbuhan dengan menyediakan unsur " +
                                            "hara dan air sekaligus sebagai penopang akar tanaman. Komposisi " +
                                            "tanah berbeda-beda pada satu lokasi dengan lokasi yang lain. " +
                                            "Air dan udara merupakan bagian dari tanah."
                                ),
                                Triple(
                                    Pair(painterResource(id = R.drawable.lapisanairtanah), null),
                                    "Retensi Air Tanah",
                                    "Retensi air adalah kemampuan tanah untuk menahan atau menyimpan " +
                                            "air di dalamnya setelah proses penyiraman atau hujan. Mengacu " +
                                            "pada kapasitas tanah untuk mempertahankan kelembaban dan ketersediaan " +
                                            "air bagi tanaman. Retensi air dalam tanah dipengaruhi oleh beberapa faktor, diantaranya:\n" +
                                            "- Tekstur tanah: Tanah berbutir halus seperti lempung memiliki " +
                                            "kemampuan yang lebih baik untuk menahan air dibandingkan dengan " +
                                            "tanah berbutir kasar seperti pasir. Partikel halus dalam " +
                                            "tanah lempung memiliki kapasitas adsorpsi (penyerapan air) yang tinggi.\n" +
                                            "- Struktur Tanah: Struktur tanah yang hancur atau terkompaksi " +
                                            "(padat) dapat menghambat infiltrasi air dan menyebabkan aliran " +
                                            "permukaan, sehingga mengurangi kemampuan tanah untuk menahan air.\n" +
                                            "- Kandungan Bahan Organik: Bahan organik meningkatkan kemampuan tanah " +
                                            "untuk menahan air dengan membentuk agregat dan meningkatkan retensi air di dalam pori-pori tanah.\n" +
                                            "- Kedalaman Tanah: Tanah yang dalam memiliki daya tampung yang " +
                                            "lebih besar untuk menahan atau menyimpan air dibandingkan dengan tanah yang dangkal."
                                ),
                                Triple(
                                    Pair(painterResource(id = R.drawable.diagramphtanah), null),
                                    "pH Tanah",
                                    "Tingkat pH tanah mengacu pada tingkat keasaman atau kebasaannya " +
                                            "tanah, jika tanah terlalu asam atau terlalu basa, hal itu " +
                                            "dapat membatasi kemampuan tanaman untuk menyerap nutrisi tertentu. " +
                                            "Pada tanah asam (pH rendah), nutrisi penting seperti nitrogen, " +
                                            "fosfor, dan kalium sering kali tidak tersedia bagi tanaman. " +
                                            "Di sisi lain, tanah basa (pH tinggi) dapat menyebabkan pengikatan " +
                                            "mikronutrien seperti zat besi, seng, dan mangan, sehingga " +
                                            "tanaman tidak dapat menyerapnya. Berikut adalah klasifikasi pH pada tanah:\n" +
                                            "Tanah Asam (pH 0-6.9): Umum terjadi di wilayah dengan curah " +
                                            "hujan tinggi dan tanah yang sudah tua. Kondisi ini dapat menyebabkan kekurangan nutrisi.\n" +
                                            "Tanah Netral (pH 7): Ideal untuk sebagian besar tanaman.\n" +
                                            "Tanah Alkali (pH 7.1-14): Sering ditemukan di daerah kering " +
                                            "dan gersang. Hal ini dapat menyebabkan kekurangan nutrisi bagi tanaman tertentu."
                                )
                            )
                        )
                    }
                    item {
                        GuideCard(
                            nav, num = 2, id = "tanaman",
                            title = "Tanaman",
                            desc = "",
                            details = listOf(
                                Triple(
                                    null,
                                    "Pengertian",
                                    ""
                                )
                            )
                        )
                    }
                    item {
                        GuideCard(
                            nav, num = 3, id = "pupuk",
                            title = "Pupuk",
                            desc = " ",
                            credits = listOf("https://id.wikipedia.org/wiki/Pupuk"),
                            details = listOf(
                                Triple(
                                    null,
                                    "Pengertian",
                                    "Pupuk juga penting bagi tanaman, seperti menyediakan dan " +
                                            "meningkatkan ketersediaan zat hara yang sangat dibutuhkan " +
                                            "tanamanmu sehingga tanaman dapat tumbuh dengan optimal. Akan " +
                                            "tetapi perlu juga diperhatikan apa kebutuhan tumbuhanmu, agar " +
                                            "tumbuhan tidak mendapat terlalu banyak zat makanan. Terlalu sedikit " +
                                            "atau terlalu banyak zat makanan dapat berbahaya bagi tumbuhan."
                                ),
                                Triple(
                                    null,
                                    "Pupuk NPK",
                                    "Pupuk NPK adalah pupuk buatan yang berbentuk cair atau padat " +
                                            "berupa butiran kasar yang mengandung unsur hara utama nitrogen, " +
                                            "fosfor, dan kalium. Berikut fungsi ketiga unsur dalam pupuk NPK:\n" +
                                            "- Nitrogen (N): membantu pertumbuhan vegetatif, terutama daun.\n" +
                                            "- Fosfor (P): membantu pertumbuhan akar dan tunas.\n" +
                                            "- Kalium (K): membantu pembungaan dan pembuahan."
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * util for guide/mission card
 * */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GuideCard(
    nav: NavController? = null,
    num: Int,
    title: String = "temp",
    desc: String = "lorem ipsum dolor sit amet",
    id: String,
    credits: List<String>? = null,
    details: List<Triple<Pair<Painter, String?>?, String, String>> = listOf(
        Triple(
            Pair(
                painterResource(id = R.drawable.background_opsi_mainactivity_rescaled),
                "Somewhere"
            ),
            "lorem ipsum sit amet test",
            """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vehicula, mauris ut faucibus tincidunt
                |
                |A fawtawtiauawhawkja iaefrhgofpkl aofaef  eaofjiae awop awi as a as huihusurahr aw.
            """.trimMargin()
        )
    )
) {
    if (num >= 10)
        throw IllegalStateException("Belum bisa melebihi 10 saat ini, males gw ituin guiny")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(110.dp)
            .background(
                if (DataStore.contains(id)) Color(0x4D4BF81A)
                else Color(0x72FFFFFF)
            )
            .border(width = 1.dp, color = Color(0x32000000))
            .drawBehind {
                val w = 25.dp.toPx()
                drawLine(
                    color = Color(0xFFB6B6B6),
                    start = Offset(w, 0f),
                    end = Offset(w, size.height),
                    strokeWidth = 3f
                )
            }
            .clickable {
                Guide.guidePointer = Triple(id, Triple(title, desc, credits), details)
                nav?.navigate(Screen.guidePointDetail)
            },
//        onClick = {
//            Guide.guidePointer = Triple(title, desc, details)
//            nav?.navigate(Screen.guidePointDetail)
//        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(.1f)
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(top = 10.dp, start = 10.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(color = Color(0xFF252525)),
                contentAlignment = Alignment.Center
            ) {
                val dynamicFontSize = (maxWidth.value - 8).sp
                Text(
                    text = num.toString(),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = dynamicFontSize
                )
            }
            Column(
                modifier = Modifier
                    .weight(.75f)
                    .fillMaxSize()
                    .padding(start = 7.5.dp, top = 10.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .weight(.35f)
                        .fillMaxWidth()
                )
                Text(
                    text = desc,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .weight(.65f)
                        .fillMaxWidth()
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                tint = Color.Black,
                contentDescription = "Open the guide",
                modifier = Modifier
                    .weight(.15f)
                    .fillMaxSize()
            )
        }
    }
}