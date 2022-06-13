package fr.epf.applicationmobileprojet

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import fr.epf.applicationmobileprojet.api.StationService
import fr.epf.applicationmobileprojet.model.DetailStation
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class DetailsStationActivity:
    AppCompatActivity()
{
    private var dbHandler: DBHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_station)

        val stationId = intent.getLongExtra("station_id", -1)
        val stationName = intent.getStringExtra("station_name")
        val stationCapacity = intent.getIntExtra("station_capacity", -1)
        var stationList: List<String?>? = null

        synchroApi(stationId,stationName.toString(),stationCapacity)

        dbHandler = DBHandler(this@DetailsStationActivity)
        val addFavori = findViewById<Button>(R.id.addFavori)
        var favori: Boolean = false

        /*----------------------VERIFICATION SI STATION FAVORIS OU PAS ------------------------------*/
            
        stationList = dbHandler!!.getAllStations()
        stationList!!.map {
            if (stationId.toString() == it) {
                favori = true
            }
        }

        if (favori == true) {
            addFavori.setText("Supprimer des favoris")
        }

        /*----------------------------- AJOUT ET SUPPRESSION FAVORIS --------------------------------*/

        addFavori.setOnClickListener(){

            if (addFavori.text == "Ajouter aux favoris"){
                dbHandler!!.addStation(stationId.toString())
                addFavori.setText("Supprimer des favoris")
                Toast.makeText(this@DetailsStationActivity, "Station ajoutée", Toast.LENGTH_SHORT).show()
            }else {
                dbHandler!!.deleteStation(stationId)
                Toast.makeText(this@DetailsStationActivity, "Station supprimée", Toast.LENGTH_SHORT).show()
                addFavori.setText("Ajouter aux favoris")
            }
        }
    }


    /*-------------------------------------MENU --------------------------------------------------*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.basic,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.display_synchro_action -> displaySync()
            R.id.display_map_action -> displayMap()
            R.id.display_favoris_action -> displayFavoris()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displaySync(){
        Toast.makeText(this, "Synchronisation en cours ...", Toast.LENGTH_SHORT).show()
        val stationId = intent.getLongExtra("station_id", -1)
        val stationName = intent.getStringExtra("station_name")
        val stationCapacity = intent.getIntExtra("station_capacity", -1)
        synchroApi(stationId,stationName.toString(),stationCapacity)
    }

    private fun displayFavoris() {
        val intent = Intent(this,FavoriteActivity::class.java)
        startActivity(intent)
    }

    private fun displayMap() {
        val intent= Intent(this,MapsActivity::class.java)
        startActivity(intent)
    }

    /*--------------------------------------------------------------------------------------------*/


    private fun synchroApi(stationId: Long, stationName: String, stationCapacity: Int) {

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
            val stationVelib = result.data.stations
            stationVelib.map {
                val (stationCode, station_id, numBikesAvailable, num_bikes_available_types, numDocksAvailable) = it
                val (mechanical, ebike) = num_bikes_available_types

                if (stationId == station_id) {
                    val mechanical2 = mechanical.mechanical.toString()
                    val ebike2 = ebike.ebike.toString()
                    DetailStation(
                       stationCode, station_id, numBikesAvailable, mechanical2.toInt(), ebike2.toInt(),
                        numDocksAvailable, false
                    )
                    val Meca =
                        findViewById<TextView>(R.id.details_station_velo_bleu_textview)
                    Meca.text = mechanical2
                    val Elec =
                        findViewById<TextView>(R.id.details_station_velo_vert_textview)
                    Elec.text = ebike2
                    val nbPlaceLibre =
                        findViewById<TextView>(R.id.details_placeLibre_textview)
                    nbPlaceLibre.text = it.numDocksAvailable.toString()
                    val Capacity =
                        findViewById<TextView>(R.id.details_capacity_textview)
                    Capacity.text = stationCapacity.toString()
                    val Name =
                        findViewById<TextView>(R.id.details_station_name_textview)
                    Name.text = stationName
                }
            }
        }
    }
}






