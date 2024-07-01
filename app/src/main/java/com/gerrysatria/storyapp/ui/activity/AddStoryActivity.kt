package com.gerrysatria.storyapp.ui.activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.gerrysatria.storyapp.R
import com.gerrysatria.storyapp.data.Result
import com.gerrysatria.storyapp.databinding.ActivityAddStoryBinding
import com.gerrysatria.storyapp.ui.viewmodel.AddStoryViewModel
import com.gerrysatria.storyapp.ui.viewmodel.ViewModelFactory
import com.gerrysatria.storyapp.utils.getImageUri
import com.gerrysatria.storyapp.utils.reduceFileImage
import com.gerrysatria.storyapp.utils.uriToFile
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.apply {
            title = getString(R.string.add_story)
            setDisplayHomeAsUpEnabled(true)
        }

        playAnimation()

        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
        binding.switchLocation.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {
                if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
                    createLocationRequest()
                } else {
                    requestLocationPermissions()
                }
            } else {
                latitude = null
                longitude = null
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun playAnimation(){
        val btnGallery = ObjectAnimator.ofFloat(binding.buttonGallery, View.ALPHA, 1f).setDuration(300)
        val btnCamera = ObjectAnimator.ofFloat(binding.buttonCamera, View.ALPHA, 1f).setDuration(300)
        val tvDescription = ObjectAnimator.ofFloat(binding.tvAddDescription, View.ALPHA, 1f).setDuration(300)
        val edAddDescription = ObjectAnimator.ofFloat(binding.edAddDescriptionLayout, View.ALPHA, 1f).setDuration(300)
        val shareLocation = ObjectAnimator.ofFloat(binding.switchLocation, View.ALPHA, 1f).setDuration(300)
        val btnUpload = ObjectAnimator.ofFloat(binding.buttonAdd, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(btnGallery, btnCamera)
        }

        AnimatorSet().apply {
            playSequentially(
                together,
                tvDescription,
                edAddDescription,
                shareLocation,
                btnUpload
            )
            startDelay = 100
        }.start()
    }

    private val launcherPhotoPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.previewImageView.setImageURI(currentImageUri)
        } else {
            showToast(getString(R.string.empty_image_gallery))
        }
    }

    private fun startGallery() {
        launcherPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.previewImageView.setImageURI(currentImageUri)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherCamera.launch(currentImageUri)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    showToast(getString(R.string.no_access_granted))
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                } else {
                    showToast(getString(R.string.error_location))
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestLocationPermissions()
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED -> showToast(getString(R.string.error_disable_GPS))
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        sendEx.message?.let { showToast(it) }
                    }
                }
            }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            showLoading(true)
            addStoryViewModel.uploadStory(
                imageFile,
                description,
                latitude,
                longitude
            ).observe(this@AddStoryActivity){result ->
                if (result != null) {
                    when(result) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showDialog(result.data.message)
                            showLoading(false)
                        }
                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_upload))
    }

    private fun moveToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showLoading(state: Boolean){
        binding.progressBarAdd.isVisible = state
    }

    private fun showDialog(message: String){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(getString(R.string.positive_button)) { _, _ ->
            moveToMain()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        private const val TAG = "AddStoryActivity"
    }
}