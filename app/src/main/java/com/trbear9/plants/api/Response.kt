package com.trbear9.plants.api

import com.trbear9.plants.api.blob.Plant
import com.trbear9.plants.api.blob.SoilCare
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.io.Serializable

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class Response : Serializable {
    var hashCode: String? = null
    var soilName: String? = null
    var soilCare: SoilCare? = null
    var soilPrediction: List<Pair<String, Float>> = listOf()
    var soilMax: Pair<String, Float>? = null
    var total = 0
    var soil: SoilParameters? = null
    var geo: GeoParameters? = null
    val tanaman: MutableMap<Int?, MutableList<Plant?>?> =
        HashMap<Int?, MutableList<Plant?>?>()

    //{score: [{nama tanaman: response rag}, {...}]}
    fun put(score: Int, plant: Plant?) {
        tanaman.computeIfAbsent(score) { k: kotlin.Int? -> java.util.ArrayList<Plant?>() }!!
            .add(plant)
        total++
    }
}
