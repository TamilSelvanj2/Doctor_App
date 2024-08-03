package com.bics.expense.doctormodule.appointment


data class CancelAppointmentResponse(
    val success: Boolean,
    val error: String,
    val data: AppointmentData?,
    val statusCode: Int
)

data class AppointmentData(
    val message: String
)
