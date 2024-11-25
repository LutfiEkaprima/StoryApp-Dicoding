package com.dicoding.picodiploma.loginwithanimation.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _listStoryItem = MutableLiveData<ListStoryResponse>()
    val listStoryItem: MutableLiveData<ListStoryResponse> = _listStoryItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun findListStoryItem() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = userPreference.getToken().first()

                _listStoryItem.value = ApiConfig.getApiService(token).getStories()
            }catch (e: Exception) {
                _listStoryItem.postValue(ListStoryResponse(listOf(),true, "Unexpected error: ${e.localizedMessage}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            userPreference.logout()
        }
    }

}