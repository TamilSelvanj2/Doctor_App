package com.bics.expense.doctormodule.fragment.doctorAppoinment

data class DocumentResponse(
    val success: Boolean,
    val error: String,
    val data: List<AppointmentNote>,
    val statusCode: Int
)

data class AppointmentNote(
    val appointmentNotesID: String,
    val appointmentID: String,
    val fileAttachments: String,
    val documentTypeID: String,
    val notes: String?,
    val role: String,
    val dateCreated: String,
    val documentType: String,
    val fullDocumentpath: String,
    val fileName: String
)
