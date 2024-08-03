package com.bics.expense.doctormodule.appointment.appointments



data class UpdateAppointmentRequest(
    val appointmentID: String,
    val statusID: Int,
    val appointmentDate: String?, // Can be null
    val notes: String
)
