package com.bics.expense.doctormodule.fragment.pastHistory



data class PastHistoryResponse(
    val appointmentID: String,
    val appointmentDate: String,
    val doctorName: String,
    val patientName: String,
    val speciality: String,
    val appointmentStartDateAndTime: String,
    val appointmentEndDateAndTime: String,
    val status: String
)