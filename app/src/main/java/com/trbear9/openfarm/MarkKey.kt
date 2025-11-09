package com.trbear9.openfarm

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    cocok("Berikut salah satu tanaman yang cocok"),
    skor("Banyaknya bintang menunjukan skor kecocokan tanahmu terhadap tanaman ini"),
    analisa("Click untuk analisa tanahmu"),
    help("Click untuk baca Panduan selengkapnya");

    @Composable
    fun tooltip(){
        Text(
            tooltip,
            fontSize = 16.sp, fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
    @Composable
    fun tooltip(placement: ToolTipPlacement){
        when(placement){
            ToolTipPlacement.Top -> {
                Balloon(Arrow.Bottom()) {
                    tooltip()
                }
            }
            ToolTipPlacement.Bottom -> {
                Balloon(Arrow.Top()) {
                    tooltip()
                }
            }
            ToolTipPlacement.Start -> {
                Balloon(Arrow.End()) {
                    tooltip()
                }
            }
            ToolTipPlacement.End -> {
                Balloon(Arrow.Start()) {
                    tooltip()
                }
            }
        }
    }
}