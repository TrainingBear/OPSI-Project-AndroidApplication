package com.trbear9.openfarm.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun Guide(){
    Box( modifier = Modifier
        .size(400.dp, 700.dp)
        .background(Color.LightGray)
        .clip(RoundedCornerShape(24.dp))
    ){
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Panduan Aplikasi Open Farm",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
            )
            Column {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16/3f)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Panduan 1",
                    )
                }
            }
        }
    }
}