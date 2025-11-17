package com.trbear9.ui.fragment

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.CoachMarkScope
import com.trbear9.ui.util.MarkKey
import com.trbear9.ui.activities.SoilStatsActivity
import com.trbear9.ui.util.highlightConfig

@Composable
fun CoachMarkScope.NavigateSoilStats(modifier: Modifier) {
    val context = LocalContext.current
    BoxWithConstraints(modifier) {
        Column(
            Modifier
            .align(Alignment.BottomEnd)
            .padding(20.dp)
            .enableCoachMark(
                key = MarkKey.analisa,
                toolTipPlacement = ToolTipPlacement.Start,
                highlightedViewConfig = highlightConfig
            ) {
                MarkKey.analisa.tooltip(ToolTipPlacement.Start)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val fontSize = (this@BoxWithConstraints.maxWidth.value / 27).sp
            Icon(
                Icons.Default.Grain, contentDescription = "Tanah",
                modifier = Modifier.size(this@BoxWithConstraints.maxWidth / 9)
//                        .border(5.dp, Color(0xFFCFE5D4))
                    .shadow(
                        10.dp,
                        RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp),
                        clip = true
                    )
                    .clip(RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp))
                    .clickable {
                        val intent = Intent(context, SoilStatsActivity::class.java)
                        context.startActivity(intent)
                    }
                    .background(Color(0xFFEEEED9).copy(alpha = 1f))
            )
            Box(
                Modifier
                    .padding(top = 10.dp)
                    .wrapContentSize()
                    .shadow(10.dp, RoundedCornerShape(5.dp), clip = true)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFCFE5D4))
            ) {
                Text(
                    text = "Analisa tahahmu",
                    fontSize = fontSize,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}