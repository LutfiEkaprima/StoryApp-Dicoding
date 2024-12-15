package com.dicoding.picodiploma.loginwithanimation.view.home.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityUploadStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.getImageUri
import com.dicoding.picodiploma.loginwithanimation.view.home.HomeActivity
import com.dicoding.picodiploma.loginwithanimation.view.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.view.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.viewmodel.ViewModelFactory
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private var currentImageUri: Uri? = null

    private lateinit var viewModel: UploadStoryViewModel

    private var currentLat: Float? = null
    private var currentLon: Float? = null

    private val requestPermissionLauncer = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
        }

    }

    private val locationRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            showToast("Izin lokasi ditolak")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLat = location.latitude.toFloat()
                    currentLon = location.longitude.toFloat()
                    binding.locationStatus.visibility = View.VISIBLE
                    binding.locationStatus.text = "Lokasi ditambahkan: ${location.latitude}, ${location.longitude}"
                } else {
                    showToast("Gagal mendapatkan lokasi")
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!allPermissionsGranted()) {
            requestPermissionLauncer.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.idButtonAdd.setOnClickListener { uploadImage() }

        val factory = ViewModelFactory.getInstance(this@UploadStoryActivity)
        viewModel = ViewModelProvider(this@UploadStoryActivity, factory)[UploadStoryViewModel::class.java]

        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    getCurrentLocation()
                }
            } else {
                currentLat = null
                currentLon = null
                binding.locationStatus.visibility = View.GONE
            }
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            lifecycleScope.launch {
                val imageFile = uriToFile(uri, this@UploadStoryActivity).reduceFileImage()
                val description = binding.edAddDescription.text.toString()

                viewModel.uploadImage(imageFile, description, currentLat, currentLon)
                    .observe(this@UploadStoryActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> showLoading(true)
                                is Result.Success -> {
                                    result.data.message?.let { showToast(it) }
                                    val intent = Intent(this@UploadStoryActivity, HomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    showLoading(false)
                                    Log.d("UploadStory", "Lat: $currentLat, Lon: $currentLon")
                                }
                                is Result.Error -> {
                                    showToast(result.error)
                                    showLoading(false)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(this, getString(R.string.select_media), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Toast.makeText(this, getString(R.string.iamge_show), Toast.LENGTH_SHORT).show()
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}