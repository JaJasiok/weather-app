package com.example.weatherapp

import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.TimeZone

class RainAdapter(private var timezoneOffset: Int,
                  private var dt: List<Long>?,
                  private var rain: List<Double>?,
                  private var pop: List<Double>?
                           ): RecyclerView.Adapter<RainAdapter.ViewHolder>() {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setData(timezoneOffset: Int,dt: List<Long>?, rain: List<Double>?, pop: List<Double>?) {
        this.timezoneOffset = timezoneOffset
        this.dt = dt
        this.rain = rain
        this.pop = pop
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView){
        var cardView: CardView = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_rain, parent, false) as CardView
        return ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardView = holder.cardView
        cardView.background = null

        val probabilityText = cardView.findViewById<TextView>(R.id.card_probability)
        probabilityText.text = (pop!![position] * 100).toInt().toString() + "%"

        val icon = getIconNameRain(rain!![position])
        val drawable = getDrawableByName(cardView.context, icon)
        val imageView = cardView.findViewById<ImageView>(R.id.rain_image)
        imageView.setImageDrawable(drawable)
        imageView.contentDescription = rain?.get(position).toString()

        val rainText = cardView.findViewById<TextView>(R.id.card_rain)
        rainText.text = getRainText(rain!![position])
        if(rain!![position] < 0.01){
            rainText.setTextColor(cardView.context.getColorResCompat(android.R.attr.textColorSecondary))
            rainText.setShadowLayer(0.0F, 0.0F, 0.0F, 0)
        }

//        val hour = getHourFromTimestamp((dt?.get(position)?.plus(timezoneOffset) as Long))
        val hour = getHourFromTimestamp(dt!![position], timezoneOffset)

        val hourText = cardView.findViewById<TextView>(R.id.card_hour)
        hourText.text = hour.toString() + ":00"


//        probabilityText.text = (pop!![position] * 100).toInt().toString() + "%"

//        val sunriseHour = getHourFromTimestamp(sunrise!! + timezoneOffset)
//        val sunsetHour = getHourFromTimestamp(sunset!! + timezoneOffset)



//        val drawable = ContextCompat.getDrawable(cardView.context, R.drawable.partly_cloudy_day)


//        val imgTxt = cardView.findViewById<TextView>(R.id.img_txt)
//        imgTxt.text = icon

        cardView.setOnClickListener {
            listener?.onClick(position)
        }
    }


    override fun getItemCount(): Int {
        return dt?.size ?: 0
    }

    fun Context.getColorResCompat(@AttrRes id: Int): Int {
        val resolvedAttr = TypedValue()
        this.theme.resolveAttribute(id, resolvedAttr, true)
        val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
        return ContextCompat.getColor(this, colorRes)
    }

    private fun getIconNameRain(rain: Double): String {
        var name: String

        if(rain < 0.01){
            name = "empty_drop"
        }
        else if(rain < 0.5){
            name = "low_drop"
        }
        else if(rain < 2.5){
            name = "half_drop"
        }
        else if(rain < 10.0){
            name = "high_drop"
        }
        else{
            name = "full_drop"
        }
        return name
    }

    private fun getRainText(rain: Double): String {
        var text = "—"

        if(rain == 0.0){
            text = "—"
        }
        else if(rain < 0.01){
            text = "<0.01"
        }
        else if(rain < 0.05){
            text = "0.1"
        }
        else if(rain >= 0.05) {
            text = String.format("%.1f", rain)
        }

        return text.replace(",", ".")
    }

    private fun getHourFromTimestamp(timestamp: Long, offsetSeconds: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000

        val timeZone = TimeZone.getTimeZone("UTC")
        timeZone.rawOffset = offsetSeconds * 1000

        calendar.timeZone = timeZone

        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}
