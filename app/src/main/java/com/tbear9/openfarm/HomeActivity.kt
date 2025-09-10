package com.tbear9.openfarm

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.RectRulers
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HomeActivity {

    //TODO : make a good design
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(name = "greet",
        device = "id:pixel_9_pro_xl", showSystemUi = true, showBackground = false
    )
    @Composable
    fun Greeting(){
        Box (modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .fillMaxSize()
            .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Apbar(
                text = "OpenFarm",
                warna = Color.Green
            )
            PageBackground()
        }
    }

    @Composable
    fun PageBackground(){
        MaterialTheme (
            typography = MaterialTheme.typography.copy(
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = responsiveFontSize(18)
                ),
                titleLarge = MaterialTheme.typography.titleLarge.copy(
                    fontSize = responsiveFontSize(28),
                ),
            ),
        ) {
            Text(
                text = "Selamat datang di Botania!",
                modifier = Modifier
                    .offset(y = (-200).dp),
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Panaen berlimpah, di mulai dari hal yang kecil",
                modifier = Modifier
                    .offset(y = (-150).dp),
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    painter = painterResource(R.drawable.background_opsi_mainactivity),
                    contentDescription = null,
                )
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Apbar(text: String, warna: Color = Color.Green){
         Scaffold (
            topBar = {
                TopAppBar(
                    title = {
                            Text(
                                modifier = Modifier
                                    .offset(x = (10).dp),
                                text = text,
                                fontSize = 20.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .background(warna),
                    navigationIcon = {
                            Image(
                                modifier = Modifier
                                    .size(50.dp)
                                    .offset(x = (5).dp),
                                painter = painterResource(R.drawable.opsi_logo),
                                contentDescription = null
                            )
                        },
                    )
                }
            ){  }
    }


    @Composable
    fun responsiveFontSize(baseSize: Int): TextUnit {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp

        return (baseSize * (screenWidth / 411f)).sp // 411 = baseline width (Pixel 2)
    }
}