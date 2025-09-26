package com.tbear9.openfarm.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.flowlayout.FlowRow
import androidx.core.net.toUri

class PlantDetail {

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getHeightByWidth(width: Dp = getScreenWidth(), bias: Float): Dp {
        val res: Dp = (width!! * bias)
        return res
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getScreenWidth(): Dp {
        return LocalConfiguration.current.screenWidthDp.dp;
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun Demo() {
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
                ){
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .padding(24.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Diserale discota",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tanaman ini ialah tanaman yang hidup di tanah, biasanya hidup di luar negri",
                        fontSize = 16.sp
                    )
                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        Label("Difficulty", "EASY", Icons.Default.LocalFireDepartment)
                        Spacer(modifier = Modifier.width(16.dp))
                        Label("Panen", "1-2 hari", Icons.Default.HourglassEmpty)
                        Spacer(modifier = Modifier.width(16.dp))
                        Label("Kingdom", "Diserale", Icons.Default.FamilyRestroom)
                    }
                    FlowRow(
                        modifier = Modifier.padding(top = 16.dp),
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        Kat("Vegetable")
                        Kat("Sayur")
                        Kat("Buah")
                        Kat("Lele")
                    }
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 16.sp)) {
                                append("Takson -> Plantae King Queen Urtica Lens: ")
                                pushStyle(
                                    SpanStyle(
                                        color = Color.Blue,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                                append("https://powo.science.kew.org/taxon/urn:lsid:ipni.org:names:857987-1")
                                pop()
                            }
                        },
                        modifier = Modifier.padding(top = 3.dp),
                        onClick = { offset ->
                            val url = "https://powo.science.kew.org/taxon/urn:lsid:ipni.org:names:857987-1"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        }
                    )

                    Text(
                        text = "Perawatan:",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Content(
                        "Penyiraman", "Tanaman ini hanya cukup di siram sehari sekali. " +
                                "kalau lebih sering ya lebih bagus. biar seger tanamanya",
                        Icons.Default.WaterDrop
                    )
                    Content(
                        "Kontrol Hama", "Tanaman ini tidak memiliki penyakit yang berbahaya. " +
                                "tapi kalau mau dikasih obat ya boleh. " +
                                "tapi jangan dikasih obat yang tidak diijinkan",
                        Icons.Default.LocalHospital
                    )
                    Content(
                        "Pemupukan", "Tanaman ini tidak memerlukan pupuk. " +
                                "tapi kalau mau dikasih pupuk ya boleh. " +
                                "tapi jangan dikasih pupuk yang tidak diijinkan",
                        Icons.Default.Shield
                    )
                    Content(
                        "Sinar Matahari", "Tanaman ini tidak memerlukan penyiangan. " +
                                "tapi kalau mau dikasih penyiangan ya boleh. " +
                                "tapi jangan dikasih penyiangan yang tidak diijinkan"
                    )
                    Content(
                        "Pruning", "Tanaman ini tidak memerlukan temperatur yang tinggi. " +
                                "tapi kalau mau dikasih temperatur ya boleh. " +
                                "tapi jangan dikasih temperatur yang tidak diijinkan"
                    )

                }
            }
        }
    }
    @Composable
    fun Content(header: String, content: String, icon: ImageVector){
        Row(
            modifier = Modifier.padding(top = 16.dp)
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