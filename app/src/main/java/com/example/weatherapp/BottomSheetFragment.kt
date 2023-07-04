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
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject

class BottomSheet(private val location: String, private val weatherData: WeatherApiResponse) : BottomSheetDialogFragment() {

    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var viewPager: NonSwipeableViewPager
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var tabLayout: TabLayout

    /*
    override fun onStart() {
        super.onStart()
        bottomSheet =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup // notice the R root package
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // SETUP YOUR BEHAVIOR HERE
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    showView(appBarLayout, getActionBarSize()) // show app bar when expanded completely
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    hideAppBar(appBarLayout) // hide app bar when collapsed
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss() // destroy the instance
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        hideAppBar(appBarLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideAppBar(view: View) {
        val params = view.layoutParams
        params.height = 0
        view.layoutParams = params
    }

    private fun showView(view: View, size: Int) {
        val params = view.layoutParams
        params.height = size
        view.layoutParams = params
    }

    private fun getActionBarSize(): Int {
        val styledAttributes =
            requireContext().theme.obtainStyledAttributes(intArrayOf(androidx.appcompat.R.attr.actionBarSize))
        return styledAttributes.getDimension(0, 0f).toInt()
    }
     */

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