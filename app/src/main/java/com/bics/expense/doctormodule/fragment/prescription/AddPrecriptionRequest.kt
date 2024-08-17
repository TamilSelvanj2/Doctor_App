package com.bics.expense.doctormodule.fragment.prescription

data class AddPrecriptionRequest(
    val success: Boolean,
    val error: String,
    val data: ArrayList<Medicine>,
    val statusCode: Int
)

data class Medicine(
    val medicineID: String,
    val medicineName: String
)
