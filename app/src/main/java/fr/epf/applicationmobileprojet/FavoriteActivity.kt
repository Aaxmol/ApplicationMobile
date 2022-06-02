package fr.epf.applicationmobileprojet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.epf.applicationmobileprojet.api.LocalisationStations
import fr.epf.applicationmobileprojet.model.Station
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FavoriteActivity : AppCompatActivity() {

    private var dbHandler: DBHandler? = null
    var stationFavorite: List<String?>? = null
    val stationList: MutableList<Station> = mutableListOf()

    private var stationAdapter : StationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val recyclerView =
            findViewById<RecyclerView>(R.id.list_stations_recyclerview)

        synchroApi()

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        dbHandler = DBHandler(this@FavoriteActivity)
        stationFavorite = dbHandler!!.getAllStations()
        stationAdapter = StationAdapter(stationFavorite as List<String>, stationList)
        recyclerView.adapter = stationAdapter
    }

    fun synchroApi() {
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
        val service = retrofit.create(LocalisationStations::class.java)

        runBlocking {
            val result = service.getStations()
            val stationVelib = result.data.stations
            stationVelib.map {
                val (station_id, name, lat, lon, capacity) = it
                Station(
                    station_id, name, lat, lon, capacity
                )
            }

                .map {
                    stationList.add(it)
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.display_map_action -> displayMap()
            R.id.display_favoris_action -> displayFavoris()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayFavoris() {
        val intent = Intent(this, FavoriteActivity::class.java)
        startActivity(intent)    }

    private fun displayMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}