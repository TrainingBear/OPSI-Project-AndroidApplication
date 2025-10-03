//   Copyright 2025 by Patryk Goworowski and Patrick Michalik.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.trbear9.openfarm.activities

import android.graphics.Typeface
import android.text.Layout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Compost
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.auto
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.trbear9.internal.TFService
import com.trbear9.openfarm.MA
import com.trbear9.plants.api.Response
import com.trbear9.plants.E.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.math.absoluteValue
import kotlin.random.Random

val labelListKey = ExtraStore.Key<List<String>>()

@Preview
@Composable
fun SoilStats(nav: NavController? = null) {
    val scroll = rememberScrollState()
    val response = MA.response?.collectAsState(Response())
    var soilType by remember { mutableStateOf("Belum diktahui") }
    var soilPred by remember { mutableStateOf("0%") }
    var ferr by remember { mutableStateOf("Belum diktahui") }
    var texr by remember { mutableStateOf("Belum diktahui") }
    var drain by remember { mutableStateOf("Belum diktahui") }
    var pH by remember { mutableStateOf(MA.soil.pH) }
    var waterRetention by remember { mutableStateOf(retention(""))}
    var depth by remember { mutableStateOf(150) }

    LaunchedEffect(Unit){
        MA.response?.collect {
            soilType = it.soilMax?.first ?: soilType
            soilPred = it.soilMax?.first ?: soilPred
            val soil = TFService.soils[soilType]
            if(soil != null){
                ferr = soil.fertility?.head ?: "Belum diketahui"
                texr = soil.texture?.head?: "Belum diketahui"
                drain = soil.drainage?.head?: "Belum diketahui"
                pH = pH ?: soil.pH
                waterRetention = retention(soilType)
            }
            depth = it.numericDepth
        }
    }

    Scaffold(bottomBar = {
        var selected by remember { mutableIntStateOf(2) }
        NavigationBar {
            NavigationBarItem(
                selected = selected == 0,
                onClick = {
                    selected = 0
                    nav?.navigate("home")
                },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = selected == 1,
                onClick = {
                    selected = 1
                    if(MA.started) nav?.navigate("result")
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
        Box(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.verticalScroll(scroll)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(350.dp)
                            .wrapContentSize()
                    ) {
                        val modelProducer = remember { CartesianChartModelProducer() }
                        var list = mutableListOf<Float>()
                        runBlocking {
                            modelProducer.runTransaction {
                                columnSeries {
                                    response?.value?.soilPrediction?.run {
                                        forEach { pair ->
                                            list.add(pair.second)
                                        }
                                    }
                                    series(
                                        if (list.isEmpty()) {
                                            listOf(
                                                Random.nextFloat(), Random.nextFloat(),
                                                Random.nextFloat(), Random.nextFloat(),
                                                Random.nextFloat(), Random.nextFloat(),
                                            )
                                        } else list
                                    )
                                    extras {
                                        it[labelListKey] = TFService.labels
                                    }
                                }
                            }
                        }
                        JetpackComposeBasicColumnChart(
                            modelProducer,
                            modifier = Modifier.fillMaxHeight()
                                .width(400.dp)
                        )

                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (response?.value?.soilPrediction == null) Text(
                            text = "Tunggu sebentar...",
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )else Text(
                            text = "Diagram Prediksi Tanah",
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }


                Column(modifier = Modifier.padding(10.dp)) {
                    val value = response?.value
                    Text(
                        text = "Berikut hasil prediksi dari tanahmu:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    Map(
                        "pH: ",
                        MA.pH?.toString()
                            ?: (value?.soil?.pH.toString() + " (default)")
                    )
                    Map("tipe: ", value?.soilName ?: "tak tersedia")
                    Map("Tekstur: ", value?.soil?.texture?.head ?: "tak tersedia")
                    Map("Drainase: ", value?.soil?.drainage?.head ?: "tak tersedia")
                    Map(
                        "Kesuburan: ",
                        value?.soil?.fertility?.head ?: "tak tersedia"
                    )
                    Text(
                        text = retention(soilType),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 10.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun Map(key:String, value:String){
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 18.sp)) { append(key) }
            withStyle(style = SpanStyle(fontSize = 18.sp)) { append(value) }
        },
        fontWeight = FontWeight.Light,
        modifier = Modifier.padding(start = 5.dp)
    )
}

@Composable
private fun Cat(icon: ImageVector, head:String, body: String) {
    Row() {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = buildAnnotatedString() {
                withStyle(style = SpanStyle(fontSize = 18.sp)) { append(head) }
                withStyle(style = SpanStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)) { append(body) }
            },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun JetpackComposeBasicColumnChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    val scroll = rememberVicoScrollState()
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = fill(Color(0xFF2979FF)),
                                thickness = 12.dp,
                                shadow = Shadow(3f),
                                margins = Insets(1f)
                            )
                        ,
                    ),
                    columnCollectionSpacing = 2.dp,
                    dataLabel = rememberTextComponent(
                        textAlignment = Layout.Alignment.ALIGN_NORMAL,
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(
                    labelRotationDegrees = 0f,
                    size = BaseAxis.Size.auto(0.dp, 45.dp)
                ),
                topAxis = HorizontalAxis.rememberTop(
                    label = rememberTextComponent(
                        textSize = 12.sp,
                        textAlignment = Layout.Alignment.ALIGN_CENTER,
                        typeface = Typeface.MONOSPACE,
                        lineCount = 1,
                        lineHeight = 16.sp,
                    ),
                    valueFormatter = CartesianValueFormatter { context, x, _ ->
                        context.model.extraStore[labelListKey][x.toInt()]
                    },
                    labelRotationDegrees = -80f
                ),
            ),
        animateIn = true,
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = scroll
    )
}

//3. Dosis Dolomit (t/ha) = CaCO3 / 0,75
fun getDosis(name: String, target:Float, depth: Int) : Float{
    val pH = MA.soil.pH
    if(MA.soil == null || pH == null){
        return -1f
    }
    val CaCO3: Float = when(name){
        "Aluvial" -> {
            (target - pH).absoluteValue * 3.0 * (depth /20)
        }
        "Andosol" -> {
            (target - pH).absoluteValue * 5.0 * (depth /20)
        }
        "Humus" -> {
            (target - pH).absoluteValue * 4.0 * (depth /20)
        }
        "Kapur" -> {
            (target - pH).absoluteValue * 3.0 * (depth /20)
        }
        "Laterit" -> {
            (target - pH).absoluteValue * 3.5 * (depth /20)
        }
        "Pasir" -> {
            (target - pH).absoluteValue * 2.0 * (depth /20)
        }
        else -> -1
    } as Float
    return CaCO3/75
}

fun retention(name: String?): String{
    return when(name){
        "Aluvial" -> """
Tanah aluvial memiliki kemampuan menahan air sedang sehingga perlu dikelola agar tetap lembap. Gunakan mulsa organik (jerami atau daun kering) setebal 5–10 cm untuk mengurangi penguapan, serta tambahkan biochar 2–5 ton/ha agar kapasitas simpan air meningkat. Penyiraman dilakukan saat kelembapan turun di bawah 60% kapasitas lapang dengan metode irigasi sederhana atau tetes. Tambahkan pupuk organik 10–15 ton/ha dan tanam cover crop untuk menjaga struktur serta kelembapan tanah.
"""
        "Andosol" -> """
Tanah andosol umumnya memiliki porositas tinggi dan mampu menahan air cukup baik, tetapi mudah kehilangan kelembapan saat kering. Untuk menjaga kestabilan air, gunakan mulsa organik (jerami, daun kering) setebal 5–10 cm dan tambahkan biochar 3–6 ton/ha agar daya simpan air lebih optimal. Lakukan penyiraman ketika kelembapan tanah turun di bawah 70% kapasitas lapang, serta gunakan irigasi tetes atau sprinkle agar distribusi air lebih merata. Pemberian pupuk organik 15–20 ton/ha serta penanaman cover crop dianjurkan untuk memperbaiki struktur tanah dan mempertahankan kelembapan lebih lama.
"""
        "Humus" -> """
Tanah humus memiliki kandungan bahan organik tinggi sehingga daya menahan airnya sangat baik. Namun, kelembapan tetap perlu dijaga agar stabil bagi tanaman. Gunakan mulsa organik (jerami, daun kering) setebal 5–10 cm untuk mengurangi penguapan, serta tambahkan biochar 2–4 ton/ha bila diperlukan agar struktur tanah lebih kokoh. Penyiraman cukup dilakukan ketika kelembapan tanah turun hingga sekitar 70% kapasitas lapang, karena humus cenderung mampu menyimpan air lebih lama. Pemberian pupuk organik tambahan 10–15 ton/ha dan penanaman cover crop akan membantu mempertahankan kelembapan sekaligus meningkatkan kesuburan tanah.
"""
        "Retensi" -> """
Tanah kapur umumnya bertekstur kasar, cepat meresapkan air, tetapi sulit menyimpannya sehingga kelembapan cepat hilang. Untuk meningkatkan retensi air, gunakan mulsa organik (jerami, serasah, daun kering) setebal 7–10 cm agar mengurangi penguapan. Tambahkan biochar 4–6 ton/ha serta pupuk organik 15–20 ton/ha untuk memperbaiki porositas dan daya simpan air. Penyiraman perlu lebih sering, terutama saat kelembapan turun di bawah 60% kapasitas lapang, dengan sistem irigasi tetes atau alur agar penyerapan lebih efisien. Penanaman cover crop juga disarankan untuk menjaga kelembapan tanah dan menambah bahan organik.
"""
        "Laterit" -> """
Tanah laterit memiliki kandungan liat tinggi, drainase kurang baik, dan mudah mengeras saat kering sehingga retensi airnya rendah. Untuk menjaga ketersediaan air, gunakan mulsa organik (jerami, serasah, daun kering) setebal 5–8 cm agar kelembapan lebih stabil. Tambahkan biochar 3–5 ton/ha serta pupuk organik 15–20 ton/ha untuk memperbaiki struktur tanah dan meningkatkan kapasitas menahan air. Penyiraman dilakukan ketika kelembapan turun hingga <65% kapasitas lapang, dengan metode irigasi tetes atau sprinkle agar air terserap merata. Penanaman cover crop dianjurkan untuk mencegah pemadatan, menjaga kelembapan, dan menambah bahan organik.
"""
        "Pasir" -> """
Retensi Air Tanah Pasir
Tanah pasir memiliki pori besar, sehingga cepat meloloskan air dan sangat rendah daya menahan airnya. Untuk meningkatkan retensi, gunakan mulsa organik (jerami, sekam, daun kering) setebal 7–10 cm agar mengurangi penguapan. Tambahkan biochar 5–7 ton/ha serta pupuk organik 20–25 ton/ha untuk meningkatkan kandungan bahan organik dan memperbaiki struktur tanah. Penyiraman perlu lebih sering, dilakukan saat kelembapan turun di bawah 50–55% kapasitas lapang, dengan metode irigasi tetes agar air langsung terserap ke zona perakaran. Penanaman cover crop juga penting untuk menahan kelembapan dan menambah unsur organik pada tanah berpasir.
"""
        else -> "Tidak tersedia"
    }
}
