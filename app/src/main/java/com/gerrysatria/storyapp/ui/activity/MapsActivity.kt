package com.gerrysatria.storyapp.ui.activity

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.gerrysatria.storyapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gerrysatria.storyapp.databinding.ActivityMapsBinding
import com.gerrysatria.storyapp.ui.viewmodel.MapsViewModel
import com.gerrysatria.storyapp.ui.viewmodel.ViewModelFactory
import com.gerrysatria.storyapp.data.Result
import com.gerrysatria.storyapp.data.response.ListStoryItem
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        mapsViewModel.getStoryWithLocation().observe(this) {
            if (it != null) {
                when(it) {
                    is Result.Success -> {
                        addManyMarker(it.data.listStory)
                    }
                    is Result.Loading -> {}
                    is Result.Error -> {
                        Toast.makeText(this, it.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                showToast(getString(R.string.error_parsing_style_map))
            }
        } catch (exception: Resources.NotFoundException) {
            showToast("Can't find style. Error: $exception")
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker(tourismPlace: List<ListStoryItem>) {
        tourismPlace.forEach { tourism ->
            tourism.lat?.let { lat ->
                tourism.lon?.let { lon ->
                    val latLng = LatLng(lat, lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Story from : ${tourism.name}")
                            .snippet(tourism.description)
                    )
                    boundsBuilder.include(latLng)
                }
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                30
            )
        )
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}