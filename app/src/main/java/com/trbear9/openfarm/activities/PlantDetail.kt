package com.trbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.pseudoankit.coachmark.UnifyCoachmark
import com.trbear9.internal.Data
import com.trbear9.openfarm.NavigateSoilStats
import com.trbear9.openfarm.Util
import com.trbear9.plants.E.A_maximum_ph
import com.trbear9.plants.E.A_minimum_ph
import com.trbear9.plants.E.Climate_zone
import com.trbear9.plants.E.MAX_crop_cycle
import com.trbear9.plants.E.MIN_crop_cycle
import com.trbear9.plants.E.O_maximum_temperature
import com.trbear9.plants.E.O_minimum_temperature
import com.trbear9.plants.api.blob.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        return LocalConfiguration.current.screenWidthDp.dp
    }

    @SuppressLint("NotConstructor")
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun PlantDetail(score: Int = 0, ref: Plant? = null, onExit: () -> Unit = {}) {
        val context = LocalContext.current
        val scroll = rememberScrollState()
        var image by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                if(ref != null) {
                    image = ImageAsset.getImage(context, ref.fullsize)
                }
            }
        }

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
                        .aspectRatio(16/11f)
                        .background(Color.LightGray)
                ) {
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/images/${ref?.nama_ilmiah}.webp")
                        .crossfade(true)
                        .build()
                    val painter = rememberAsyncImagePainter(model)
                    val state = painter.state
                    AsyncImage(
                        model = model,
                        contentDescription = "Plant image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    if(state is AsyncImagePainter.State.Error){
                        Image(
                            imageVector = CONS.noImage2,
                            contentDescription = "Plant image",
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(end = 10.dp, top = 7.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .wrapContentSize(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .align(Alignment.TopEnd)
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
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                if (ref != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = ref.commonName?.toString()?:"null",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = ref.description?.toString()?:"null",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(modifier = Modifier.padding(top = 16.dp)) {
                            val ph_min = Data.ecocrop[ref.nama_ilmiah]?.get(A_minimum_ph)
                            val ph_max = Data.ecocrop[ref.nama_ilmiah]?.get(A_maximum_ph)
                            Label("PH Ideal", "$ph_min - $ph_max", Icons.Default.Science)
                            Spacer(modifier = Modifier.width(8.dp))
                            val panen_min =
                                Data.ecocrop[ref.nama_ilmiah]?.get(MIN_crop_cycle)
                            val panen_max =
                                Data.ecocrop[ref.nama_ilmiah]?.get(MAX_crop_cycle)
                            if (panen_max != "0") {
                                Label(
                                    "Waktu Panen",
                                    if (panen_min != panen_max) "$panen_min-$panen_max hari"
                                    else "$panen_min hari",
                                    Icons.Default.HourglassEmpty
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            val temp_min =
                                Data.ecocrop[ref.nama_ilmiah]?.get(O_minimum_temperature)
                            val temp_max =
                                Data.ecocrop[ref.nama_ilmiah]?.get(O_maximum_temperature)
                            Label(
                                "Temperatur",
                                "$temp_min - $temp_max",
                                Icons.Default.Whatshot
                            )
                        }
                        FlowRow(
                            modifier = Modifier.padding(top = 16.dp),
                            mainAxisSpacing = 4.dp,
                            crossAxisSpacing = 4.dp
                        ) {
                            Data.ecocrop[ref.nama_ilmiah]?.get(Climate_zone)?.split(", ")?.forEach {
                                Kat(Util.translateClimate(it), tcolor = Color.White, bcolor = Color.Blue)
                            }
                        }
                        FlowRow(
                            modifier = Modifier.padding(top = 16.dp),
                            mainAxisSpacing = 4.dp,
                            crossAxisSpacing = 4.dp
                        ) {
                            ref.category?.forEach {
                                Kat(Util.translateCategory(it))
                            }
                        }
//                        FlowRow(
//                            modifier = Modifier.padding(top = 16.dp),
//                            mainAxisSpacing = 4.dp,
//                            crossAxisSpacing = 4.dp
//                        ) {
//                            ref.nama_umum?.forEach {
//                                    Kat(it, tcolor = Color.Black, bcolor = Color.Green)
//                                }
//                        }
                        ClickableText(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 16.sp)) {
                                    val tax = ref.full_taxon
                                    if (tax != null)
                                        withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                                            append(
                                                "Full Taksonomi dari ${
                                                            tax["family"].asText()
                                                                .replace("\"", "")
                                                        } " +
                                                        tax["name"].asText().replace("\"", "")
                                            )
                                            append(":\n")
                                        }
                                    if (ref.taxon != null) {
                                        pushStyle(
                                            SpanStyle(
                                                color = Color.Blue,
                                                textDecoration = TextDecoration.Underline,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        append(ref.taxon.toString())
                                        pop()
                                    }
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            onClick = { offset ->
                                if (ref.taxon != null) {
                                    val url = ref.taxon
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, url.toString().toUri())
                                    context.startActivity(intent)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Content(
                            "Penyiraman", ref.plantCare?.watering ?: "Belum Tersedia",
                            Icons.Default.WaterDrop
                        )
                        Content(
                            "Kontrol Hama",
                            ref.plantCare?.pestDiseaseManagement ?: "Belum Tersedia",
                            Icons.Default.Coronavirus
                        )
                        Content(
                            "Pemupukan", ref.plantCare?.fertilization ?: "Belum Tersedia",
                            Icons.Default.LocalHospital
                        )
                        Content(
                            "Sinar Matahari", ref.plantCare?.sunlight ?: "Belum Tersedia",
                            Icons.Default.WbSunny
                        )
                        Content(
                            "Pruning", ref.plantCare?.pruning ?: "Belum Tersedia",
                            Icons.Default.ContentCut
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Content2(
                            "Rumah Tangga",
                            ref.productSystem?.rumah_tangga ?: "Belum Tersedia"
                        )
                        Content2(
                            "Komersial",
                            ref.productSystem?.komersial ?: "Belum Tersedia"
                        )
                        Content2(
                            "Industri",
                            ref.productSystem?.industri ?: "Belum Tersedia"
                        )
                    }
                }
            }
            UnifyCoachmark {
                NavigateSoilStats(Modifier.fillMaxSize())
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
    fun Kat(text: String) {
        if (text != "" || text != " ")
            Box(
                modifier = Modifier.wrapContentSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFF9800))
            ) {
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
            Row {
                Column {
                    Text(
                        text = head,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
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
