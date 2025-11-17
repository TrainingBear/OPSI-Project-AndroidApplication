package com.trbear9.ui.util

object Screen {
    const val home = "home"
    const val soilStats = "soilStats"
    const val soilResult = "soil_result"
    const val searchResult = "search_result"
    const val search = "searchByCommonName"
    const val about = "about"
    const val tentang = "tentang"
    const val camera = "camera"
    const val inputSoil = "inputSoil"
    const val help = "help"
    const val guidePointDetail = "guide_point_detail"
    const val firstLoading = "firstLoading"
}

fun String.withArgs(vararg arg: Pair<String, String>): String{
    var result = "$this|"
    for (pair in arg) {
        result += "${pair.first}=${pair.second}&"
    }
    return result
}