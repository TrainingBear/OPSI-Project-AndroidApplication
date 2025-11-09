package com.trbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.pseudoankit.coachmark.UnifyCoachmark
import com.pseudoankit.coachmark.model.ToolTipPlacement
import com.pseudoankit.coachmark.scope.enableCoachMark
import com.pseudoankit.coachmark.util.CoachMarkKey
import com.trbear9.internal.Data
import com.trbear9.openfarm.MarkKey
import com.trbear9.openfarm.highlightConfig
import com.trbear9.plants.E
import com.trbear9.plants.E.CATEGORY.*
import com.trbear9.plants.api.blob.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CONS {
    val noImage = Icons.Default.Image
    val noImage2 = Icons.Default.WbSunny
}

var coached by mutableStateOf(false)
@SuppressLint("NotConstructor")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlantCardDisplayer(score: Int = 0, ref: Plant?) {
    val context = LocalContext.current

    if (ref != null) {
        UnifyCoachmark {
            val cmodifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .padding(8.dp)
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                onClick = {
                    val intent = Intent(context, PlantDetail::class.java)
                    intent.putExtra("plant", ref)
                    intent.putExtra("score", score)
                    context.startActivity(intent)
                },
                modifier = if(!coached) {
//                    coached = true
                    cmodifier
                        .enableCoachMark(
                            key = MarkKey.cocok,
                            toolTipPlacement = ToolTipPlacement.Bottom,
                            highlightedViewConfig = highlightConfig
                        ) {
                            MarkKey.cocok.tooltip(ToolTipPlacement.Bottom)
                        }
                }
                else cmodifier
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                    ) {
                        Box(Modifier.fillMaxWidth().aspectRatio(16 / 9f)) {
                            val model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/images/${ref.nama_ilmiah}.webp")
                                .crossfade(true)
                                .size(600, 400)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build()
                            val painter = rememberAsyncImagePainter(model)
                            val state = painter.state
                            AsyncImage(
                                model = model,
                                contentDescription = "${ref.nama_ilmiah} image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            if (state is AsyncImagePainter.State.Error || state is AsyncImagePainter.State.Loading) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        imageVector = CONS.noImage,
                                        contentDescription = "${ref.nama_ilmiah ?: "no"} image",
                                        modifier = Modifier
                                            .fillMaxSize(fraction = 0.5f)
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                    Text(
                                        text = "Gambar tidak tersedia untuk ${ref?.commonName}",
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            if (score != 0) {
                                val star = (score / 10f) * 5
                                val half = (star - star.toInt()) > 0.1f
                                val modifier = Modifier
                                    .padding(end = 10.dp, top = 7.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .wrapContentSize(Alignment.Center)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .align(Alignment.TopEnd)
                                androidx.compose.foundation.layout.Row(
                                    modifier = if(!coached) modifier
                                        .enableCoachMark(
                                            key = MarkKey.skor,
                                            toolTipPlacement = ToolTipPlacement.Top,
                                            highlightedViewConfig = highlightConfig
                                        ){
                                            MarkKey.skor.tooltip(ToolTipPlacement.Top)
                                        }
                                    else modifier
                                ) {
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
                            }
                        }
                    }

                    // Plant Title
                    Text(
                        text = ref.commonName?.split(",")[0] ?: "Tidak tersedia",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    // Plant Description
                    Text(
                        text = ref.description?.take(100) + if ((ref.description?.length
                                ?: 0) > 100
                        ) "..." else "",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    // Plant Tags
                    com.google.accompanist.flowlayout.FlowRow(
                        modifier = Modifier.padding(top = 6.dp),
                        mainAxisSpacing = 2.dp,
                        crossAxisSpacing = 2.dp
                    ) {
                        Kat(
                            ref.difficulty?.toString() ?: "???",
                            Color.Black,
                            diffToColor(ref.difficulty?.toString() ?: "UNKNOWN")
                        )
                        val panen_min = Data.ecocrop[ref.nama_ilmiah]?.get(E.MIN_crop_cycle)
                        val panen_max = Data.ecocrop[ref.nama_ilmiah]?.get(E.MAX_crop_cycle)
                        Kat(
                            if (panen_min != panen_max) "$panen_min-$panen_max hari"
                            else if (panen_min == "0") ""
                            else if (panen_min == "null") ""
                            else "$panen_min hari",
                            bcolor = Color.LightGray
                        )
                        ref.category?.forEach {
                            Kat(
                                translateCategory(it), tcolor = Color.White,
                                bcolor = categoryToColor(it)
                            )
                        }
                    }
                }
            }
            coached = true
        }
    }
}


    @Composable
    fun Kat(
        text: String,
        tcolor: Color = Color.Black,
        bcolor: Color = Color(0xFFFF9800),
        icon: ImageVector? = null
    ) {
        if(text.isNotEmpty()) Box(
            modifier = Modifier.wrapContentSize()
                .clip(RoundedCornerShape(4.dp))
                .background(bcolor)
        ) {
            androidx.compose.foundation.layout.Row() {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Text(
                    text = text,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tcolor,
                    modifier = Modifier.padding(6.dp)
                )
            }
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

        fun categoryToColor(category: String): Color {
            return when (category) {
                other.head -> Color.Gray
                vegetables.head -> Color.Green
                cereals_pseudocereals.head -> Color(0xFF4CAF50)
                roots_tubers.head -> Color.Red
                forage_pastures.head -> Color.Blue
                fruit_nut.head -> Color(0xFF9C27B0)
                materials.head -> Color(0xFFFF9100)
                ornamentals_turf.head -> Color(0xFFE91E63)
                medicinals_and_armoatic.head -> Color(0xFF936123)
                else -> Color(0xFF9E9E9E)
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
class ImageAsset {
    companion object {
        val images = mutableMapOf<String, Bitmap>()


        fun getImage(context: Context, name: String?): Bitmap? {
            if(name == null) return null
            if (ImageAsset.images.containsKey(name)) return ImageAsset.images[name]!!
            try {
                context.assets.open("images/$name.webp").use {
                    val decodeStream = BitmapFactory.decodeStream(it)
                    ImageAsset.images[name] = decodeStream
                    return decodeStream
                }
            } catch (_: Exception) {
                return null
            }
        }
    }
}

fun test() {

}
fun getImage(context: Context, name: String?): Bitmap? {
    if (name == null) return null
    if (ImageAsset.images.containsKey(name)) return ImageAsset.images[name]!!
    try {
        context.assets.open("images/$name.webp").use {
            val decodeStream = BitmapFactory.decodeStream(it)
            ImageAsset.images[name] = decodeStream
            return decodeStream
        }
    } catch (_: Exception) {
        return null
    }
}


