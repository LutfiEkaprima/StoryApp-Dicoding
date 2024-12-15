package com.dicoding.picodiploma.loginwithanimation.view.home.upload

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import java.io.File

class UploadStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadImage(file: File, description: String, lat: Float?, lon: Float?) = repository.uploadImage(file, description, lat, lon)
}