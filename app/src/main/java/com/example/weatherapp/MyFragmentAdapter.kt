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
    private var weatherData: WeatherApiResponse
) : FragmentPagerAdapter(fm) {

    private val fragments = arrayListOf<Fragment>()

    init {
        fragments.add(TodayFragment(weatherData))
        fragments.add(TomorrowFragment(weatherData))
        fragments.add(WeekFragment(weatherData))
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