package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.example.weatherapp.databinding.SheetBottomBinding
import com.example.weatherapp.db.Location
import com.google.android.material.tabs.TabLayout

class BottomSheet(
    private val locationName: String,
    private val latitude: Double,
    private val longitude: Double,
    private val weatherData: WeatherApiResponse,
) : ViewPagerBottomSheetDialogFragment() {

    private var _binding: SheetBottomBinding? = null
    private val binding get() = _binding!!

    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetBottomBinding.inflate(inflater, container, false)
        val view = binding.root

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = locationName

        toolbar.inflateMenu(R.menu.like_menu)

        locationViewModel.locations.observe(viewLifecycleOwner) {locations ->
            if((locations.find { it.locationName == locationName }) != null ){
                toolbar.menu.findItem(R.id.action_add_favorite).isVisible = false
                toolbar.menu.findItem(R.id.action_delete_favorite).isVisible = true
            }
            else{
                toolbar.menu.findItem(R.id.action_add_favorite).isVisible = true
                toolbar.menu.findItem(R.id.action_delete_favorite).isVisible = false
            }
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_favorite -> {
                    locationViewModel.addLocation(
                        Location(
                            locationName,
                            latitude,
                            longitude,
                            System.currentTimeMillis()
                        )
                    )
                    true
                }
                R.id.action_delete_favorite -> {
                    locationViewModel.deleteLocationByName(locationName)
                    true
                }
                else -> false
            }
        }

        viewPager = view.findViewById(R.id.pager)
        val adapter = MyFragmentAdapter(childFragmentManager, weatherData)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}