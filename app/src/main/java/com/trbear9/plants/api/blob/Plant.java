package com.trbear9.plants.api.blob;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.trbear9.plants.api.blob.PlantCare;
import com.trbear9.plants.api.blob.ProductSystem;

import org.apache.commons.csv.CSVRecord;

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
    @JsonIgnore
    public JsonNode taxon;
    private String fullsize, thumbnail;
    @JsonProperty("nama_ilmiah")
    private String nama_ilmiah;
    @JsonProperty("common_names")
    private String common_names;
    private String family;
    private String ph, temp;
    private String genus;
    private String kingdom;
    private String link;
    private String kategori;
    @JsonProperty("min_panen")
    private int min_panen;
    @JsonProperty("max_panen")
    private int max_panen;
    @JsonProperty("plant_care")
    private PlantCare plantCare;

    private String difficulty;
    private String description;

    @JsonProperty("product_system")
    private ProductSystem productSystem;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("prune_guide")
    private String pruneGuide;
}

