package com.trbear9.plants

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.trbear9.ui.activities.SearchResult
import com.trbear9.ui.activities.SoilResult
import com.trbear9.plants.parameters.GeoParameters
import com.trbear9.plants.parameters.Response
import com.trbear9.plants.parameters.SoilParameters
import kotlinx.coroutines.flow.Flow

class Inputs {
    var response: Flow<Response>? = null
    var loaded by mutableStateOf(false)
    var geo = GeoParameters()
    var soil = SoilParameters()
    var pH: Float? = null
    var image: Bitmap? = null
    var soilResult = SoilResult()
    var searchResult = SearchResult()
}