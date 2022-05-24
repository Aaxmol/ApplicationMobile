package fr.epf.applicationmobileprojet

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.drawToBitmap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.epf.applicationmobileprojet.api.LocalisationStations
import fr.epf.applicationmobileprojet.api.StationService
import fr.epf.applicationmobileprojet.model.DetailStation
import fr.epf.applicationmobileprojet.model.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.Serializable

class DetailsStationActivity :
    AppCompatActivity(),
    Serializable
    {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_station)
        val stationId = intent.getIntExtra("station_id", -1).toString()
        val stationName: String? = intent.getStringExtra("station_name")
        //val stations: MutableList<Station> = intent.getPa("list") as MutableList<Station>


        val ID =
            findViewById<TextView>(R.id.details_id_textview)
        ID.text = stationId

        val Name =
            findViewById<TextView>(R.id.details_station_name_textview)
        Name.text = stationName

        synchroApi(stationId)

        /*---------------Apres que y'a favori, voir faire if avec des tag ---------------------------*/

        val addFavori = findViewById<ImageView>(R.id.addFavori)
        addFavori.setTag(R.drawable.ic_baseline_favorite_border)
        Log.d("lol", "${addFavori.background} -- ${addFavori.resources.hashCode()} -- ${addFavori.id.hashCode()}-- ${addFavori.drawable.hashCode()}")
        //Log.d("lol", "${addFavori.background.hashCode()} -- ${addFavori.resources.hashCode()} -- ${addFavori.drawable.hashCode()}")
        Log.d("lol", "${getDrawable(R.drawable.ic_baseline_favorite_border)} -- ${R.drawable.ic_baseline_favorite_border.hashCode()} -- ${R.drawable.ic_baseline_favorite_border}")// -- ${R.drawable.ic_baseline_favorite_border.hashCode()}
        addFavori.setOnClickListener(){




        /*if (addFavori.resources.hashCode() == R.drawable.ic_baseline_favorite_border.hashCode()) {
                addFavori.setImageResource(R.drawable.ic_baseline_favorite)
                Log.d("lol", "iflol ${addFavori.drawable.hashCode()} ")
            }
            else{
                addFavori.setImageResource(R.drawable.ic_baseline_favorite_border)
                Log.d("lol", "elselol ${addFavori.resources.hashCode()} --- ${R.drawable.ic_baseline_favorite_border.hashCode()} ")
            }*/
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basic,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.display_map_action -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun synchroApi(stationId: String) {

        /*--------------------------------CONNEXION JSON STATION------------------------------*/
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://velib-metropole-opendata.smoove.pro")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        val service = retrofit.create(StationService::class.java)

        runBlocking {
            val result = service.getDetailStations()
            Log.d(ContentValues.TAG, "synchroApi: ${result}")
            val stationVelib = result.data.stations
            stationVelib.map {
                val (stationCode, station_id, numBikesAvailable, num_bikes_available_types, numDocksAvailable) = it
                val (mechanical, ebike) = num_bikes_available_types

                if (stationId == station_id.toString()) {
                    val mechanical2 = mechanical.mechanical.toString()
                    val ebike2 = ebike.ebike.toString()
                    DetailStation(
                        stationCode, station_id, numBikesAvailable, mechanical2.toInt(), ebike2.toInt(),
                        numDocksAvailable, false
                    )

                    val Code =
                        findViewById<TextView>(R.id.details_code_textview)
                    Code.text = it.stationCode
                    val nbVeloLibre =
                        findViewById<TextView>(R.id.details_veloLibre_textview)
                    nbVeloLibre.text = it.numBikesAvailable.toString()
                    val Meca =
                        findViewById<TextView>(R.id.details_station_velo_bleu_textview)
                    Meca.text = mechanical2
                    val Elec =
                        findViewById<TextView>(R.id.details_station_velo_vert_textview)
                    Elec.text = ebike2
                    val nbPlaceLibre =
                        findViewById<TextView>(R.id.details_placeLibre_textview)
                    nbPlaceLibre.text = it.numDocksAvailable.toString()

                }
            }
        }
    }
}





