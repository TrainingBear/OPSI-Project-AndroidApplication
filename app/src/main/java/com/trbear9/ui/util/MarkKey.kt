package com.trbear9.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pseudoankit.coachmark.model.HighlightedViewConfig
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.shape.Arrow
import com.pseudoankit.coachmark.shape.Balloon

val highlightConfig = HighlightedViewConfig(
    shape = HighlightedViewConfig.Shape.Rect(12.dp),
    padding = PaddingValues(8.dp)
)

enum class MarkKey(private val tooltip: String) {
    scantanah("Click untuk scan tanahmu"),
    cocok("Berikut salah satu tanaman\nyang cocok berdasarkan\ntanah anda"),
    skor("Banyaknya bintang menunjukan\nnilai kecocokan tanahmu pada\n tanaman ini"),
    analisa("Click untuk analisa tanahmu"),
    help("Click untuk baca Panduan selengkapnya");

    @Composable
    fun tooltip(){
        Text(
            tooltip,
            fontSize = 15.sp, fontWeight = FontWeight.Bold,
//            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
    @Composable
    fun tooltip(placement: ToolTipPlacement){
        val color = Color.Green
        when(placement){
            ToolTipPlacement.Top -> {
                Balloon(Arrow.Bottom(), bgColor = color) {
                    tooltip()
                }
            }
            ToolTipPlacement.Bottom -> {
                Balloon(Arrow.Top(), bgColor = color) {
                    tooltip()
                }
            }
            ToolTipPlacement.Start -> {
                Balloon(Arrow.End(), bgColor = color) {
                    tooltip()
                }
            }
            ToolTipPlacement.End -> {
                Balloon(Arrow.Start(), bgColor = color) {
                    tooltip()
                }
            }
        }
    }
}