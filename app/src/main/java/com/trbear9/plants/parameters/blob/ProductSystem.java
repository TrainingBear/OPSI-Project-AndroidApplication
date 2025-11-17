package com.trbear9.plants.parameters.blob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSystem implements Serializable {
    public String rumah_tangga;
    public String komersial;
    public String industri;
}

