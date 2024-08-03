package com.bics.expense.doctormodule.appointment.appointments


data class  UpdateAppointmentResponse(
    val success: Boolean,
    val error: String?,
    val data: Any?, // Adjust the type based on the actual response data structure
    val statusCode: Int
)
