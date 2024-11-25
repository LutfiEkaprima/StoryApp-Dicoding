package com.dicoding.picodiploma.loginwithanimation.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.ErrorResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val repository: UserRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.loginUser(email, password)
                if (!response.error!!) {
                    val user = UserModel(email = email, token = response.loginResult?.token ?: "", isLogin = true)
                    saveSession(user)
                }
                _loginResponse.postValue(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                _loginResponse.postValue(
                    LoginResponse(
                        error = true,
                        message = errorResponse.message ?: "Login Failed"
                    )
                )
            } catch (e: IOException) {
                _loginResponse.postValue(
                    LoginResponse(
                        error = true,
                        message = "Network Error, Check your Internet Connection."
                    )
                )
            } catch (e: Exception) {
                _loginResponse.postValue(
                    LoginResponse(
                        error = true,
                        message = "Unknown Error has Occurred."
                    )
                )
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userPreference.saveSession(user)
        }
    }
}
