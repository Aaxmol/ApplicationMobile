package fr.epf.applicationmobileprojet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fr.epf.applicationmobileprojet.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import fr.epf.applicationmobileprojet.PermissionUtils.isPermissionGranted
import fr.epf.applicationmobileprojet.api.LocalisationStations
import fr.epf.applicationmobileprojet.databinding.ActivityMapsBinding
import fr.epf.applicationmobileprojet.model.Station
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MapsActivity:
        AppCompatActivity(),
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val stations: MutableList<Station> = mutableListOf()
    private var stationAdapter : StationAdapter? = null
    private var selectedMarker: Marker? = null
    private var permissionDenied = false
    /*
    Les déclarations suivantes ne sont pas très optimisée mais nous n'avons pas réussi à faire
    autrement
     */
    private val markerListGroup1: MutableList<Marker> = mutableListOf()
    private val markerListGroup2: MutableList<Marker> = mutableListOf()
    private val markerListGroup3: MutableList<Marker> = mutableListOf()
    private val markerListGroup4: MutableList<Marker> = mutableListOf()
    private var zoomLimit1 = 1f
    private var zoomLimit2 = 11.9f
    private var zoomLimit3 = 13.4f
    private var zoomLimit4 = 14.9f
    private var regulateur1 = false
    private var regulateur2 = false
    private var regulateur3 = false
    private var regulateur4 = false
    private var num = 0
    private val DEFAULT_ZOOM = 15


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

    /* -------------------------------ACCES DETAILS STATION---------------------------------------*/
     override fun onInfoWindowClick(marker: Marker) {

         stations.map {
             if (it.name == marker.title){
                 val intent =Intent(this, DetailsStationActivity::class.java)
                 intent.putExtra("station_id", it.station_id)
                 intent.putExtra("station_name", it.name)
                 intent.putExtra("station_capacity", it.capacity)
                 startActivity(intent)
             }
         }
     }

    /* ----------------------------APPARITION PAR ZONE MARKERS------------------------------------*/
    override fun onCameraMove() {
        zone()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* -------------------------------CREATION MAP----------------------------------------*/

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                    val newStation1: Marker? =
                        mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lon)).title(it.name).snippet("Capacité : ${it.capacity}").visible(false))
                    val newStation2: Marker? =
                        mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lon)).title(it.name).snippet("Capacité : ${it.capacity}").visible(false))
                    val newStation3: Marker? =
                        mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lon)).title(it.name).snippet("Capacité : ${it.capacity}").visible(false))
                    val newStation4: Marker? =
                        mMap.addMarker(MarkerOptions().position(LatLng(it.lat, it.lon)).title(it.name).snippet("Capacité : ${it.capacity}").visible(false))
                    if (num % 10 == 0){
                        if (newStation1 != null) {
                            markerListGroup1.add(newStation1)
                        }
                    }
                    if (num % 5 == 0){
                        if (newStation2 != null) {
                            markerListGroup2.add(newStation2)
                        }
                    }
                    if (num % 2 == 0){
                        if (newStation3 != null) {
                            markerListGroup3.add(newStation3)
                        }
                    }
                    if (num % 1 == 0){
                        if (newStation4 != null) {
                            markerListGroup4.add(newStation4)
                        }
                    }
                    num += 1
                    }

                }
        stationAdapter?.notifyDataSetChanged()

        for (marker4 in markerListGroup4) {
            marker4.isVisible = true
        }

        val paris = LatLng(48.856614, 2.3522219)
        mMap.setMinZoomPreference(9.5f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(paris.latitude, paris.longitude), DEFAULT_ZOOM.toFloat()))
        googleMap.setOnCameraMoveListener(this@MapsActivity)
        googleMap.setOnInfoWindowClickListener(this@MapsActivity)
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()
    }


/*------------------------------CONFIGURATION GEOLOCALISATION-------------------------------------*/

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Localisation actuelle:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            enableMyLocation()
        } else {
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    /*-----------------------------------------MENU-----------------------------------------------*/


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

    /*--------------FONCTION POUR REGULER L'APPARITION DES MARKER: PAS OPTIMISE-------------------*/

    fun zone (){
        val position: CameraPosition = mMap.cameraPosition

        if (zoomLimit2 > position.zoom && position.zoom > zoomLimit1 && !regulateur1) {
            gestionApparitionMarkers(
                markerListGroup1,
                markerListGroup2,
                markerListGroup3,
                markerListGroup4
            )
            regulateur1 = true
            regulateur2 = false
        }


        if (zoomLimit3 > position.zoom && position.zoom > zoomLimit2 && !regulateur2) {
            gestionApparitionMarkers(
                markerListGroup2,
                markerListGroup1,
                markerListGroup3,
                markerListGroup4
            )
            regulateur1 = false
            regulateur2 = true
            regulateur3 = false
        }
        if (zoomLimit4 > position.zoom && position.zoom > zoomLimit3 && !regulateur3) {
            gestionApparitionMarkers(
                markerListGroup3,
                markerListGroup2,
                markerListGroup1,
                markerListGroup4
            )
            regulateur2 = false
            regulateur3 = true
            regulateur4 = false

        }
        if (zoomLimit4 < position.zoom && !regulateur4) {
            gestionApparitionMarkers(
                markerListGroup4,
                markerListGroup2,
                markerListGroup3,
                markerListGroup1
            )
            regulateur3 = false
            regulateur4 = true

        }
    }

    fun gestionApparitionMarkers(
        markerListGroupYes: MutableList<Marker> = mutableListOf(),
        markerListGroupNo1: MutableList<Marker> = mutableListOf(),
        markerListGroupNo2: MutableList<Marker> = mutableListOf(),
        markerListGroupNo3: MutableList<Marker> = mutableListOf(),)
    {
        for (marker1 in markerListGroupYes) {
            marker1.isVisible = true
        }
        for (marker2 in markerListGroupNo1) {
            marker2.isVisible = false
        }
        for (marker3 in markerListGroupNo2) {
            marker3.isVisible = false
            }
        for (marker4 in markerListGroupNo3) {
            marker4.isVisible = false
            }
    }
    /*-------------------------------------------END----------------------------------------------*/
    }



