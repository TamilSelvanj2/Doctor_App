package com.bics.expense.doctormodule.fragment.profile

data class ProfileResponse(
    val accountID: String,
    val userID: String,
    val clinicID: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: String
)
