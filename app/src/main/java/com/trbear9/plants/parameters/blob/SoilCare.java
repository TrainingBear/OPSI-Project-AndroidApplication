package com.trbear9.plants.parameters.blob;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SoilCare {
    @JsonProperty("pH_correction")
    public String pHCorrection;

    @JsonProperty("organic_matter")
    public String organicMatter;

    @JsonProperty("water_retention")
    public String waterRetention;

    @JsonProperty("nutrient_management")
    public NutrientManagement nutrientManagement;

}
