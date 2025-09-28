package com.tbear9.openfarm

import android.content.res.AssetManager
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.trbear9.plants.E.CATEGORY.*
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object Util {
    fun debug(message: String) {
        Log.d("OpenFarm", message)
    }

    @Throws(IOException::class)
    fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer? {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.getFileDescriptor())
        val fileChannel = inputStream.getChannel()
        val startOffset = fileDescriptor.getStartOffset()
        val declaredLength = fileDescriptor.getDeclaredLength()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun diffToColor(diff: String): Color {
        return when (diff) {
            "EASY" -> Color.Green
            "MEDIUM" -> Color.Yellow
            "HARD" -> Color.Red
            else -> Color.Gray
        }
    }

    fun scoreToColor(score: Int): Color {
        return when (score) {
            in 0..2 -> Color.Red
            in 3..6 -> Color.Yellow
            in 7..354 -> Color.Green
            else -> Color.Blue
        }
    }

        fun categoryToColor(category: String): Color {
            return when (category) {
                other.head -> Color.Gray
                vegetables.head -> Color.Green
                cereals_pseudocereals.head -> Color.Yellow
                roots_tubers.head -> Color.Red
                forage_pastures.head -> Color.Blue
                fruit_nut.head -> Color(0xFF9C27B0)
                materials.head -> Color(0xFFFF9100)
                ornamentals_turf.head -> Color(0xFFE91E63)
                medicinals_and_armoatic.head -> Color(0xFF936123)
                else -> Color(0xFF9E9E9E)
            }
        }

        fun translateCategory(category: String): String {
            when (category) {
                other.head -> return "Lainnya"
                vegetables.head -> return "Sayur"
                cereals_pseudocereals.head -> return "Pseudocereal"
                roots_tubers.head -> return "Akar/Umbi"
                forage_pastures.head -> return "Padang rumput"
                fruit_nut.head -> return "Buah & kacang"
                materials.head -> return "Bahan"
                ornamentals_turf.head -> return "Rumput hias"
                medicinals_and_armoatic.head -> return "Obat & aromatik"
                forest_or_wood.head -> return "Hutan/Kayu"
                cover_crop.head -> return "Tanaman penutup"
                environmental.head -> return "Lingkungan"
                weed.head -> return "Gulma"
            }
            return "Lainnya"
        }
}
