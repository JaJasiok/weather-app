package com.example.weatherapp.fragments

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DailyWeatherAdapter
import com.example.weatherapp.MyFragmentAdapter
import com.example.weatherapp.databinding.FragmentWeekBinding

class WeekFragment() : Fragment(), MyFragmentAdapter.WeatherDataListener {

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherData: WeatherApiResponse
    private var dataReceived = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dataReceived) {
            updateFragment()
        }
    }

    private fun updateFragment() {
        _binding?.let { binding ->

            val dailyRecyclerView = binding.dailyRecyclerView
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            dailyRecyclerView.layoutManager = layoutManager

            val dailyAdapter = DailyWeatherAdapter(weatherData.daily!!)

            dailyRecyclerView.adapter = dailyAdapter
        }
    }

    override fun onWeatherDataUpdated(newWeatherData: WeatherApiResponse) {
        this.weatherData = newWeatherData
        dataReceived = true

        if (isAdded && view != null) {
            updateFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}