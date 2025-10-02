package com.trbear9.plants.api.blob

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.io.Serializable

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class Plant : Serializable {
    var name: String? = null

    @JsonIgnore
    var taxon: JsonNode? = null
    var fullsize: String? = null
    var thumbnail: String? = null

    @JsonProperty("nama_ilmiah")
    var nama_ilmiah: String? = null

    @JsonProperty("common_names")
    var common_names: String? = null
    var family: String? = null
    var ph: String? = null
    var temp: String? = null
    var genus: String? = null
    var kingdom: String? = null
    var link: String? = null
    var kategori: String? = null

    @JsonProperty("min_panen")
    var min_panen = 0

    @JsonProperty("max_panen")
    var max_panen = 0

    @JsonProperty("plant_care")
    var plantCare: PlantCare? = null

    var difficulty: String? = null
    var description: String? = null

    @JsonProperty("product_system")
    var productSystem: ProductSystem? = null

    @JsonProperty("common_name")
    var commonName: String? = null

    @JsonProperty("prune_guide")
    var pruneGuide: String? = null
}

