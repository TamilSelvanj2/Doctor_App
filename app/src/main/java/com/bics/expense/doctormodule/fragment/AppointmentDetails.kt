package com.bics.expense.doctormodule.fragment


data class AppointmentDetail(
    var appointmentID: String? = null,
    var patientID: String? = null,
    var doctorID: String? = null,
    var appointmentDate: String? = null,
    var appointmentStartTime: Any? = null,
    var appointmentEndTime: Any? = null,
    var appointmentStartDateandTime: String,
    var appointmentEndDateandTime: String,
    var statusID: Int? = null,
    var specialityID: Int? = null,
    var notes: List<Note>? = null,
    var notifications: Any? = null,
    var description: String? = null,
    var patientName: String? = null,
    var doctorName: String? = null,
    var consultingFees: Double? = null,
    var speciality: String? = null,
    var status: String? = null,
    var appointmentDatetime: Any? = null,
    var doctorAvailability: String? = null,
    var dateModified: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var userID: String? = null,
    var dateOfBirth: String? = null,
    var gender: String? = null,
    var age: String? = null,
    var currentDateAndTimeIST: String? = null,
    var currentDateAndTimeUTC: String,
    var appointmentScheduleDateandTime: String? = null
)

data class Data(
    var appointmentDetail: AppointmentDetail? = null,
    var patientDetail: PatientDetail? = null,
    var doctorDetail: DoctorDetail? = null,
    var prescriptionDetails: List<Any>? = null,
    var appointmentNotesResponse: List<Any>? = null
)

data class DoctorDetail(
    val doctorID: String,
    val userID: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val name: String,
    val email: String,
    val gender: String,
    val phone: String,
    val qualification: String,
    val experience: Double,
    val consultingFees: String,
    val speciality: String,
    val language: String,
    val quickBlox_Id: String? = null,
    val quickBlox_Password: String? = null
)


data class AppointmentDetails(
    var success: Boolean? = null,
    var error: String? = null,
    var data: Data? = null,
    var statusCode: Int? = null
)

data class Note(
    var appointmentNotesID: String? = null,
    var appointmentID: String? = null,
    var fileAttachments: Any? = null,
    var documentTypeID: Any? = null,
    var notes: String= "",
    var role: String? = null,
    var dateCreated: String? = null,
    var documentName: Any? = null,
    var fullDocumentpath: Any? = null,
    var fileName: Any? = null
)

data class PatientDetail(
    var patientID: String? = null,
    var userID: Any? = null,
    var quickblox_Id: Any? = null,
    var quickblox_Password: Any? = null,
    var doctorQuickBlox_Id: Any? = null,
    var firstName: Any? = null,
    var lastName: Any? = null,
    var name: Any? = null,
    var email: Any? = null,
    var phone: Any? = null,
    var dateOfBirth: String? = null,
    var age: Any? = null,
    var gender: Any? = null,
    var createdBy: Any? = null,
    var dateCreated: String? = null
)
