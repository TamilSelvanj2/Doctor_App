package com.bics.expense.doctormodule.fragment.patientDetail


import com.google.gson.annotations.SerializedName


data class PatientResponse (

    @SerializedName("success") var success: Boolean,
    @SerializedName("error") var error: String,
    @SerializedName("data") var data: List<PatientModel>?,
    @SerializedName("statusCode") var statusCode: Int
)