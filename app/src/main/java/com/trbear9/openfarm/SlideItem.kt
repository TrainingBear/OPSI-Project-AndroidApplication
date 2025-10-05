package com.trbear9.openfarm

/**
 * Data model untuk 1 slide di ViewPager
 */
data class SlideItem(
    val imageRes: Int,      // resource gambar (R.drawable.xxx)
    val title: String,      // judul slide (misal: "Step 1")
    val description: String // deskripsi (misal: "Nyalakan izin kamera")
)
