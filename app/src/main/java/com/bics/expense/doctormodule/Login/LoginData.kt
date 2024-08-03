package com.bics.expense.doctormodule


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("accountID")
    @Expose
    var accountID: String? = null,

    @SerializedName("userID")
    @Expose
    var userID: String? = null,

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null,

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("token")
    @Expose
    var token: String? = null,

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null,

    @SerializedName("role")
    @Expose
    var role: String? = null,

    @SerializedName("profileImage")
    @Expose
    var profileImage : String? = null  ,

    @SerializedName("quickBlox_UserID")
    @Expose
    var quickBlox_UserID : String? = null  ,

    @SerializedName("quickBlox_Password")
    @Expose
    var quickBlox_Password : String? = null


)
