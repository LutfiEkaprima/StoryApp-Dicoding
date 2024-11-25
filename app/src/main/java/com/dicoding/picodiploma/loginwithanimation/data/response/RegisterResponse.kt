package com.dicoding.picodiploma.loginwithanimation.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("error")
    var error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)