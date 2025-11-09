package com.trbear9.internal

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.time.Date
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.openfarm.Util
import com.trbear9.openfarm.activities.SoilResult
import com.trbear9.openfarm.debug
import com.trbear9.openfarm.getLocation
import com.trbear9.openfarm.info
import com.trbear9.plants.CsvHandler
import com.trbear9.plants.E.Category
import com.trbear9.plants.E.Common_names
import com.trbear9.plants.E.Science_name
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
//import com.trbear9.plants.save
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import lombok.Getter
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutput
import java.io.ObjectOutputStream

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Getter
object Data {
    fun who(): String {
        return """
                            
                                        TIM OPSI SMANEGA 2025
                                        oleh Kukuh & Refan.
                RestAPI has been built by kujatic (trbear) -> https://github.com/TrainingBear (opensource)
                BIG SHOUTOUT TO JASPER                vvvvvvvvvvvvvvvvvvvvvvvvv
                Join komunitas discord kami (Jasper): https://discord.gg/fbAZSd3Hf2
                
                
                """.trimIndent()
    }

    fun searchByCommonName(
        max: Int = Int.MAX_VALUE,
        query: String,
        consumer: (String) -> Unit = {}
    ): Set<String> {
        val result = mutableSetOf<String>()
        var i = 0
        for (tag in namaUmumToNamaIlmiah.keys) {
            val prefix = query.lowercase()
            val target = tag.lowercase()
            if (target != null && (target.startsWith(prefix) ||
                        target.contains(prefix))
                ) {
                result.add(tag)
                consumer(tag)
                i++
                if (i >= max) break
            }
        }
        return result
    }

    fun searchByScienceName(
        max: Int = Int.MAX_VALUE,
        query: String,
        consumer: (String) -> Unit = {}
    ): Set<String> {
        val result = mutableSetOf<String>()
        var i = 0
        for (tag in namaIlmiahToNamaUmum.keys){
            val prefix = query.lowercase()
            val target = tag.lowercase()
            if (target != null && (target.startsWith(prefix) ||
                        target.contains(prefix))
                ) {
                result.add(tag)
                consumer(tag)
                i++
                if (i >= max) break
            }
        }
        return result
    }

    val tags = mutableSetOf<String>()
    var kew = mutableMapOf<String, JsonNode?>()
    val namaUmumToNamaIlmiah = mutableMapOf<String, String>()
    val namaIlmiahToNamaUmum = mutableMapOf<String, String>()

    val plant = mutableMapOf<String, Plant>()
    val normalize = mutableMapOf<String, String>()
    var plantByTag = mutableMapOf<String, MutableSet<String>>()
    val ecocrop = mutableMapOf<String, CSVRecord>()
    fun load(context: Context) {
        CsvHandler.load(context)
        CsvHandler.ecocropcsv?.forEach {
            ecocrop[it[Science_name]] = it
        }
        TFService.load(context)
        context.assets.list("serialized")?.forEach {
            if(it!="CSV" && it!="ecocrop.ser") {
                val field = this::class.java.getDeclaredField(it.replace(".ser", ""))
                field.isAccessible = true

                field.set(
                    this, ObjectInputStream(
                        context.assets.open("serialized/$it")
                    ).readObject()
                )
                "Deserialized $it".info("Data Processor")
            }
        }
    }

