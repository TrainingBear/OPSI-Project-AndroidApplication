package com.trbear9.internal

import android.content.Context
import com.fasterxml.jackson.core.StreamReadConstraints
import com.fasterxml.jackson.core.StreamWriteConstraints
import com.fasterxml.jackson.databind.ObjectMapper
import com.openmeteo.api.Forecast
import com.openmeteo.api.OpenMeteo
import com.openmeteo.api.common.time.Date
import com.openmeteo.api.common.units.TemperatureUnit
import com.trbear9.plants.CsvHandler
import com.trbear9.plants.E.*
import com.trbear9.plants.api.GeoParameters
import com.trbear9.plants.api.Response
import com.trbear9.plants.api.UserVariable
import com.trbear9.plants.api.blob.Plant
import lombok.Getter
import org.slf4j.LoggerFactory
import java.io.IOException

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Getter
class ServerHandler {
    fun who(): String {
        return """
                            
                                        TIM OPSI SMANEGA 2025
                                        oleh Kukuh & Refan.
                RestAPI has been built by kujatic (trbear) -> https://github.com/TrainingBear (opensource)
                BIG SHOUTOUT TO JASPER                vvvvvvvvvvvvvvvvvvvvvvvvv
                Join komunitas discord kami (Jasper): https://discord.gg/fbAZSd3Hf2
                
                
                """.trimIndent()
    }

    val plant = mutableMapOf<String, Plant>()
    fun loadPlant(context:Context, name:String): Plant{
        if(plant.containsKey(name)) return plant[name]!!
        val plant = context.assets.open("plants/$name.json").use {
            ObjectMapper().readValue(it, Plant::class.java)
        }
        this.plant[name] = plant
        return plant
    }

    @Throws(IOException::class)
    fun process(context: Context, data: UserVariable): Response {
        val prediction = TFService.predict(context, data.image!!)

        val response = Response()
        val max = TFService.argmax(prediction)
        val resultSoil = TFService.soils[max.first]
        response.soilMax = max
        response.soilPrediction = prediction

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

        val processedData = CsvHandler.process(data)
        var target = 0
        for (i in processedData.keys)
            target += processedData[i]!!.size
        for (i in processedData.keys) {
            for (ecorecord in processedData[i]!!) {
                val namaIlmiah = ecorecord.get(Science_name)
                var plant = Plant()

                plant.nama_ilmiah = namaIlmiah
                writeTaxonomy(context, plant)
                response.put(i, plant)
            }
        }
        return response
    }

    private fun writeTaxonomy(context:Context,plant: Plant) {
        plant.genus = plant.nama_ilmiah.split(" ")[0]
        try {
            context.assets.open("plants/${plant.nama_ilmiah}.json").use {
                plant.taxon = ObjectMapper().readTree(it)
                plant.link = "https://powo.science.kew.org/" + plant.taxon["url"]?.asText()
            }
            log.warn("No taxonomy found for ${plant.nama_ilmiah}")
            return
        } catch (e: IOException) {
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

    companion object {
        val log: org.slf4j.Logger = LoggerFactory.getLogger(ServerHandler::class.java)!!
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
}