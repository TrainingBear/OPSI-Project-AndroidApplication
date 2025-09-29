package com.tbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tbear9.openfarm.MainActivity
import com.tbear9.openfarm.Util
import com.trbear9.plants.PlantClient
import com.trbear9.plants.api.blob.Plant

class PlantDetail : ComponentActivity(){
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ref = intent.getSerializableExtra("plant", Plant::class.java)
            val score = intent.getIntExtra("score", 1)
            PlantDetail(ref = ref!!, onExit = { this.finish() }, score = score)
        }
    }
}

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getHeightByWidth(width: Dp = getScreenWidth(), bias: Float): Dp {
        val res: Dp = (width * bias)
        return res
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getScreenWidth(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp;
    }

    @SuppressLint("NotConstructor")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlantDetail(score: Int, ref: Plant, onExit: () -> Unit, ) {
        val context = LocalContext.current
        val scroll = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.verticalScroll(scroll)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 11f)
                        .background(Color.Gray)
                ) {
                    Image(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Plant image",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    if (ref.fullsize != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(MainActivity .url + PlantClient.IMAGE + "/${ref.fullsize}")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Plant image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .wrapContentSize(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        val star = (score / 10f) * 5
                        val half = (star - star.toInt()) > 0.1f
                        repeat(star.toInt()) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Score",
                                tint = Color.Yellow,
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(2.dp)
                            )
                        }
                        if (half) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                                contentDescription = "Half Score",
                                tint = Color.Yellow,
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onExit() },
                        modifier = Modifier
                            .padding(24.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = ref.commonName,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = ref.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        Label("pH ideal", ref.ph, Icons.Default.LocalFireDepartment)
                        Spacer(modifier = Modifier.width(8.dp))
                        Label(
                            "Panen (hari)",
                            if (ref.min_panen != ref.max_panen) "${ref.min_panen}-${ref.max_panen} hari"
                            else "${ref.min_panen} hari",
                            Icons.Default.HourglassEmpty
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Label("Genus", ref.genus, Icons.Default.Spa)
                    }
                    FlowRow(
                        modifier = Modifier.padding(top = 16.dp),
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        ref.kategori.split(", ").forEach {
                            Kat(Util.translateCategory(it))
                        }
                    }
                    FlowRow(
                        modifier = Modifier.padding(top = 16.dp),
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        ref.common_names.split(", ").forEach {
                            Kat(it, tcolor = Color.Black, bcolor = Color.Green)
                        }
                    }
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 16.sp)) {
                                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                                    append("Takson -> ${ref.kingdom} ${ref.family} ${ref.nama_ilmiah}: ")
                                }
                                pushStyle(
                                    SpanStyle(
                                        color = Color.Blue,
                                        textDecoration = TextDecoration.Underline,
                                        fontWeight = FontWeight.Light
                                    )
                                )
                                append(ref.taxon)
                                pop()
                            }
                        },
                        modifier = Modifier.padding(top = 3.dp),
                        onClick = { offset ->
                            val url = ref.taxon
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Content(
                        "Penyiraman", ref.plantCare.watering,
                        Icons.Default.WaterDrop
                    )
                    Content(
                        "Kontrol Hama", ref.plantCare.pestDiseaseManagement,
                        Icons.Default.Coronavirus
                    )
                    Content(
                        "Pemupukan", ref.plantCare.fertilization,
                        Icons.Default.LocalHospital
                    )
                    Content(
                        "Sinar Matahari", ref.plantCare.sunlight,
                        Icons.Default.WbSunny
                    )
                    Content(
                        "Pruning", ref.plantCare.pruning,
                        Icons.Default.ContentCut
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Content2("Rumah Tangga", ref.productSystem.rumah_tangga)
                    Content2("Komersial", ref.productSystem.komersial)
                    Content2("Industri", ref.productSystem.industri)
                }
            }
        }
    }


    @Composable
    fun Content2(header: String, content: String){
        Text(
            text = header,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = content,
            modifier = Modifier.padding(top = 2.dp),
            fontWeight = FontWeight.Normal
        )
    }
    @Composable
    fun Content(header: String, content: String, icon: ImageVector){
        Row(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    ) {
                        append("$header:")
                    }
                    append(" ")
                    append(content)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
    @Composable
    fun Kat(text: String){
        Box(
            modifier = Modifier.wrapContentSize()
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFF9800))
        ){
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
    @Composable
    fun Label(head: String, body: String, icon: ImageVector) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(8.dp))
        ) {
            Row() {
                Column() {
                    Text(
                        text = head,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(){
                        Image(
                            imageVector = icon,
                            contentDescription = head,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = body,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
