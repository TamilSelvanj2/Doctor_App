package com.bics.expense.doctormodule.fragment.profile

data class ProfileUpdateRequest(
    val accountID: String,
    val firstName: String,
    val lastName: String
)