    fun preLoad() {
//        CsvHandler.preload()
//        TFService.load(context)
        val plants = File("plants.json").inputStream().use {
            objectMapper.readValue(it, Array<Plant>::class.java)
        }
        for (item in plants) {
            plant[item.nama_ilmiah] = item
        }
//        Log.d("Data Processor", "Loaded ${plant.size} plants")

        File("KEW.json").inputStream().use {
            val type = object : TypeReference<Map<String, JsonNode>>() {}
            val k = objectMapper.readValue(it, type)
            for (entry in k) {
                val name = entry.key
                val ke = k[name]
                kew[name] = ke
            }
        }
        CsvHandler.ecocropcsv?.forEach {
            if (!ecocrop.containsKey(it[Science_name]))
                ecocrop[it[Science_name]] = it
            loadPlant(it, true)
        }
//        Log.d("Data Processor", "Loaded ${ecocrop.size} ecocrop")
//        Log.d("Data Processor", "Loaded ${kew.size} kew")
//        Log.d("Data Processor", "Loaded ${plant.size} plants")
//        Log.d("Data Processor", "Loaded ${namaUmumToNamaIlmiah.size} namaUmumToNamaIlmiah")
//        Log.d("Data Processor", "Loaded ${namaIlmiahToNamaUmum.size} namaIlmiahToNamaUmum")
//        Log.d("Data Processor", "Loaded ${tags.size} Tags size")

        for (field in Data::class.java.declaredFields) {
            try {
//                ObjectOutputStream(save("${field.name}.ser").outputStream())
//                    .use{it.writeObject(field.get(this))}
            } catch (_: Throwable){
//                error("Failed to serialize ${field.name}")
                continue
            }
            println("Serialized ${field.name}")
//            "Serialized ${field.name}".info(this::class.simpleName.toString())
        }
    }

    fun loadPlant(record: CSVRecord, load: Boolean = false): Plant {
        val ilmiah = record[Science_name]
        if (plant.containsKey(ilmiah) && !load) return plant[ilmiah]!!

        val plant = if (plant[ilmiah] == null) {
//            Log.e("Data Processor", "Full plant version of $ilmiah not found")
            val plant = Plant()
            plant.commonName = ilmiah ?: "Tidak diketahui"
            plant
        } else plant[ilmiah]!!

        plant.nama_ilmiah = ilmiah
        record[Common_names]?.split(", ")?.forEach {
            plant.nama_umum.add(it)
            plantByTag.computeIfAbsent(it) { key ->
                mutableSetOf<String>()
            }.add(ilmiah)
        }
        record[Category]?.split(", ")?.forEach {
            plant.category.add(it)
            tags += it.lowercase()
            plantByTag.computeIfAbsent(it) { key ->
                mutableSetOf<String>()
            }.add(ilmiah)
            plantByTag.computeIfAbsent(Util.translateCategory(it)) { key ->
                mutableSetOf<String>()
            }.add(ilmiah)
            plantByTag.computeIfAbsent("All") { key ->
                mutableSetOf<String>()
            }.add(ilmiah)
        }
        writeTaxonomy(plant)
        tags += ilmiah.lowercase()
        val commonname = plant.commonName?.lowercase() ?: ilmiah
        tags += commonname

        namaUmumToNamaIlmiah[commonname.lowercase()] = ilmiah.lowercase()
        namaIlmiahToNamaUmum[ilmiah.lowercase()] = commonname

        this.plant[ilmiah] = plant
        normalize[ilmiah.lowercase()] = ilmiah
//        Log.d("Data Processor", "Loaded ${plant.commonName} plants")
        return plant
    }

