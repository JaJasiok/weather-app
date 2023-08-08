package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private val locationViewModel: LocationViewModel by activityViewModels{
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

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

    private fun setRecyclerView(){
        locationViewModel.locations.observe(viewLifecycleOwner){
            binding.locationRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = LocationAdapter(it).apply{
                    setListener(object : LocationAdapter.Listener {
                        override fun onClick(position: Int) {
                            val intent = Intent(requireContext(), WeatherActivity::class.java)
                            intent.putExtra("latitude", it[position].locationLat)
                            intent.putExtra("longitude", it[position].locationLng)
                            startActivity(intent)
                        }
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}