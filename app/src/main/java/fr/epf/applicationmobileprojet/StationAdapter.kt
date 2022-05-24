package fr.epf.applicationmobileprojet

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.epf.applicationmobileprojet.model.Station

class StationAdapter(val stations : List<Station>) :
    RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    class StationViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val stationView = inflater.inflate(R.layout.station_adapter, parent, false)
        return StationViewHolder(stationView)
    }


    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val station = stations[position]

        holder.view.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetailsStationActivity::class.java)
            intent.putExtra("station_id", position)
            context.startActivity(intent)
        }

        val stationTextview =
            holder.view.findViewById<TextView>(R.id.adapter_station_textview)

        stationTextview.text = station.name



    }

    override fun getItemCount() =  stations.size


}