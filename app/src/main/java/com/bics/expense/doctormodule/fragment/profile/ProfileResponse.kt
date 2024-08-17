package com.bics.expense.receptionistmodule.fragment.profile


data class ProfileResponse(
    val accountID: String,
    val userID: String,
    val clinicID: String?,
    val firstName: String,
    val lastName: String,
    val profileImage: String,
    val email: String,
    val phone: String,
    val role: String
)
