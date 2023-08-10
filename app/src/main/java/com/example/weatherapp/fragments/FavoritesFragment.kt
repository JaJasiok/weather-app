package com.example.weatherapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.LocationAdapter
import com.example.weatherapp.LocationModelFactory
import com.example.weatherapp.LocationViewModel
import com.example.weatherapp.WeatherActivity
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enterTransition = MaterialFadeThrough()
//        exitTransition = MaterialFadeThrough()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val view = binding.root

        val toolbar = binding.toolbar
        toolbar.title = "Favorites"

        setRecyclerView()

        return view
    }

    private fun setRecyclerView() {
        locationViewModel.locations.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.locationRecyclerView.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            } else {
                binding.locationRecyclerView.visibility = View.VISIBLE
                binding.errorText.visibility = View.GONE

                binding.locationRecyclerView.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = LocationAdapter(it).apply {
                        setListener(object : LocationAdapter.Listener {
                            override fun onClick(position: Int /*, locationNameView: TextView*/) {
                                val intent = Intent(requireContext(), WeatherActivity::class.java)
                                intent.putExtra("latitude", it[position].locationLat)
                                intent.putExtra("longitude", it[position].locationLng)
//                            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), locationNameView, "location_name")
                                startActivity(intent /*, options.toBundle()*/)
                            }

                            override fun onLongClick(locationName: String): Boolean {
                                showDeleteConfirmationDialog(locationName)
                                return true
                            }
                        })
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(locationName: String) {
        val builder = AlertDialog.Builder(requireContext())
        val message = "Are you sure you want to delete <b>$locationName</b> from favorites?"
        builder.setTitle("Delete Location")
            .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
            .setPositiveButton("Delete") { _, _ ->
                // Delete the location from favorites here
                locationViewModel.deleteLocationByName(locationName)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}