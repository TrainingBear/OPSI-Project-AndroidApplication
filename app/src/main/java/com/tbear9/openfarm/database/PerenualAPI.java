package com.tbear9.openfarm.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter

///  Free plan API (Personal Usage)
///  https://perenual.com/subscription-api-pricing
@SuppressWarnings({"UNMODIFIABLE_FINAL_FIELD", "JANGAN DI MODIFIKASI, AGAR TIDAK TERJADI EROR"})
public final class PerenualAPI {
    @Setter public static String key = "sk-fh6s685f9f720fc7b11089";
    public static final Set<Integer> plant_list_pages = new HashSet<>(); // PAGE 1 - 405 | SPECIES TANAMAN | TOTAL ADA 10k SPECIES
    public static final Set<Integer> plant_disease_list_pages = new HashSet<>(); // PAGE 1 - 8 | TANAMAN HAMA | TOTAL ADA 239 SPECIES
    public static final Set<Integer> plant_details = new HashSet<>(); // 1 - 10k+ | DETAIL TANAMAN | TOTAL ADA 10k DETAILS UNTUK SETIAP SPECIES
    public static final Set<Integer> plant_guide_pages= new HashSet<>(); // PAGE 1 - 405 | GUIDE TANAMAN | TOTAL ADA 10k PANDUAN UNTUK SETIAP SPECIES
    public static final Set<Integer> plant_hardiness = new HashSet<>(); // ID DARI SPECIES | HARDNESS TANAMAN | TOTAL ADA 10k HARDNESS UNTUK SETIAP SPECIES
}
