package com.dicoding.picodiploma.loginwithanimation.data

import androidx.datastore.core.IOException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.FileUploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun registerUser(name: String, email: String, password: String): RegisterResponse {
        return try {
            apiService.register(name, email, password)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            RegisterResponse(true, errorResponse.message ?: "Unknown error occurred")
        } catch (e: IOException) {

            RegisterResponse(true, "Network error: ${e.localizedMessage}")
        }
    }
    suspend fun loginUser(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    fun uploadImage(imageFile: File, description: String, lat: Float?, lon: Float?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val token = userPreference.getToken().first()
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("photo", imageFile.name, requestImageFile)
                .addFormDataPart("description", description)

            val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            lat?.let { builder.addFormDataPart("lat", it.toString()) }
            lon?.let { builder.addFormDataPart("lon", it.toString()) }

            val successResponse = ApiConfig.getApiService(token).uploadImage(
                multipartBody,
                requestBody,
                latRequestBody,
                lonRequestBody
            )
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        }
    }

    fun getQuote(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = {
                QuotePagingSource(apiService)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(apiService,userPreference)
                INSTANCE = instance
                instance
            }
        }

    }
}