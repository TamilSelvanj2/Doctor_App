package com.bics.expense.doctormodule.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(


    @SerializedName("userID")
    @Expose
    val userID: String? = null,
    @SerializedName("otp")
    @Expose
    val otp: String? = null
)