package com.tbear9.openfarm.activities

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trbear9.plants.api.blob.Plant
import com.trbear9.plants.E.CATEGORY.*


@SuppressLint("NotConstructor")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCardDisplayer(score: Int, ref: Plant, nav: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Plant Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            ) {
                if (ref.thumbnail != null) Image(
                    imageVector = Icons.Default.Image,
                    contentDescription = "${ref.nama_ilmiah} image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(16.dp))
                )
                else Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "${ref.nama_ilmiah} image",
                        modifier = Modifier
                            .fillMaxSize(fraction = 0.5f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Text(
                        text = "Gambar tidak tersedia untuk ${ref.commonName}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            val star = (score / 10f) * 5
            val half = (star - star.toInt()) > 0.1f
            // Plant Title
            androidx.compose.foundation.layout.Row() {
                Text(
                    text = ref.commonName,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
                repeat(star.toInt()) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Score",
                        tint = Color.Yellow,
                        modifier = Modifier
                            .size(30.dp)
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

            // Plant Description
            Text(
                text = ref.description,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            // Plant Tags
            com.google.accompanist.flowlayout.FlowRow(
                modifier = Modifier.padding(top = 6.dp),
                mainAxisSpacing = 2.dp,
                crossAxisSpacing = 2.dp
            ) {
                Kat(ref.difficulty, Color.Black, diffToColor(ref.difficulty))
                Kat("3-4 hari", bcolor = Color.LightGray)
                ref.kategori.split(",").forEach {
                    Kat(it, tcolor = Color.White, bcolor = categoryToColor(it))
                }
            }
        }
    }
}


    @Composable
    fun Kat(text: String, tcolor: Color = Color.Black, bcolor: Color = Color(0xFFFF9800), icon: ImageVector? = null){
        Box(
            modifier = Modifier.wrapContentSize()
                .clip(RoundedCornerShape(4.dp))
                .background(bcolor)
        ){
            androidx.compose.foundation.layout.Row () {
                if(icon != null){
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Text(
                    text = text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = tcolor,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }

    fun translateCategory(category: String): String {
        when (category) {
            other.head -> return "Lainnya"
            vegetables.head -> return "Sayur"
            cereals_pseudocereals.head -> return "Pseudocereal"
            roots_tubers.head -> return "Akar/Umbi"
            forage_pastures.head -> return "Padang rumput"
            fruit_nut.head -> return "Buah & kacang"
            materials.head -> return "Bahan"
            ornamentals_turf.head -> return "Rumput hias"
            medicinals_and_armoatic.head -> return "Obat & aromatik"
            forest_or_wood.head -> return "Hutan/Kayu"
            cover_crop.head -> return "Tanaman penutup"
            environmental.head -> return "Lingkungan"
            weed.head -> return "Gulma"
        }
        return "Lainnya"
    }

    fun categoryToColor(category: String): Color {
        return when (category) {
            other.head -> Color.Gray
            vegetables.head -> Color.Green
            cereals_pseudocereals.head -> Color.Yellow
            roots_tubers.head -> Color.Red
            forage_pastures.head -> Color.Blue
            fruit_nut.head -> Color(0xFF9C27B0)
            materials.head -> Color(0xFFFF9100)
            ornamentals_turf.head -> Color(0xFFE91E63)
            medicinals_and_armoatic.head -> Color(0xFF936123)
            else -> Color(0xFF9E9E9E)
        }
    }

    fun diffToColor(diff: String): Color {
        return when (diff) {
            "EASY" -> Color.Green
            "MEDIUM" -> Color.Yellow
            "HARD" -> Color.Red
            else -> Color.Gray
        }
    }

    fun scoreToColor(score: Int): Color {
        return when (score) {
            in 0..2 -> Color.Red
            in 3..6 -> Color.Yellow
            in 7..354 -> Color.Green
            else -> Color.Blue
        }
    }
