package com.tbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import androidx.core.net.toUri
import com.trbear9.plants.api.blob.Plant
import java.util.Locale

class PlantDetail : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ref = intent.getSerializableExtra("plant", Plant::class.java)
            PlantDetail(ref = ref, onExit = { finish() })
        }
    }
}

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getHeightByWidth(width: Dp = getScreenWidth(), bias: Float): Dp {
        val res: Dp = (width * bias)
        return res
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getScreenWidth(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp;
    }

    @SuppressLint("NotConstructor")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlantDetail(ref: Plant, onExit: () -> Unit, ) {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.7f)
                        .background(Color.Gray)
                ) {
                    if(ref.thumbnail == null){
                        Image(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Plant image",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }else {
                        Image(
                            bitmap = BitmapFactory.decodeByteArray(
                                ref.thumbnail,
                                0,
                                ref.thumbnail.size
                            ).asImageBitmap(),
                            contentDescription = "Plant image",
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    IconButton(
                        onClick = { onExit },
                        modifier = Modifier
                            .padding(24.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = ref.commonName,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text =ref.description,
                        fontSize = 16.sp
                    )
                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        val diff = ref.difficulty.lowercase()
                        diff.replaceFirstChar { it.uppercase() }
                        Label("Difficulty", diff, Icons.Default.LocalFireDepartment)
                        Spacer(modifier = Modifier.width(16.dp))
                        Label("Panen", "${ref.min_panen}-${ref.max_panen} hari", Icons.Default.HourglassEmpty)
                        Spacer(modifier = Modifier.width(16.dp))
                        Label("Genus", ref.genus, Icons.Default.Spa)
                    }
                    FlowRow(
                        modifier = Modifier.padding(top = 16.dp),
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        ref.kategori.split(",").forEach {
                            Kat(it)
                        }
                    }
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 16.sp)) {
                                append("Takson -> ${ref.kingdom} ${ref.family} ${ref.nama_ilmiah}: ")
                                pushStyle(
                                    SpanStyle(
                                        color = Color.Blue,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                                append(ref.taxon)
                                pop()
                            }
                        },
                        modifier = Modifier.padding(top = 3.dp),
                        onClick = { offset ->
                            val url = ref.taxon
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Content(
                        "Penyiraman", ref.plantCare.watering,
                        Icons.Default.WaterDrop
                    )
                    Content(
                        "Kontrol Hama", ref.plantCare.pestDiseaseManagement,
                        Icons.Default.Coronavirus
                    )
                    Content(
                        "Pemupukan", ref.plantCare.fertilization,
                        Icons.Default.LocalHospital
                    )
                    Content(
                        "Sinar Matahari", ref.plantCare.sunlight,
                        Icons.Default.WbSunny
                    )
                    Content(
                        "Pruning", ref.plantCare.pruning,
                        Icons.Default.ContentCut
                    )
                    Content2("Rumah Tangga", ref.productSystem.rumah_tangga)
                    Content2("Komersial", ref.productSystem.komersial)
                    Content2("Industri", ref.productSystem.industri)
                }
            }
        }
    }


    @Composable
    fun Content2(header: String, content: String){
        Text(
            text = header,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = content,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
    @Composable
    fun Content(header: String, content: String, icon: ImageVector){
        Row(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    ) {
                        append("$header:")
                    }
                    append(" ")
                    append(content)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
    @Composable
    fun Kat(text: String){
        Box(
            modifier = Modifier.wrapContentSize()
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFF9800))
        ){
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
    @Composable
    fun Label(head: String, body: String, icon: ImageVector) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(8.dp))
        ) {
            Row() {
                Column() {
                    Text(
                        text = head,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(){
                        Image(
                            imageVector = icon,
                            contentDescription = "Difficulty",
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = body,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}