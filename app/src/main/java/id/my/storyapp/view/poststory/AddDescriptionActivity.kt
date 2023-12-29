package id.my.storyapp.view.poststory

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.my.storyapp.R
import id.my.storyapp.data.remote.Result
import id.my.storyapp.databinding.ActivityAddDescriptionBinding
import id.my.storyapp.util.reduceFileImage
import id.my.storyapp.util.uriToFile
import id.my.storyapp.view.ViewModelFactory
import id.my.storyapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddDescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDescriptionBinding
    private lateinit var viewModel: AddDescriptionViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Float? = null
    private var lon: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(this)

        // set location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setImageData()
        binding.checkboxAddLocation.setOnClickListener {
            getMyLocation()
        }
    }

    private fun setImageData() {
        val imgUri = intent.getStringExtra(AddStoryActivity.EXTRA_IMAGE_URI)?.toUri()
        binding.ivStoryPhoto.setImageURI(imgUri)

        if (imgUri != null) {
            binding.btnPost.setOnClickListener {
                uploadImage(imgUri)
            }
        }
    }

    private fun uploadImage(uri: Uri) {
        showLoading(true)
        val imgFile = uriToFile(uri, this).reduceFileImage()
        Log.d("Image File", "uploadImage: ${imgFile.path}")
        val description = binding.descriptionEditText.text.toString()

        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imgFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            requestImageFile
        )

        viewModel.postStory(multipartBody, desc, lat, lon).observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }

                is Result.Empty -> {
                    showLoading(false)
                    Toast.makeText(this,
                        getString(R.string.response_data_is_empty), Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    showLoading(true)
                }

            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): AddDescriptionViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[AddDescriptionViewModel::class.java]
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_request_granted), Toast.LENGTH_SHORT
                    ).show()
                    getMyLocation()
                }

                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_request_granted), Toast.LENGTH_SHORT
                    ).show()
                    getMyLocation()
                }

                else -> {
                    Toast.makeText(this, getString(R.string.rejected_permission), Toast.LENGTH_LONG).show()
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                    Log.d(AddDescriptionActivity::class.java.simpleName, "lat: $lat, lon: $lon")
                } else {
                    Toast.makeText(
                        this@AddDescriptionActivity,
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}