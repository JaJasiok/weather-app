package com.example.weatherapp

import WeatherApiResponse
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val weatherData: WeatherApiResponse
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


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
            0 -> return context.getText(R.string.today)
            1 -> return context.getText(R.string.tomorrow)
            2 -> return context.getText(R.string.week)
        }
        return null
    }
}