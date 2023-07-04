package com.example.weatherapp

import WeatherApiResponse
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyFragmentAdapter(
    fm: FragmentManager,
    private val weatherData: WeatherApiResponse
) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return TodayFragment(weatherData)
            1 -> return TomorrowFragment(weatherData)
            2 -> return WeekFragment(weatherData)
        }
        return TodayFragment(weatherData)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Today"
            1 -> return "Tomorrow"
            2 -> return "Week"
        }
        return null
    }
}