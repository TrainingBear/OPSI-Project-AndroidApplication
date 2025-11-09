package com.trbear9.openfarm

import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trbear9.internal.Data
import com.trbear9.openfarm.activities.Guide
import com.trbear9.openfarm.activities.PointDetail
import com.trbear9.openfarm.activities.SearchLayout
import com.trbear9.openfarm.activities.SoilResultScreen
import com.trbear9.openfarm.activities.SoilStats
import com.trbear9.openfarm.activities.SoilStatsActivity
import com.trbear9.openfarm.util.Screen
import com.trbear9.plants.api.SoilParameters
import com.trbear9.plants.api.UserVariable

object LocalNav {
    var current: NavController? = null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(app: AppCompatActivity) {
    val context = LocalContext.current
    var nav = rememberNavController()
    DisposableEffect(Unit) {
        LocalNav.current = nav
        onDispose { LocalNav.current = null }
    }
    NavHost(navController = nav, startDestination = "home") {
        composable(Screen.camera) {
            CameraActivity(nav, onClick = {
                nav.navigate(Screen.inputSoil)
            })
        }
        composable(Screen.inputSoil) {
            InputSoil(nav, onClick = { pH: Float?, depth: Int? ->
                getLocation(context)
                val variable = UserVariable()
                val soil = SoilParameters()
                if (pH != null) soil.pH = pH.coerceAtMost(14f)
                if (depth != null) soil.numericDepth = depth
                inputs.soil = soil
                variable.geo = inputs.geo
                variable.image = inputs.image
                variable.soil = soil
                inputs.soilResult.collected = false
                inputs.response = Data.process(variable, inputs.soilResult)
//                Toast.makeText(context, "finished", Toast.LENGTH_SHORT)
//                    .show()
                Util.debug("Job has been finished!")
                inputs.soilResult.res = inputs.response
                nav.navigate(Screen.soilResult)
            })
        }
        composable(Screen.soilResult) {
//            val intent = Intent(app, PlantResultActivity::class.java)
//            app.startActivity(intent)
            SoilResultScreen()
        }
        composable(Screen.search) {
            SearchLayout(
                nav = nav
            )
        }
        composable(Screen.soilStats) {
//            SoilStats(nav)
            val intent = Intent(context, SoilStatsActivity::class.java)
            context.startActivity(intent)
        }
        composable(Screen.home) {
            Home(nav)
        }
        composable(Screen.help) {
            Guide(nav)
        }
        composable(Screen.tentang) {
            AndroidView(
                factory = {
                    LayoutInflater.from(it).inflate(R.layout.botakuhpengetahuan, null)
                },
                update = {
                    val about = it.findViewById<ImageButton>(R.id.tombolKembali)
                    about.setOnClickListener {
                        nav.navigateUp()
                    }
                }
            )
        }
        composable(Screen.about) {
            AndroidView(
                factory = {
                    LayoutInflater.from(it).inflate(R.layout.activity_about_me, null)
                },
                update = {
                    val aboutMe = it.findViewById<ImageButton>(R.id.buttonBackmenuabout)
                    aboutMe.setOnClickListener {
                        nav.navigateUp()
                    }
                }
            )
        }
        composable(Screen.guidePointDetail){
            PointDetail(
                nav = nav,
                title = Guide.guidePointer!!.first,
                subtitle = Guide.guidePointer!!.second,
                details = Guide.guidePointer!!.third
            )
//            Guide.guidePointer = null
        }
    }
}