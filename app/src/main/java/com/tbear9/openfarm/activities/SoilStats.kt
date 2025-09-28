//   Copyright 2025 patrykandpatrick/vico
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
package com.tbear9.openfarm.activities

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Canvas
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.rememberDefaultCartesianMarker
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormat

public val label = listOf("Aluvial", "Andosol", "Entisol", "Humus", "Inceptisol", "Laterit", "Kapur", "Pasir")

@Composable
private fun JetpackComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
fun JetpackComposeBasicLineChart(modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
        }
    }
    JetpackComposeBasicLineChart(modelProducer, modifier)
}

@Composable
@Preview
private fun Preview() {
    val modelProducer = remember { CartesianChartModelProducer() }
    // Use `runBlocking` only for previews, which don’t support asynchronous execution.
    runBlocking {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
        }
    }
     JetpackComposeBasicLineChart(modelProducer)
}


@Composable
@Preview
fun SoilStats(){
    Scaffold (bottomBar = {
        var selected by remember { mutableIntStateOf(2) }
        NavigationBar {
            NavigationBarItem(
                selected = selected == 0,
                onClick = {
                    selected = 0
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
                          },
                icon = { Icon(Icons.Default.Grain, contentDescription = "Tanah") },
                label = { Text("Tanah") }
            )
        }
    }){
        Box(modifier = Modifier.padding(it)){
            Box(modifier = Modifier.fillMaxSize()
                .padding(start = 12.dp, end = 8.dp, top = 16.dp)
            ) {
                val modelProducer = remember { CartesianChartModelProducer() }
                val data = mapOf(
                    label[0] to 0.21f, label[1] to 0.134f, label[2] to 0.5f, label[3] to 0.898f,
                    label[4] to 0.91f, label[5] to 0.75f, label[6] to 0.3f, label[7] to 0.6f
                )
                val labelListKey = ExtraStore.Key<List<String>>()
                runBlocking {
                    modelProducer.runTransaction {
                        columnSeries { series(data.values) }
                        extras { it[labelListKey] = data.keys.toList() }
                    }
                }

                JetpackComposeBasicColumnChart(modelProducer)
            }
        }
    }
}

@Composable
private fun JetpackComposeBasicColumnChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val labelListKey = ExtraStore.Key<List<String>>()
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    dataLabelValueFormatter = CartesianValueFormatter { context, x, _ ->
                    context.model.extraStore[labelListKey][x.toInt()]
                }
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = "Soil Parameter",
                ),
            ),
        animateIn = true,
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

