package com.dicoding.picodiploma.loginwithanimation.view.viewmodel


import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference

class MainViewModel(private val userPreference: UserPreference) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

}