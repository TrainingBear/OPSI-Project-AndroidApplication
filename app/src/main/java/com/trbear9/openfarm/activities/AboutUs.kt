package com.trbear9.openfarm.activities

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.trbear9.openfarm.R
import com.trbear9.openfarm.util.Screen

private val backgroundCardColor: Color = Color(0x4DFFFFFF)
private val backgroundTitleColor: Color = Color(0x27050091)
private val clipRound: Dp = 15.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AboutUs(nav: NavController? = null) {
    val context = LocalContext.current
    val urihandler = LocalUriHandler.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFC8E3CA),
                        Color(0xFFD7EEE4),
                        Color(0xFFE5E1D2)  // ulang atau variasi
                    ), start = Offset(500f, 0f), end = Offset.Infinite
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .wrapContentHeight()
                            .border(width = 2.dp, color = Color(0x12000000)),
                        navigationIcon = {
                            IconButton(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
//                                .background(Color(0x80EAEAEA))
                                    .size(40.dp),
                                onClick = { nav?.navigate(Screen.home) },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "back arrow",
                                    tint = Color.Black,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        },
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tentang Kami",
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    overflow = TextOverflow.Visible
                                )
                            }
                        }
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(clipRound))
//                    .background(backgroundCardColor)
            ) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.oak_sapling),
                        contentDescription = "About Us",
//            contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Panduan cerdas untuk Agrikultur berkelanjutan berbasis tehnologi.",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 60.dp, end = 60.dp,top = 6.dp, bottom = 10.dp)
                    )
                }
                item @Composable {
                    Text(
                        text = "Pengenalan",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 10.dp)
                    )
                    Text(
                        text = "Aplikasi ini mulai dikembangkan pada 24 Mei 2025 oleh Kukuh, Refan (Pelajar SMA Negeri 1 Ambarawa) dan Renaldi (Pelajar MAN 3 Jakarta Pusat)",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
                    )
                }
                item @Composable {
                    val licenseExpand = remember { mutableStateOf(false) }
                    ExpandCard(
                        text = "Lisensi",
                        backgroundColor = Color(0x3400FF2A),
                        imageVector = Icons.Default.LocalPolice,
                        isExpanded = licenseExpand
                    ) {
                        val annotated = buildAnnotatedString {
                            append("© 2025 Jasper\n")
                            append("Aplikasi ini di dalam lisensi Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0).\n")
                            append("Anda bebas menggunakan dan membagikan aplikasi ini dengan atribusi yang tepat.\n\n")

                            append("License details: ")

                            // add clickable link
                            pushStringAnnotation(
                                tag = "URL",
                                annotation = "https://creativecommons.org/licenses/by-sa/4.0/"
                            )
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF1565C0),
                                    textDecoration = TextDecoration.Underline,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("https://creativecommons.org/licenses/by-sa/4.0/")
                            }
                            pop()

                            append("\n\nThird-party data and components:\n")
                            append("Ecocrop database © FAO 2025\n")

                            pushStringAnnotation(
                                tag = "URL",
                                annotation = "https://gaez.fao.org/pages/ecocrop-search"
                            )
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF1565C0),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("https://gaez.fao.org/pages/ecocrop-search")
                            }
                            pop()

                            append(" — Accessed 9 November 2025\nUsed under FAO Terms and Conditions for non-commercial use.\n\n")

                            append("Taxonomic data © The Trustees of the Royal Botanic Gardens, Kew.\n")
                            append("Licensed under CC BY 4.0.\n")
                            pushStringAnnotation(
                                tag = "URL",
                                annotation = "https://www.kew.org/science/data-and-resources"
                            )
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF1565C0),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("https://www.kew.org/science/data-and-resources")
                            }
                            pop()
                        }
                        ClickableText(
                            text = annotated,
                            onClick = { offset ->
                                annotated.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        urihandler.openUri(annotation.item) // 🔗 opens in browser
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    }
                }
                item @Composable {
                    val medSosExpand = remember { mutableStateOf(false) }
                    ExpandCard(
                        text = "Media Social",
                        backgroundColor = Color(0x4D11FF00),
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        isExpanded = medSosExpand
                    ) {
                        val anotated = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ){
                                append("Discord: ")
                            }
                            pushStringAnnotation(
                                tag = "URL",
                                annotation = "https://discord.gg/fbAZSd3Hf2"
                            )
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF1565C0),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("https://discord.gg/fbAZSd3Hf2")
                            }
                            append("\n")
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ){
                                append("Youtube: ")
                            }
                            pushStringAnnotation(
                                tag = "URL",
                                annotation = "https://www.youtube.com/channel/UCglCTRnlIGvO5o5VfI1t9NQ"
                            )
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF1565C0),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("https://www.youtube.com/channel/kujatic")
                            }
                        }
                        ClickableText(
                            text = anotated,
                            onClick = { offset ->
                                anotated.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        urihandler.openUri(annotation.item)
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    }
                }
                item @Composable {
                    val medSosExpand = remember { mutableStateOf(false) }
                    ExpandCard(
                        text = "FAQ",
                        backgroundColor = Color(0x5400D023),
                        imageVector = Icons.Default.Task,
                        isExpanded = medSosExpand
                    ) {
                        Text(
                            text = "https://cdn.discordapp.com/attachments/1330496061558620191/1372439934291677215/New_Piskel-1.png_13.png?ex=6910214b&is=690ecfcb&hm=5872d37954dfc6fd0780eabe1baf516c49587bc99b1671ebfce2fe25a52600fe&",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        )
                    }
                }
                item @Composable {
                    Spacer(modifier = Modifier.height(40.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.smanega),
                            contentDescription = "logo SMAN Ambarawa 1",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(5.5.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, "https://sman1ambarawa.sch.id/".toUri())
                                    context.startActivity(intent)
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.logoman3),
                            contentDescription = "logo MAN Jakarta 3",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(5.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, "https://man3jkt.sch.id/".toUri())
                                    context.startActivity(intent)
                                }
                        )
                    }
                    Text(
                        text = "Version: 1.0.0   Tahun rilis: 2025",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

/**
 * Custom [Composable] element
 * */
@Composable
private fun ExpandCard(
    text: String,
    backgroundColor: Color,
    contentColor: Color = lerp(backgroundColor, Color.White, .75f),
    imageVector: ImageVector,
    isExpanded: MutableState<Boolean>,
    content: @Composable (BoxScope.() -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(backgroundColor)
            .clickable { isExpanded.value = !isExpanded.value }
            .padding(8.5.dp),
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Icon",
            tint = Color.Black,
            modifier = Modifier
                .weight(.15f)
                .fillMaxSize()
                .padding(3.5.dp)
        )
        Text(
            text = text,
            fontSize = 23.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(.7f)
                .padding(start = 2.5.dp)
        )
        Icon(//               gtw napa kebalik jir
            imageVector = if (isExpanded.value) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
            tint = Color.Black,
            contentDescription = "Expand the menu",
            modifier = Modifier
                .weight(.15f)
                .fillMaxSize()
        )
    }
    val animateTime = 400
    AnimatedVisibility(
        visible = isExpanded.value,
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(
                durationMillis = animateTime,
                easing = LinearOutSlowInEasing
            )
        ) + fadeIn(),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(
                durationMillis = animateTime,
                easing = FastOutLinearInEasing
            )
        ),
        modifier = Modifier.zIndex(0f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(contentColor)
        ) { content() }
    }
}