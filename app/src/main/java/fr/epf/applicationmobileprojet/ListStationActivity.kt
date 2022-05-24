package fr.epf.applicationmobileprojet

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
/*import fr.epf.applicationmobileprojet.api.StationService*/
import fr.epf.applicationmobileprojet.model.Station
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val TAG = "ListStationActivity"
class ListStationActivity : AppCompatActivity() {

    val stations: MutableList<Station> = mutableListOf()

    private var stationAdapter : StationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_station)

        val recyclerView =
            findViewById<RecyclerView>(R.id.list_stations_recyclerview)


        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        //val stations = Station.bdd(40)
        stationAdapter = StationAdapter(stations)
        recyclerView.adapter = stationAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basic, menu)
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
        val intent = Intent(this, ListStationActivity::class.java)
        startActivity(intent)    }

    private fun displayMap() {
        /*val intent2 = Intent(this, MapsActivity::class.java)
        startActivity(intent2)*/
        synchroApi()
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


        /*
        val service = retrofit.create(StationService::class.java)



        runBlocking {
            val result = service.getStations()
            Log.d(ContentValues.TAG, "synchroApi: ${result}")
            val users = result.data.stations
            users.map {
                //ordre se refere à RandomUserService
                val (numBikesAvailable, numDocksAvailable) = it
                Station(
                    //ordre comme dans model
                    "nom Station", numBikesAvailable, numBikesAvailable,
                    numDocksAvailable, true
                )

            }

        }
            //emplacement à check
            .map {
                stations.add(it)
            }

        stationAdapter?.notifyDataSetChanged()
*/
    }

}