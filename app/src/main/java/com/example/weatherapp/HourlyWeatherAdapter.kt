package com.example.weatherapp

import HourlyWeather
import WeatherApiResponse
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar


class HourlyWeatherAdapter(private var timezoneOffset: Int,
                           private var sunrise: Long?,
                           private var sunset: Long?,
                           private var dt: List<Long>?,
                           private var temp: List<Double>?,
                           private var clouds: List<Int>?,
                           private var id: List<Int>?,
                           private var desc: List<String>?): RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder>() {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setData(timezoneOffset: Int, sunrise: Long?, sunset: Long?, dt: List<Long>?, temp: List<Double>?, clouds: List<Int>?, d: List<Int>?, desc: List<String>?) {
        this.timezoneOffset = timezoneOffset
        this.sunrise = sunrise
        this.sunset = sunset
        this.dt = dt
        this.temp = temp
        this.clouds = clouds
        this.id = id
        this.desc = desc
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

        val hour = getHourFromTimestamp((dt?.get(position)?.plus(timezoneOffset) as Long))

        val hourText = cardView.findViewById<TextView>(R.id.card_hour)
        hourText.text = hour.toString() + ":00"

        val tempText = cardView.findViewById<TextView>(R.id.card_temp)
        tempText.text = (temp?.get(position) ?: 0).toInt().toString() + "°C"

        val imageView = cardView.findViewById<ImageView>(R.id.hourly_image)

        val sunriseHour = getHourFromTimestamp(sunrise!! + timezoneOffset)
        val sunsetHour = getHourFromTimestamp(sunset!! + timezoneOffset)

        var dayTime: String

        if(dt!![position] > sunset!! && dt!![position] < sunrise!!){
            dayTime = "night"
        }
        else{
            dayTime = "day"
        }

        var icon = ""

        if(id?.get(position) in (200..232)){
            icon = "thunderstorms"

            if(clouds?.get(position)!! < 25){
                icon += "_$dayTime"
            }

            if(id?.get(position) !in (210..221)){
                icon += "rain"
            }
        }
        else if(id?.get(position) in (300..321)){
            icon = "drizzle"

            if(clouds?.get(position)!! < 25){
                icon = "partly_cloudy_${dayTime}_drizzle"
            }
        }
        else if(id?.get(position) in (500..531)){
            icon = "rain"

            if(clouds?.get(position)!! < 25){
                icon = "partly_cloudy_${dayTime}_rain"
            }
        }
        else if(id?.get(position) in (600..622)){
            icon = "snow"

            if(clouds?.get(position)!! < 25){
                icon = "partly_cloudy_${dayTime}_snow"
            }

            if(id?.get(position) in (611..616)){
                icon = "sleet"

                if(clouds?.get(position)!! < 25){
                    icon = "partly_cloudy_${dayTime}_sleet"
                }
            }
        }
        else if(id?.get(position) == 701){
            icon = "mist"
        }
        else if(id?.get(position) == 711){
            icon = "smoke"

            if(clouds?.get(position)!! < 25){
                icon = "partly_cloudy_${dayTime}_smoke"
            }
        }
        else if(id?.get(position) == 721){
            icon = "haze_${dayTime}"

            if(clouds?.get(position)!! > 25){
                icon = "partly_cloudy_{$dayTime}_haze"
            }
            else if(clouds?.get(position)!! > 50){
                icon = "haze"
            }
        }
        else if(id?.get(position) == 741){
            icon = "fog_${dayTime}"

            if(clouds?.get(position)!! > 25){
                icon = "partly_cloudy_{$dayTime}_fog"
            }
            else if(clouds?.get(position)!! > 50){
                icon = "fog"
            }
        }
        else if(id?.get(position) in (731..762)){
            icon = "dust_${dayTime}"

            if(clouds?.get(position)!! > 50){
                icon = "dust"
            }
        }
        else if(id?.get(position) in (771..781)){
            icon = "tornado"
        }
        else if(id?.get(position) == 800){
            icon = "clear_${dayTime}"
        }
        else if(id?.get(position) == 801){
            icon = "partly_cloudy_${dayTime}"
        }
        else if(id?.get(position) in (802..803)){
            icon = "overcast_${dayTime}"
        }
        else if(id?.get(position) == 804){
            icon = "overcast"
        }

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

    private fun getDrawableByName(context: Context, drawableName: String): Drawable? {
        val resources = context.resources
        val packageName = context.packageName
        val resourceId = resources.getIdentifier(drawableName, "drawable", packageName)
        return if (resourceId != 0) {
            resources.getDrawable(resourceId, null)
        } else {
            null
        }
    }

}
