package com.example.weatherapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar


class HourlyWeatherAdapter(private var timezoneOffset: Int,
                           private var sunrise: Long?,
                           private var sunset: Long?,
                           private var dt: List<Long>?,
                           private var temp: List<Double>?,
                           private var clouds: List<Int>?,
                           private var id: List<Int>?,
                           private var desc: List<String>?,
                           private var rain: List<Double>?,
                           private var pop: List<Double>?,
                           ): RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder>() {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setData(timezoneOffset: Int, sunrise: Long?, sunset: Long?, dt: List<Long>?, temp: List<Double>?, clouds: List<Int>?, id: List<Int>?, desc: List<String>?, rain: List<Double>?, pop: List<Double>?) {
        this.timezoneOffset = timezoneOffset
        this.sunrise = sunrise
        this.sunset = sunset
        this.dt = dt
        this.temp = temp
        this.clouds = clouds
        this.id = id
        this.desc = desc
        this.rain = rain
        this.pop = pop
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){
        var cardView: CardView = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_hourly, parent, false) as CardView
        return ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardView = holder.cardView
        cardView.background = null

//        val hour = getHourFromTimestamp((dt?.get(position)?.plus(timezoneOffset) as Long))
        val hour = getHourFromTimestamp(dt!![position])

        val hourText = cardView.findViewById<TextView>(R.id.card_hour)
        hourText.text = hour.toString() + ":00"

        val tempText = cardView.findViewById<TextView>(R.id.card_temp)
        tempText.text = (temp?.get(position) ?: 0).toInt().toString() + "°C"

        val imageView = cardView.findViewById<ImageView>(R.id.hourly_image)

        val rainText = cardView.findViewById<TextView>(R.id.card_rain)
        if(rain!![position] == 0.0){
            rainText.text = ""
        }
        else{
            rainText.text = (pop!![position] * 100).toInt().toString() + "%"

        }

        val sunriseHour = getHourFromTimestamp(sunrise!! + timezoneOffset)
        val sunsetHour = getHourFromTimestamp(sunset!! + timezoneOffset)

        val icon = getIconName(id!![position], dt!![position], sunrise, sunset, clouds!![position])

        val drawable = getDrawableByName(cardView.context, icon)

//        val drawable = ContextCompat.getDrawable(cardView.context, R.drawable.partly_cloudy_day)
        imageView.setImageDrawable(drawable)
        imageView.contentDescription = desc?.get(position) ?: "desc"

        cardView.setOnClickListener {
            listener?.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dt?.size ?: 0
    }

    fun getHourFromTimestamp(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000 // Convert seconds to milliseconds

        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}
