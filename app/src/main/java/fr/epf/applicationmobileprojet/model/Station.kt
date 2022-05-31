package fr.epf.applicationmobileprojet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Station(
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int

) : Parcelable {
    companion object {
        fun bdd(nb : Int) =
            (1..100).map {
                Station(
                    200,"nom$it", 30.20, 20.30, it
                )
            }

    }
}