    @Throws(IOException::class)
    fun process(data: UserVariable, soilResult: SoilResult): Flow<Response> = flow {
        val response = Response()
        val prediction = TFService.predict(data.image!!)

        val max = TFService.argmax(prediction)
        val resultSoil = TFService.soils[max.first]
        response.soilMax = max
        response.soilPrediction = TFService.getPredictions(prediction)
        response.predicted = true
        emit(response)

        Log.d("Data Processor", "Getting temperature..")
        data.geo.let {
            meteo(it)
            response.geo = data.geo
            response.parameterLoaded = true
            emit(response)
        }
        data.soil.let {
            it.texture = resultSoil!!.texture;
            it.fertility = resultSoil.fertility;
            it.drainage = resultSoil.drainage;
            it.pH = it.pH ?: resultSoil.pH

            response.soil = it
        }
        Log.d("Data Processor", "DONE")

        val processedData = CsvHandler.process(data)
        for (i in processedData.keys)
            response.target += processedData[i]!!.size
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                val namaIlmiah = ecorecord.get(Science_name)
                response.current = namaIlmiah
                emit(response)
                response.put(i, loadPlant(ecorecord))
                response.progress++
                emit(response)
            }
        }
        response.loaded = true
        soilResult.plants = mutableStateMapOf()
        soilResult.plantByCategory = mutableStateMapOf()
        for (score in response.tanaman.keys) {
            Log.d("Data Processor", "Loaded $score with size ${response.tanaman[score]!!.size}")
            soilResult.plants!![score] = response.tanaman[score] ?: mutableSetOf()
        }
        soilResult.plantByCategory!!["All"] = soilResult.plants!!
        soilResult.plants!!.forEach { (score, list) ->
            list.forEach { name ->
                plant[name]?.category
                    ?.forEach { kat ->
                        soilResult.plantByCategory!!.computeIfAbsent(
                            Util.translateCategory(
                                kat
                            )
                        ) { mutableStateMapOf() }
                            .computeIfAbsent(score) { mutableSetOf<String>() }
                            .add(name)
                    }
            }
        }
        Util.getCategory().forEach { kat ->
            soilResult.plantByCategory!![kat]?.let {
                Log.d("Data Processor", "Adding $kat with size ${it.size}")
            } ?: Log.d("Data Processor", "$kat is null")
        }
        Log.d("Data Processor", "DONE with result of ${response.target} tanaman")
        emit(response)
    }.flowOn(Dispatchers.IO)


    private fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah?.split(" ")[0]
        if (kew?.containsKey(plant.nama_ilmiah) == true) {
            plant.full_taxon = kew!![plant.nama_ilmiah]
        } else {
            log.warn("No taxonomy found for ${plant.nama_ilmiah}")
        }
    }

    fun pupuk(category: String) {
    }

    @OptIn(com.openmeteo.api.common.Response.ExperimentalGluedUnitTimeStepValues::class)
    fun meteo(geo: GeoParameters) {
        var max = 0.0
        var min = 0.0
        var elevation = 0f
        val meteo = OpenMeteo(geo.latitude.toFloat(), geo.longtitude.toFloat())
        val temperatur = meteo.forecast() {
            latitude = geo.latitude.toFloat()
            longitude = geo.longtitude.toFloat()
            temperatureUnit = TemperatureUnit.Celsius
            elevation
            startDate = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 5)
            endDate = Date(System.currentTimeMillis())
            daily = Forecast.Daily {
                listOf(temperature2mMin, temperature2mMax)
            }
        }.getOrThrow()
        Forecast.Daily.run {
            temperatur.daily.getValue(temperature2mMax).run {
                for (m in values.values)
                    max += m ?: 28.0
                max /= values.size
            }
            temperatur.daily.getValue(temperature2mMin).run {
                for (m in values.values)
                    min += m ?: 20.0
                min /= values.size
            }
        }

        val MPDL = meteo.elevation {
            latitude = (-7.257281798437764).toString()
            longitude = 110.4031409940034.toString()
        }.getOrThrow()
        for (f in MPDL.elevation) {
            elevation = f
        }
        elevation /= MPDL.elevation.size
        geo.altitude = elevation.toDouble()
        geo.min = min
        geo.max = max
        "long: ${geo.longtitude} lat: ${geo.latitude} mdpl: ${geo.altitude}".debug("Data Processor")
        "temperatur: $min, $max with elevation: $elevation".debug("Data Processor")
    }

    val log: org.slf4j.Logger = LoggerFactory.getLogger(Data::class.java)!!
    private val objectMapper = ObjectMapper()

    init {
        objectMapper.factory.setStreamReadConstraints(
            StreamReadConstraints.builder()
                .maxStringLength(1_000_000_000)
                .build()
        ).setStreamWriteConstraints(
            StreamWriteConstraints.builder()
                .maxNestingDepth(1_000_000_000)
                .build()
        )
    }
}