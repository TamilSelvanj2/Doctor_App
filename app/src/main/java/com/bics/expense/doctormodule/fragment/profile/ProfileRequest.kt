package com.bics.expense.receptionistmodule.fragment.profile

data class ProfileRequest(
    val success: Boolean,
    val error: String,
    val data: ProfileResponse,
    val statusCode: Int
)