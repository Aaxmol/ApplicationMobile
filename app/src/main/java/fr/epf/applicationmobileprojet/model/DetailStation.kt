package fr.epf.applicationmobileprojet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailStation (
    val codeStation: String,
    var station_id: Long,
    val numBikesAvailable: Int,
    val mechanical: Int,
    val ebike: Int,
    val numDocksAvailable: Int,
    val favori: Boolean
    ) : Parcelable{
        companion object {
            fun bdd(nb : Double) =
                (1..100).map {
                    DetailStation(
                        "lol",200, it, it, it , it, false
                    )
                }

        }
}