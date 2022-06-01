package fr.epf.applicationmobileprojet

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.withStateAtLeast
import androidx.versionedparcelable.VersionedParcelize
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import fr.epf.applicationmobileprojet.api.LocalisationStations
import fr.epf.applicationmobileprojet.api.StationService
import fr.epf.applicationmobileprojet.databinding.ActivityMapsBinding
import fr.epf.applicationmobileprojet.model.Station
import fr.epf.applicationmobileprojet.model.DetailStation
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.ArrayList

class MapsActivity :
        AppCompatActivity(),
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    val stations: MutableList<Station> = mutableListOf()
    private var stationAdapter : StationAdapter? = null
    private var selectedMarker: Marker? = null

    private val markerClickListener = object : GoogleMap.OnMarkerClickListener {
        override fun onMarkerClick(marker: Marker): Boolean {
            if (marker == selectedMarker) {
                selectedMarker = null
                return true
            }
            selectedMarker = marker
            return false
        }
    }
    /* -------------------------------J'Y ARRIVE PAS LOL---------------------------------------*/

     override fun onInfoWindowClick(marker: Marker) {

         stations.map {
             if (it.name == marker.title){
                 val intent = Intent(this, DetailsStationActivity::class.java)
                 intent.putExtra("station_id", it.station_id)
                 intent.putExtra("station_name", it.name)
                 intent.putExtra("station_capacity", it.capacity)
                 intent.putParcelableArrayListExtra("stationList", stations as ArrayList<out Parcelable>)
                 startActivity(intent)
             }
         }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* -------------------------------CREATION MAP----------------------------------------*/

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* -----------------------------------------------------------------------------------*/


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
        val service = retrofit.create(LocalisationStations::class.java)

        /*---------------------------------MARKERS-INFOS--------------------------------------*/
        with(mMap) {
            setOnMarkerClickListener(markerClickListener)
            setOnMapClickListener { selectedMarker = null }
            setContentDescription("test")
        }


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
                    stations.add(it)
                    mMap.addMarker(
                        MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                            .apply {
                        val newStation = LatLng(it.lat, it.lon)
                        position(newStation)
                        title(it.name)
                        snippet("${it.station_id}")
                    })
                }
            }
            googleMap.setOnInfoWindowClickListener(this)
            stationAdapter?.notifyDataSetChanged()
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
        val intent = Intent(this, ListStationActivity::class.java)
        startActivity(intent)    }

    private fun displayMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}