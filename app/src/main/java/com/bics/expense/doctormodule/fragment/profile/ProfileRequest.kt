package com.bics.expense.doctormodule.fragment.profile

data class ProfileRequest(
    val success: Boolean,
    val error: String,
    val data: ProfileResponse,
    val statusCode: Int
)