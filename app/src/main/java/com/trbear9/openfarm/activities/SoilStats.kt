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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.trbear9.openfarm.MainActivity
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

val label = listOf("Aluvial", "Andosol", "Humus", "Kapur", "Laterit", "Pasir")
val labelListKey = ExtraStore.Key<List<String>>()

@Preview
@Composable
fun SoilStats(nav: NavController? = null) {
    val scroll = rememberScrollState()
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
                    if(MainActivity.started) nav?.navigate("result")
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
    }) {
        Box(modifier = Modifier.padding(it)) {
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

                        if (MainActivity.response?.soilPrediction == null) Text(
                            text = "Tunggu sebentar...",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        val modelProducer = remember { CartesianChartModelProducer() }
                        runBlocking {
                            modelProducer.runTransaction {
                                columnSeries {
                                    series(
                                        MainActivity.response?.soilPrediction?.toList()
//                                                ?: listOf(1,2,3,4,5,6)
                                            ?: listOf(
                                                Random.nextFloat(), Random.nextFloat(),
                                                Random.nextFloat(), Random.nextFloat(),
                                                Random.nextFloat(), Random.nextFloat(),
                                            )
                                    )
                                }
                                extras {
                                    it[labelListKey] = label
                                }
                            }
                        }
                        JetpackComposeBasicColumnChart(
                            modelProducer,
                            modifier = Modifier.fillMaxSize()
                                .padding(10.dp)
                        )

                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Diagram Prediksi Tanah",
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }


                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Berikut hasil prediksi dari tanahmu:",
                        fontWeight = FontWeight.Normal,
                    )
                    Map(
                        "pH: ",
                        MainActivity.pH?.toString()
                            ?: (MainActivity.response?.soil?.pH.toString() + " (default)")
                    )
                    Map("tipe: ", MainActivity.response?.soilName ?: "tak tersedia")
                    Map("Tekstur: ", MainActivity.response?.soil?.texture?.head ?: "tak tersedia")
                    Map("Drainase: ", MainActivity.response?.soil?.drainage?.head ?: "tak tersedia")
                    Map(
                        "Kesuburan: ",
                        MainActivity.response?.soil?.fertility?.head ?: "tak tersedia"
                    )
                    Text(
                        text = MainActivity.response?.soilCare?.phCorrection
                            ?: "Tunggu sebentar...",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 10.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Cat(
                        Icons.Default.Compost,
                        "Natrium: ",
                        MainActivity.response?.soilCare?.nutrientManagement?.N
                            ?: "Tunggu sebentar..."
                    )
                    Cat(
                        Icons.Default.Grain,
                        "Phospor: ",
                        MainActivity.response?.soilCare?.nutrientManagement?.P
                            ?: "Tunggu sebentar..."
                    )
                    Cat(
                        Icons.Default.BlurOn,
                        "Kalium: ",
                        MainActivity.response?.soilCare?.nutrientManagement?.K
                            ?: "Tunggu sebentar..."
                    )
                    Text(
                        text = MainActivity.response?.soilCare?.organicMatter
                            ?: "Tunggu sebentar... ",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 10.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = MainActivity.response?.soilCare?.waterRetention
                            ?: "Tunggu sebentar... ",
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
        text = buildAnnotatedString() {
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

