package fr.epf.applicationmobileprojet

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import fr.epf.applicationmobileprojet.model.Station


class StationAdapter(val stationFavorite : List<String>, val stationList: List<Station>) :
    RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    //val stationList: MutableList<Station> = mutableListOf()

    class StationViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val stationView = inflater.inflate(R.layout.station_adapter, parent, false)
        return StationViewHolder(stationView)
    }


    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val stationTextview = holder.view.findViewById<TextView>(R.id.adapter_station_textview)
        val stationID = stationFavorite[position]
        val favori = Station(
            200,"nom", 30.20, 20.30, 90
        )
        favori.station_id = stationFavorite[position].toLong()

        stationList.map {
            if(favori.station_id == it.station_id) {
                stationTextview.text = it.name
                favori.name = it.name
                favori.capacity = it.capacity
            }
        }

        holder.view.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetailsStationActivity::class.java)

            Toast.makeText(context, "Chargement ...", Toast.LENGTH_LONG).show()
            intent.putExtra("station_id", favori.station_id)
            intent.putExtra("station_name", favori.name)
            intent.putExtra("station_capacity", favori.capacity)
            context.startActivity(intent)
        }


    }


    override fun getItemCount() =  stationFavorite.size
}