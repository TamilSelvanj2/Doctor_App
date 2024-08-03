package com.bics.expense.doctormodule.fragment.upcoming



data class UpcomingModule(
    val success: Boolean,
    val error: String,
    val data: List<UpcomingResponse>,
    val statusCode: Int
)
