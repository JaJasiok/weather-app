package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.TimeZone

class HourlyWeatherAdapter(
    private var timezoneOffset: Int,
    private var sunriseToday: Long?,
    private var sunsetToday: Long?,
    private var sunriseTomorrow: Long?,
    private var dt: List<Long>?,
    private var temp: List<Double>?,
    private var clouds: List<Int>?,
    private var id: List<Int>?,
    private var desc: List<String>?,
    private var main: List<String>?,
    private var pop: List<Double>?
) : RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder>() {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setData(
        timezoneOffset: Int,
        sunriseToday: Long?,
        sunsetToday: Long?,
        sunriseTomorrow: Long?,
        dt: List<Long>?,
        temp: List<Double>?,
        clouds: List<Int>?,
        id: List<Int>?,
        desc: List<String>?,
        main: List<String>?,
        pop: List<Double>?
    ) {
        this.timezoneOffset = timezoneOffset
        this.sunriseToday = sunriseToday
        this.sunsetToday = sunsetToday
        this.sunriseTomorrow = sunriseTomorrow
        this.dt = dt
        this.temp = temp
        this.clouds = clouds
        this.id = id
        this.desc = desc
        this.main = main
        this.pop = pop
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
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

        val hour = getHourFromTimestamp(dt!![position], timezoneOffset)

        val hourText = cardView.findViewById<TextView>(R.id.card_hour)
        hourText.text = hour.toString() + ":00"

        val tempText = cardView.findViewById<TextView>(R.id.card_temp)
        tempText.text = (temp?.get(position) ?: 0).toInt().toString() + "°C"

        val imageView = cardView.findViewById<ImageView>(R.id.hourly_image)

        val probabilityText = cardView.findViewById<TextView>(R.id.card_probability)
        val stringList =
            listOf("Thunderstorm", "Drizzle", "Rain", "Snow", "Mist", "Fog", "Haze", "Tornado")

        if (main!![position] in stringList) {
            probabilityText.text = (pop!![position] * 100).toInt().toString() + "%"
        } else {
            probabilityText.text = ""
//            probabilityText.visibility = View.GONE
        }

        val icon = getIconNameHour(
            id!![position],
            dt!![position],
            sunriseToday,
            sunsetToday,
            sunriseTomorrow,
            clouds!![position]
        )

        val drawable = getDrawableByName(cardView.context, icon)

        imageView.setImageDrawable(drawable)
        imageView.contentDescription = desc?.get(position) ?: "desc"

        cardView.setOnClickListener {
            listener?.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dt?.size ?: 0
    }

    fun getHourFromTimestamp(timestamp: Long, offsetSeconds: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000

        val timeZone = TimeZone.getTimeZone("UTC")
        timeZone.rawOffset = offsetSeconds * 1000

        calendar.timeZone = timeZone

        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}
