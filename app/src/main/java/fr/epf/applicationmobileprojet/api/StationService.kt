package fr.epf.applicationmobileprojet.api

import retrofit2.http.GET
import retrofit2.http.Query


interface StationService {

    @GET("opendata/Velib_Metropole/station_status.json/")
    suspend fun getDetailStations(@Query("stations") size : Int = 1) : Data
}

data class Data(val data: GetStationsResult)
data class GetStationsResult(val stations : List<DetailStation>)
data class DetailStation(val stationCode: String, val station_id: Long, val numBikesAvailable: Int, val num_bikes_available_types: List<num_bikes_available_types>, val numDocksAvailable: Int)
data class num_bikes_available_types(val mechanical: Int, val ebike: Int)