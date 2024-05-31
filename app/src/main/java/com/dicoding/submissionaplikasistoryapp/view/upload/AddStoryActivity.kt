package com.dicoding.submissionaplikasistoryapp.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.submissionaplikasistoryapp.R
import com.dicoding.submissionaplikasistoryapp.data.di.*
import com.dicoding.submissionaplikasistoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.submissionaplikasistoryapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.File
import com.dicoding.submissionaplikasistoryapp.data.Result
import com.dicoding.submissionaplikasistoryapp.view.ViewModelFactory
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun isPermissionsGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            }

            else -> {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this@AddStoryActivity) { session ->
            binding.btnUpload.setOnClickListener { postStory(session.token) }
        }

        if (!isPermissionsGranted(Manifest.permission.CAMERA)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnOpenFile.setOnClickListener { startGallery() }
        binding.btnTakePicture.setOnClickListener { startCamera() }
        binding.checkBox.setOnClickListener {
            if (!isPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION) && !isPermissionsGranted(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                requestPermissionLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                showToast(getString(R.string.location_message))
            }
        }
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
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
            Log.d("Upload media", "Media Tidak ditemukan")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { uri ->
            launcherIntentCamera.launch(uri)
        } ?: run {
            showToast("Failed to create image URI")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAddStory.setImageURI(it)
        }
    }

    private fun postStory(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edtDescStory.text.toString()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                uploadStory(token, imageFile, description)
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val isChecked = binding.checkBox.isChecked
                    val lat = if (isChecked) location.latitude else null
                    val lon = if (isChecked) location.longitude else null
                    uploadStory(token, imageFile, description, lat, lon)
                }
            }
            showLoading(true)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun uploadStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null
    ) {
        viewModel.uploadStories(token, imageFile, description, lat, lon).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showToast(result.data.message)
                        showLoading(false)
                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbAdd.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}