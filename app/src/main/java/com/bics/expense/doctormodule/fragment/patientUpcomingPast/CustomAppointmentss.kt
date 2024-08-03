package com.bics.expense.doctormodule.fragment.patientUpcomingPast


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class CustomAppointment(
    val success: Boolean,
    val error: String,
    val data: CustomAppointmentData?,
    val statusCode: Int
): Parcelable
@Parcelize

data class CustomAppointmentData(
    val appointmentID: String,
    val upcomingAppointmentStatusID: Int,
    val currentDateAndTime: String,
    val upcomingAppointmentDateAndTime: String,
    val doctorAvailablityTime: String,
    val doctorAcceptTime: String,
    val consultingFees: String,
    val appointmentDetails: CustomAppointmentDetails
): Parcelable
@Parcelize

data class CustomAppointmentDetails(
    val upcomingAppointment: List<CustomAppointmentItem>,
    val pastAppointment: List<CustomAppointmentItem>,
    val rejectedAppointment: List<CustomAppointmentItem>
): Parcelable
@Parcelize

data class CustomAppointmentItem(

    val appointmentID: String,
    val patientID: String,
    val doctorID: String,
    val appointmentDate: String,
    val appointmentStartTime: String,
    val appointmentEndTime: String,
    val appointmentStartDateandTime: String,
    val appointmentEndDateandTime: String,
    val statusID: Int,
    val specialityID: Int,
    val notes: String?,
    val notifications: String?,
    val description: String,
    val patientName: String,
    val doctorName: String,
    val consultingFees: Double,
    val speciality: String,
    val status: String,
    val appointmentDatetime: String?,
    val doctorAvailability: String,
    val dateModified: String,
    val email: String,
    val phoneNumber: String,
    val userID: String,
    val dateOfBirth: String,
    val gender: String,
    val age: Int?,
    val currentDateAndTimeIST: String,
    val currentDateAndTimeUTC: String,
    val appointmentScheduleDateandTime: String
) : Parcelable
