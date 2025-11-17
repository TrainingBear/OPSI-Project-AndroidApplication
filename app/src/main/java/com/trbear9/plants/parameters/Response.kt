package com.trbear9.plants.parameters

import com.trbear9.plants.parameters.blob.Plant
import com.trbear9.plants.parameters.blob.SoilCare
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
    var numericDepth: Int = 150
    var progress: Int = 0
    var target: Int = 0
    var predicted = false
    var parameterLoaded = false
    var current = "Tanaman"
    var loaded = false
    var hashCode: String? = null
    var soilName: String? = null
    var soilCare: SoilCare? = null
    var soilPrediction: List<Pair<String, Float>> = listOf()
    var soilMax: Pair<String, Float>? = null
    var total = 0
    var soil: SoilParameters? = null
    var geo: GeoParameters? = null
    val tanaman: MutableMap<Int, MutableSet<String>> =
        HashMap<Int, MutableSet<String>>()

    fun put(score: Int, plant: Plant) {
        tanaman.computeIfAbsent(score) { k: Int? -> mutableSetOf<String>() }
            .add(plant.nama_ilmiah)
        total++
    }
}
