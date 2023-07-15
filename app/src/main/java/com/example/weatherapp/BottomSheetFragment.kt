package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject

class BottomSheet(private val location: String, private val weatherData: WeatherApiResponse) : ViewPagerBottomSheetDialogFragment() {

//    private lateinit var viewPager: NonSwipeableViewPager
    private lateinit var viewPager: ViewPager
    private lateinit var appBarLayout: AppBarLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myview: View = inflater.inflate(R.layout.sheet_bottom, container, false)

        appBarLayout = myview.findViewById(R.id.app_bar)

        val toolbar = myview.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = location

        viewPager = myview.findViewById(R.id.pager)

        val adapter = MyFragmentAdapter(childFragmentManager, weatherData)
        viewPager.adapter = adapter

        val tabLayout = myview.findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        return myview
    }
}