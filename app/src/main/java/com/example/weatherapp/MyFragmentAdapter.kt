package com.example.weatherapp

import WeatherApiResponse
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.weatherapp.fragments.TodayFragment
import com.example.weatherapp.fragments.TomorrowFragment
import com.example.weatherapp.fragments.WeekFragment

class MyFragmentAdapter(
    fm: FragmentManager,
) : FragmentPagerAdapter(fm) {

    private val fragments = arrayListOf<Fragment>()

    private lateinit var weatherData: WeatherApiResponse


    init {
        fragments.add(TodayFragment())
        fragments.add(TomorrowFragment())
        fragments.add(WeekFragment())
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Today"
            1 -> return "Tomorrow"
            2 -> return "Week"
        }
        return null
    }

    fun setWeatherData(newWeatherData: WeatherApiResponse) {
        this.weatherData = newWeatherData
        for (fragment in fragments) {
            if (fragment is WeatherDataListener) {
                fragment.onWeatherDataUpdated(weatherData)
            }
        }
        notifyDataSetChanged()
    }

    interface WeatherDataListener {
        fun onWeatherDataUpdated(weatherData: WeatherApiResponse)
    }
}