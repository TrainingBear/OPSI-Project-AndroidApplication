package com.trbear9.plants.api.blob;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plant implements Serializable {
    public JsonNode full_taxon;

    public String fullsize, thumbnail;
    @JsonProperty("nama_ilmiah")
    public String nama_ilmiah;
    @JsonProperty("common_names")
    public String common_names;
    public String family;
    public String ph, temp;
    public String genus;
    public String kingdom;
    public String taxon;
    public String kategori;
    @JsonProperty("min_panen")
    public int min_panen;
    @JsonProperty("max_panen")
    public int max_panen;
    @JsonProperty("plant_care")
    public PlantCare plantCare;

    public String difficulty;
    public String description;

    @JsonProperty("product_system")
    public ProductSystem productSystem;

    @JsonProperty("common_name")
    public String commonName;

    @JsonProperty("prune_guide")
    public String pruneGuide;
}

