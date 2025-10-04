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
import com.trbear9.plants.CsvHandler
import com.trbear9.plants.E.*
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import lombok.Getter
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import java.io.IOException

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

    fun search(query: String): Set<String>{
        return tags.filter {
            it.startsWith(query) ||  it.contains(query)
        }.toSet()
    }

    val tags = mutableSetOf<String>()
    var kew : Map<String, JsonNode>? = null
    fun load(context: Context){
        CsvHandler.load(context)
        runBlocking {
            withContext(Dispatchers.IO) {
                val plants = context.assets.open("plants.json").use {
                    objectMapper.readValue(it, Array<Plant>::class.java)
                }
                for (item in plants) {
                    plant[item.nama_ilmiah] = item
                }
                Log.d("Data Processor", "Loaded ${plant.size} plants")
                kew = context.assets.open("KEW.json").use {
                    val type = object : TypeReference<Map<String, JsonNode>>() {}
                    objectMapper.readValue(it, type)
                }
            }
        }
        CsvHandler.ecocropcsv?.forEach {
            loadPlant(context, it)
        }
    }

    val namaUmumToNamaIlmiah = mutableMapOf<String, String>()
    val namaIlmiahToNamaUmum = mutableMapOf<String, String>()

    val plant = mutableMapOf<String, Plant>()
    var plantByTag = SnapshotStateMap<String, MutableSet<String>>()
    val ecocrop = mutableMapOf<String, CSVRecord>()
    fun loadPlant(context:Context, record: CSVRecord, load: Boolean = false): Plant {
        val name = record[Science_name]
        if(plant.containsKey(name) && !load) return plant[name]!!
        val plant = try {
            context.assets.open("plants/$name.json").use {
                ObjectMapper().readValue(it, Plant::class.java)
            }
        } catch (_: IOException){
            Log.e("Data Processor", "Full plant version of $name not found")
            val plant = Plant()
            plant.commonName = name ?: "Tidak diketahui"
            plant
        }
        plant.nama_ilmiah = name
        record[Common_names]?.split(", ")?.forEach {
            plant.nama_umum.add(it)
            tags += it
            plantByTag.computeIfAbsent(it){
                key -> mutableSetOf<String>()
            }.add(name)
        }
        record[Category]?.split(", ")?.forEach {
            plant.category.add(it)
            tags += it
            plantByTag.computeIfAbsent(it){
                    key -> mutableSetOf<String>()
            }.add(name)
        }
        writeTaxonomy(plant)
        tags += name
        tags += plant.commonName

        namaUmumToNamaIlmiah[plant.commonName] = name
        namaIlmiahToNamaUmum[name] = plant.commonName

        this.plant[name] = plant
        Log.d("Data Processor", "Loaded ${plant.commonName} plants")
        return plant
    }

    @Throws(IOException::class)
    fun process(context: Context, data: UserVariable, soilResult: SoilResult): Flow<Response> = flow {
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
        }
        data.soil.let {
            it.texture = resultSoil!!.texture;
            it.fertility = resultSoil.fertility;
            it.drainage = resultSoil.drainage;
            it.pH = it.pH ?: resultSoil.pH

            response.soil = it
        }
        Log.d("Data Processor", "DONE")
        response.parameterLoaded = true
        emit(response)

        val processedData = CsvHandler.process(data)
        for (i in processedData.keys)
            response.target += processedData[i]!!.size
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                val namaIlmiah = ecorecord.get(Science_name)
                response.current = namaIlmiah
                emit(response)
                if (!ecocrop.containsKey(namaIlmiah))
                    ecocrop[namaIlmiah] = ecorecord
                response.put(i, loadPlant(context, ecorecord))
                response.progress++
                emit(response)
            }
        }
        response.loaded = true
        soilResult.plants = mutableStateMapOf()
        soilResult.plantByCategory = mutableStateMapOf()

        for (score in response.tanaman.keys) {
            Log.d("Data Processor", "Loaded $score with size ${response.tanaman[score]!!.size}")
            soilResult.plants!![score] = response.tanaman[score]?: mutableSetOf()
        }
        soilResult.plantByCategory!!["All"] = soilResult.plants!!
        soilResult.plantByCategory!!.clear()
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
            } ?: Log.d("Data Processor","$kat is null")
        }
        emit(response)
    }.flowOn(Dispatchers.IO)

    private fun writeTaxonomy(plant: Plant) {
        plant.genus = plant.nama_ilmiah?.split(" ")[0]
        if(kew?.containsKey(plant.nama_ilmiah) == true){
            plant.full_taxon = kew!![plant.nama_ilmiah]
        }
        else{
            log.warn("No taxonomy found for ${plant.nama_ilmiah}")
        }
    }

    @OptIn(com.openmeteo.api.common.Response.ExperimentalGluedUnitTimeStepValues::class)
    fun meteo(geo: GeoParameters) {
        var max = 0.0
        var min = 0.0
        var elevation = 0f
        val meteo = OpenMeteo(geo.latitude.toFloat(), geo.longtitude.toFloat())
        val temperatur = meteo.forecast(){
            latitude = geo.latitude.toFloat()
            longitude = geo.longtitude.toFloat()
            temperatureUnit = TemperatureUnit.Celsius
            elevation
            startDate = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 60)
            endDate = Date(System.currentTimeMillis())
            daily = Forecast.Daily{
                listOf(temperature2mMin, temperature2mMax)
            }
        }.getOrThrow()
        Forecast.Daily.run {
            temperatur.daily.getValue(temperature2mMax).run {
                for (m in values.values)
                    max+= m?:28.0
                max/= values.size
            }
            temperatur.daily.getValue(temperature2mMin).run {
                for (m in values.values)
                    min+= m?:20.0
                min/= values.size
            }
        }

        val MPDL = meteo.elevation {
            latitude = (-7.257281798437764).toString()
            longitude = 110.4031409940034.toString()
        }.getOrThrow()
        for (f in MPDL.elevation) {
            elevation = f
        }
        elevation/=MPDL.elevation.size
        geo.altitude = elevation.toDouble()
        geo.min =  min
        geo.max = max
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