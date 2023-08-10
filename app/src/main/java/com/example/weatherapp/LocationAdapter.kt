package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.CardLocationBinding
import com.example.weatherapp.db.Location

class LocationAdapter(
    private val locations: List<Location>
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    class ViewHolder(val binding: CardLocationBinding) : RecyclerView.ViewHolder(binding.root)

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int /*, locationName: TextView*/)
        fun onLongClick(locationName: String): Boolean
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            CardLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardView = holder.binding

        val locationName = cardView.locationName
        locationName.text = locations[position].locationName

        holder.itemView.setOnClickListener {
            listener?.onClick(position /*, locationName*/)
        }

        holder.itemView.setOnLongClickListener {
            listener?.onLongClick(locations[position].locationName) ?: false
        }
    }
}