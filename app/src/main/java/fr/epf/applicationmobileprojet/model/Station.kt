package fr.epf.applicationmobileprojet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Station(
    var station_id: Long,
    var name: String,
    val lat: Double,
    val lon: Double,
    var capacity: Int

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