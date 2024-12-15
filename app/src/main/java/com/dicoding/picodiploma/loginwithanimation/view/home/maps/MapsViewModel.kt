package com.dicoding.picodiploma.loginwithanimation.view.home.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryResponse
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapsViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _listStoryItem = MutableLiveData<ListStoryResponse>()
    val listStoryItem: MutableLiveData<ListStoryResponse> = _listStoryItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun findmapsStoryItem() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = userPreference.getToken().first()

                _listStoryItem.value = ApiConfig.getApiService(token).getStoriesWithLocation()
            } catch (e: Exception) {
                _listStoryItem.postValue(
                    ListStoryResponse(
                        listOf(),
                        true,
                        "Unexpected error: ${e.localizedMessage}"
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}