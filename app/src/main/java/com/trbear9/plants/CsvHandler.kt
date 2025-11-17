package com.trbear9.plants

import android.content.Context
import com.trbear9.ui.util.error
import com.trbear9.plants.parameters.Parameters
import com.trbear9.plants.parameters.UserVariable
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectOutputStream
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

private val dir = File("serialized").also{it.mkdirs()}
fun save(name: String): File = File(dir, name)

object CsvHandler {
    @JvmField
    var explored_fields: Int = 0
    var ecocropcsv: MutableList<CSVRecord>? = null
    var perawatan: MutableList<CSVRecord>? = null

    fun preload(){
        try {
            File("EcoCrop_DB.csv").inputStream().use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> ecocropcsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }

            File("Perawatan.csv").inputStream().use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> perawatan = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
        ObjectOutputStream(save("ecocropcsv.ser").outputStream()).use {
            it.writeObject(ecocropcsv)
        }
        ObjectOutputStream(save("perawatan.ser").outputStream()).use{
            it.writeObject(perawatan)
        }
    }

    fun load(context: Context) {
//        context.assets.list("serialized/CSV")?.forEach {
//            val field = this::class.java.getDeclaredField(it.replace(".ser", ""))
//            field.isAccessible = true
//
//            field.set(this, ObjectInputStream(
//                context.assets.open("serialized/CSV/$it")
//            ).readObject())
//
//            "Deserialized $it".info("CsvHandler")
//        }
        try {
            context.assets.open("EcoCrop_DB.csv").use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> ecocropcsv = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }

            context.assets.open("Perawatan.csv").use { `is` ->
                InputStreamReader(`is`).use {
                        reader -> perawatan = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).records
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    fun isAuthored(record: CSVRecord): Boolean {
        return record.get(E.Authority) != null || !record.get(E.Authority).isEmpty()
    }

    @JvmStatic
    fun getScienceName(record: CSVRecord): String {
        return record.get(E.Science_name)
    }

    @JvmStatic
    fun commonNames(record: CSVRecord): MutableSet<String?> {
        return HashSet<String?>(
            listOf(
                *record.get(E.Common_names).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
        )
    }

    @JvmStatic
    fun process(data: UserVariable): MutableMap<Int, MutableSet<CSVRecord>> {
        val map: MutableMap<Int, MutableSet<CSVRecord>> =
            TreeMap<Int, MutableSet<CSVRecord>>()
        val parameters = listOf(data.geo, data.custom, data.soil)
        if(ecocropcsv==null){
            throw Exception("EcoCrop_DB.csv not found, coba untuk load()")
        }

        for (record in ecocropcsv) {
            explored_fields++
            var score = 0
            var flag = false
            for (parameter in parameters) {
                if(parameter==null) continue
                val params = parameter.getParameters()
                for (col in params.keys) {
                    val paramVal = params[col]
                    paramVal?:continue
                    var floatVar = Float.Companion.MAX_VALUE
                    try { floatVar = paramVal.toFloat() }
                    catch (_: NumberFormatException) { }

                    when (col) {
                        "LAT" -> {
                            floatVar = abs(floatVar)
                            val min: Float
                            val max: Float
                            try {
                                min = record.get(E.A_minimum_latitude).toFloat()
                                max = record.get(E.A_maximum_latitude).toFloat()
                            }catch (_: NumberFormatException) {
                                continue
                            }
                            if (floatVar in min .. max) {
                                score += 1
                                flag = true
                            }
                        }

                        "ALT" -> {
                            val altitude: Float
                            try {altitude = record.get(E.O_maximum_altitude).toFloat()}
                            catch (_: NumberFormatException) {
                                continue
                            }
                            if (altitude >= floatVar) {
                                score += 1
                                flag = true
                            } else score -= (abs(altitude - floatVar)/2).toInt()
                        }

                        "RAIN" -> {
                            val min: Float
                            val max: Float
                            try {
                                min = record.get(E.A_minimum_rainfall).toFloat()
                                max = record.get(E.A_maximum_rainfall).toFloat()
                            } catch (_: NumberFormatException) {
                                continue
                            }
                            if (floatVar in min..max) {
                                score += 1
                                flag = true
                            } else {
                                val floatVar = floatVar // bias
                                score -= (floatVar.coerceIn(
                                    Math.min(min, max),
                                    Math.max(min, max)
                                ).absoluteValue).toInt()
                            }
                        }

                        "TEMPMAX" -> {
                            val max:Float
                            try {
                                max = record.get(E.A_maximum_temperature).toFloat()
                            } catch (_: NumberFormatException){
                                "Can't parse ${record.get(E.A_maximum_temperature)} to float (TEMPMAX) for ${record.get(E.Science_name)} alias ${Data.plant[record.get(E.Science_name)]?.commonName}".error("CsvHandler")
                                continue
                            }
                            if (max >= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(max - floatVar).toInt()
                        }

                        "TEMPMIN" -> {
                            val min:Float
                            try{
                                min = record.get(E.A_minimum_temperature).toFloat()
                            }catch (_: NumberFormatException){
                                "Can't parse ${record.get(E.A_minimum_temperature)} to float (TEMPMAX)".error("CsvHandler")
                                 continue
                            }
                            if (min <= floatVar) {
                                score += 1
                                flag = true
                            } else score -= abs(floatVar - min).toInt()
                        }

                        "PANEN" -> {
                            val min:Float
                            val max:Float
                            try {
                                min = record.get(E.MIN_crop_cycle).toFloat()
                                max = record.get(E.MAX_crop_cycle).toFloat()
                            }catch (_: NumberFormatException) {
                                continue
                            }
                            if (floatVar in min..max) {
                                score+=1
                                flag = true
                            }
                        }

                        "QUERY" -> {
                            if (record.get(E.Common_names).contains(paramVal)) {
                                score++
                                flag = true
                            }
                        }

                        "PH" -> {
                            val min: Float
                            val max:Float
                            getScienceName(record)
                            try {
                                min = record.get(E.A_minimum_ph).toFloat()
                                max = record.get(E.A_maximum_ph).toFloat()
                            } catch (_: NumberFormatException){
//                                Log.d("CSV $scienceName ", "pH not defined for $scienceName plant")
                                continue
                            }
                            if (floatVar in min..max) {
                                score += 2
                                flag = true
//                                Log.d("CSV $scienceName", "$scienceName actual pH: $floatVar, increased to $score")
                            }
                            else {
                                val absoluteValue = floatVar.coerceIn(
                                    Math.min(min, max),
                                    Math.max(min, max)
                                ).absoluteValue
                                score -= (absoluteValue * 2.7).toInt()
//                                Log.d("CSV $scienceName ", "$scienceName missPH: $absoluteValue, decreased to $score actual ph is $floatVar")
                            }
                        }

                        else -> {
                            val value = record.get(col)
                            if ((col == E.O_soil_texture || col == E.A_soil_texture) &&
                                record.get(col).contains("wide")
                            ) {
                                score += 3
                                flag = true
                                continue
                            }

                            if (value.contains(paramVal)) {
                                score += 1
                                flag = true
                                continue
                            }

                            flag = false
                            if (col == E.Climate_zone) {
                                score -= 354354
                                continue
                            }
                            if (col == E.A_soil_drainage || col == E.O_soil_drainage){
                                score -= 10
                                continue
                            }
                        }
                    }
                }
            }
            if (isAuthored(record) && flag && score > 0)
                map.computeIfAbsent(score) { k: Int? -> HashSet<CSVRecord>() }
                .add(record)
        }
        return map
    }

    fun perawatan(record: CSVRecord): CSVRecord? {
        if(perawatan==null){
            throw Exception("Perawatan.csv is not loaded! tolong load() sebelum digunakan")
        }
        for (i in perawatan) {
            val s = i.get(record.get(E.Science_name))
            if (s != null) return i
        }
        return null
    }

    fun qperawatan(record: CSVRecord): String {
        return record.get(E.PERAWATAN)
    }

    fun qpenyakit(record: CSVRecord): String {
        return record.get(E.PENYAKIT)
    }

    @Deprecated("")
    fun getRecord(parameters: Parameters): CSVRecord? {
        val par = parameters.getParameters()
        if(ecocropcsv==null){
            throw Exception("Ecocrop.csv is not loaded! tolong load() sebelum digunakan")
        }
        for (record in ecocropcsv) {
            var flag = false
            for (col in par.keys) {
                val `val` = par[col]
                if (`val` == null) continue
                if (record.get(col).contains(`val`)) {
                    flag = true
                }
            }
            if (flag) return record
        }
        return null
    }

    @JvmStatic
    fun getRecord(query: String, column: String?): CSVRecord? {
        if(ecocropcsv==null){
            throw Exception("Ecocrop.csv is not loaded! tolong load() sebelum digunakan")
        }
        for (i in ecocropcsv) {
//            log.info("checking {}", i.get(column));
            if (i.get(column).contains(query)) {
                return i
            }
        }
        return null
    }

    private val science_perawatancsv: MutableMap<String?, CSVRecord?> = HashMap<String?, CSVRecord?>()
    fun perawatanCsv(ecocropcsv: CSVRecord): CSVRecord? {
        if(perawatan==null){
            throw Exception("Perawatan.csv is not loaded! tolong load() sebelum digunakan")
        }
        if (science_perawatancsv.containsKey(ecocropcsv.get(E.Science_name)))
            return science_perawatancsv[ecocropcsv[E.Science_name]]
        for (i in perawatan) {
            if (i.get(E.Science_name).contains(ecocropcsv.get(E.Science_name))) {
                science_perawatancsv.put(ecocropcsv.get(E.Science_name), i)
                return i
            }
        }
        return null
    }
}
