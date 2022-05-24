package fr.epf.applicationmobileprojet

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.withStateAtLeast
import androidx.versionedparcelable.VersionedParcelize
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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
import java.io.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
class MapsActivity :
        AppCompatActivity(),
        Serializable,
        Parcelable,
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
         val stationID = marker.snippet?.toInt()
         val stationNom = marker.title

         stations.map {
             if (it.name == marker.title){
                 val intent = Intent(this, DetailsStationActivity::class.java)
                 intent.putExtra("station_id", it.station_id)
                 intent.putExtra("station_name", it.name)
                 //Here putParcebleExtra
                 intent.putExtra("stationList", stations as Parcelable)
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
                Log.d(ContentValues.TAG, "synchroApi: ${result}")
                val stationVelib = result.data.stations
                stationVelib.map {
                    val (station_id, name, lat, lon, capacity) = it
                    Station(
                        station_id, name, lat, lon, capacity
                    )
                }

                .map {
                    stations.add(it)
                    mMap.addMarker(MarkerOptions().apply {
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
}