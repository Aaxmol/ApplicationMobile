package fr.epf.applicationmobileprojet.api

import retrofit2.http.GET
import retrofit2.http.Query


interface LocalisationStations {

    @GET("/opendata/Velib_Metropole/station_information.json")
    suspend fun getStations(@Query("stations") size : Int = 1) : Data2
}

data class Data2(val data: GetStationsResult2)
data class GetStationsResult2(val stations : List<Station>)
data class Station(val station_id: Long, val name: String, val lat: Double, val lon: Double, val capacity: Int)