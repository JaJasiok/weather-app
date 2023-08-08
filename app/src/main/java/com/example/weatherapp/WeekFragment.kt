package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentWeekBinding

class WeekFragment(private val weatherData: WeatherApiResponse) : Fragment() {

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)

        val weekView = binding.root

        val dailyRecyclerView = binding.dailyRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        dailyRecyclerView.layoutManager = layoutManager

        val dailyAdapter = DailyWeatherAdapter(weatherData.daily!!)

        dailyRecyclerView.adapter = dailyAdapter

        return weekView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}