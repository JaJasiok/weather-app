package com.example.weatherapp

import DailyWeather
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyWeatherAdapter(private var daily: List<DailyWeather>) :
    RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder>() {
    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setData(daily: List<DailyWeather>) {
        this.daily = daily
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView = itemView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyWeatherAdapter.ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_daily, parent, false) as CardView
        return DailyWeatherAdapter.ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: DailyWeatherAdapter.ViewHolder, position: Int) {
        val cardView = holder.cardView
        cardView.background = null

        val dateText = cardView.findViewById<TextView>(R.id.date_text)
        if (position == 0) {
            dateText.text = "Today"
        } else {
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
            dateText.text = dateFormat.format(Date(daily[position].dt * 1000))
        }

        val weatherText = cardView.findViewById<TextView>(R.id.weather_text)
        weatherText.text = daily[position].weather.description.capitalize()

        val tempDayText = cardView.findViewById<TextView>(R.id.temp_day_text)
        tempDayText.text = daily[position].temp.day.toInt().toString() + "°C"

        val tempNightText = cardView.findViewById<TextView>(R.id.temp_night_text)
        tempDayText.text = daily[position].temp.night.toInt().toString() + "°C"

        val weatherImage = cardView.findViewById<ImageView>(R.id.weather_image)

        val probabilityText = cardView.findViewById<TextView>(R.id.probability_text)
        val stringList = listOf("Thunderstorm", "Drizzle", "Rain", "Snow", "Mist", "Fog", "Haze", "Tornado")

        if(daily[position].weather.main in stringList){
            probabilityText.text = (daily[position].pop * 100).toInt().toString() + "%"
        }
        else{
            probabilityText.text = ""
        }

        var icon = getIconNameDay(
            daily[position].weather.id,
            daily[position].dt,
            daily[position].sunrise,
            daily[position].sunset,
            daily[position].clouds
        )

        icon = icon.replace("night", "day")

        val drawable = getDrawableByName(cardView.context, icon)
        weatherImage.setImageDrawable(drawable)

        weatherImage.contentDescription = daily[position].weather.description

        if (position == 7) {
            val breakingLine = cardView.findViewById<ImageView>(R.id.breaking_line)
            breakingLine.visibility = View.GONE
        }

        cardView.setOnClickListener {
            listener?.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return daily.size
    }
}