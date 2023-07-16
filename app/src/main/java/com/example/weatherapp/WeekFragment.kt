package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WeekFragment(private val weatherData: WeatherApiResponse) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val weekView = inflater.inflate(R.layout.fragment_week, container, false) as NestedScrollView

        val dailyRecyclerView = weekView.findViewById<RecyclerView>(R.id.daily_recycler_view)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        dailyRecyclerView.layoutManager = layoutManager

        val dailyAdapter = DailyWeatherAdapter(weatherData.daily!!)

        dailyRecyclerView.adapter = dailyAdapter

        return weekView
    }
}