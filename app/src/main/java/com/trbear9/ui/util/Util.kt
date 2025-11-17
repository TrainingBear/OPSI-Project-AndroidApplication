package com.trbear9.ui.util

import android.content.res.AssetManager
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.trbear9.plants.E.CATEGORY.cereals_pseudocereals
import com.trbear9.plants.E.CATEGORY.cover_crop
import com.trbear9.plants.E.CATEGORY.environmental
import com.trbear9.plants.E.CATEGORY.fruit_nut
import com.trbear9.plants.E.CATEGORY.materials
import com.trbear9.plants.E.CATEGORY.medicinals_and_armoatic
import com.trbear9.plants.E.CATEGORY.ornamentals_turf
import com.trbear9.plants.E.CATEGORY.other
import com.trbear9.plants.E.CATEGORY.roots_tubers
import com.trbear9.plants.E.CATEGORY.vegetables
import com.trbear9.plants.E.CATEGORY.weed
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

fun String.info(tag: String) = Log.i(tag, this)
fun String.debug(tag: String) = Log.d(tag, this)
fun String.warn(tag: String) = Log.w(tag, this)
fun String.error(tag: String) = Log.e(tag, this)

object Util {
    fun debug(message: String) {
        Log.d("OpenFarm", message)
    }

    @Throws(IOException::class)
    fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer? {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
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
            cereals_pseudocereals.head -> Color(0xFF4CAF50)
            roots_tubers.head -> Color.Red
            "forage/pasture" -> Color.Blue
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
            "forage/pasture" -> return "Pakan ternak"
            fruit_nut.head -> return "Buah & kacang"
            materials.head -> return "Bahan"
            ornamentals_turf.head -> return "Rumput hias"
            medicinals_and_armoatic.head -> return "Obat & aromatik"
            "forest/wood" -> return "Hutan/Kayu"
            cover_crop.head -> return "Tanaman penutup"
            environmental.head -> return "Lingkungan"
            weed.head -> return "Gulma"
        }
        return "Lainnya"
    }

    fun translateClimate(climate: String): String {
        return when (climate) {
            "tropical wet & dry (Aw)" -> "tropis basah & kering (Aw)"
            "tropical wet (Ar)" -> "tropis kering"
            "steppe or semiarid (Bs)" -> "steppe or semiarid (Bs)" // flag
            "subtropical humid (Cf)" -> "subtropis basah"
            "subtropical dry summer (Cs)" -> "mediterania"
            "subtropical dry winter (Cw)" -> "subtropis lembab"
            "temperate oceanic (Do)" -> "maritim"
            "temperate continental (Dc)" -> "temperate continental (Dc)"
            "temperate with humid winters (Df)" -> "temperate with humid winters (Df)"
            "temperate with dry winters (Dw)" -> "dingin"
            "desert or arid (Bw)" -> "gurun"
            "boreal (E)" -> "taiga"
            "polar (F)" -> "kutub"
            else -> ""
        }
    }
    fun getCategory(): List<String>{
        return listOf("Lainya", "Sayur", "Pseudocereal", "Akar/Umbi", "Pakan ternak",
            "Buah & kacang", "Bahan", "Rumput hias", "Obat & aromatik", "Hutan/Kayu",
            "Tanaman penutup", "Lingkungan", "Gulma"
            )
    }
}
