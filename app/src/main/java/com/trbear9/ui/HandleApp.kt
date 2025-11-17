package com.trbear9.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trbear9.plants.Data
import com.trbear9.ui.activities.AboutUs
import com.trbear9.ui.activities.Guide
import com.trbear9.ui.activities.PointDetail
import com.trbear9.ui.activities.SearchLayout
import com.trbear9.ui.activities.SoilResultScreen
import com.trbear9.ui.activities.SoilStatsActivity
import com.trbear9.ui.util.Screen
import com.trbear9.plants.parameters.SoilParameters
import com.trbear9.plants.parameters.UserVariable
import com.trbear9.ui.activities.CameraActivity
import com.trbear9.ui.activities.Home
import com.trbear9.ui.activities.InputSoil
import com.trbear9.ui.activities.LoadingScreen
import com.trbear9.ui.activities.MainActivity
import com.trbear9.ui.activities.getLocation
import com.trbear9.ui.activities.gps
import com.trbear9.ui.activities.inputs
import com.trbear9.ui.activities.perm
import com.trbear9.ui.util.Util

object LocalNav {
    var current: NavController? = null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(app: MainActivity) {
    val context = LocalContext.current
    var nav = rememberNavController()
    DisposableEffect(Unit) {
        LocalNav.current = nav
        onDispose { LocalNav.current = null }
    }
    NavHost(navController = nav, startDestination = "firstLoading") {
        composable(Screen.camera) {
            CameraActivity(nav, onClick = {
                nav.navigate(Screen.inputSoil)
            })
        }
        composable(Screen.inputSoil) {
            InputSoil(nav, onClick = { pH: Float?, depth: Int? ->
                perm.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        context,
                        "Tolong izinkan kamera untuk menggunakan fitur ini!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    getLocation(context)
                    gps = true
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

                    if (isNetworkAvailable(app))
                        nav.navigate(Screen.soilResult)
                    else {
                        Toast.makeText(context, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show()
                        nav.navigate(Screen.soilResult)
                    }
                }
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
        composable(Screen.about) {
            AboutUs(nav)
//            AndroidView(
//                factory = {
//                    LayoutInflater.from(it).inflate(R.layout.activity_about_me, null)
//                },
//                update = {
//                    val aboutMe = it.findViewById<ImageButton>(R.id.buttonBackmenuabout)
//                    aboutMe.setOnClickListener {
//                        nav.navigateUp()
//                    }
//                }
//            )
        }
        composable(Screen.guidePointDetail){
            PointDetail(
                nav = nav,
                title = Guide.guidePointer!!.second.first,
                subtitle = Guide.guidePointer!!.second.second,
                id = Guide.guidePointer!!.first,
                details = Guide.guidePointer!!.third,
                credits = Guide.guidePointer!!.second.third
            )
//            Guide.guidePointer = null
        }
        composable(Screen.firstLoading){
            LoadingScreen()
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
