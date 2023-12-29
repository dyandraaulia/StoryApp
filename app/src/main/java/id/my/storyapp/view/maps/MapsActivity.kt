package id.my.storyapp.view.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import id.my.storyapp.R
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.remote.response.ListStoryItem
import id.my.storyapp.databinding.ActivityMapsBinding
import id.my.storyapp.view.ViewModelFactory
import id.my.storyapp.view.detailstory.DetailStoryActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapsViewModel = obtainViewModel(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        getUsersStory()
    }

    private fun getUsersStory() {
        mapsViewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    val listStory = result.data.listStory

                    listStory.forEach { data ->
                        val latLng =
                            data.lat?.let { lat -> data.lon?.let { lon -> LatLng(lat, lon) } }
                        if (latLng != null) {
                            showMarker(
                                latLng, data
                            )
                        }
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }

                is Result.Empty -> {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.data_is_empty), Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.permission_request_granted), Toast.LENGTH_SHORT
                    ).show()
                    getMyLocation()
                }

                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.permission_request_granted), Toast.LENGTH_SHORT
                    ).show()
                    getMyLocation()
                }

                else -> {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.rejected_permission), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    private fun getMyLocation() {
        if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @Suppress("NAME_SHADOWING")
    private fun showMarker(myLocation: LatLng, data: ListStoryItem) {
        val marker = mMap.addMarker(
            MarkerOptions()
                .position(myLocation)
                .title(data.name)
                .snippet(data.description)
                .icon(vectorToBitmap(R.drawable.marker))
        )
        marker?.tag = data

        mMap.setOnInfoWindowClickListener {
            val data = it.tag as? ListStoryItem
            if (data != null) {
                val intent = Intent(applicationContext, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_DETAIL_STORY, data)
                startActivity(intent)
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun obtainViewModel(activity: AppCompatActivity): MapsViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MapsViewModel::class.java]
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}