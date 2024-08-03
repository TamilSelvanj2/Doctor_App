package com.bics.expense.doctormodule.dashboard

import com.google.gson.annotations.SerializedName

data class PatientDetailsModel(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String,
    @SerializedName("data") val data: PatientDetailsResponse,
    @SerializedName("statusCode") val statusCode: Int
)

data class PatientDetailsResponse(
    @SerializedName("patientID") val patientID: String,
    @SerializedName("userID") val userID: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("age") val age: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("dateCreated") val dateCreated: String
)
