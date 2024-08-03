package com.bics.expense.doctormodule.fragment.pastHistory



data class PastHistoryModule(

    val success: Boolean,
    val error: String,
    val data: List<PastHistoryResponse>,
    val statusCode: Int
)