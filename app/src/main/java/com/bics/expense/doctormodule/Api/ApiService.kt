package com.bics.expense.doctormodule.Api

import com.bics.expense.doctormodule.Login.LoginRequest
import com.bics.expense.doctormodule.Login.LoginResponse
import com.bics.expense.doctormodule.appointment.CancelAppointmentResponse
import com.bics.expense.doctormodule.appointment.appointments.UpdateAppointmentRequest
import com.bics.expense.doctormodule.appointment.appointments.UpdateAppointmentResponse
import com.bics.expense.doctormodule.dashboard.PatientDetailsModel
import com.bics.expense.doctormodule.fragment.AppointmentDetails
import com.bics.expense.doctormodule.fragment.doctorAppoinment.DocumentResponse
import com.bics.expense.doctormodule.fragment.newRequest.NewRequestModule
import com.bics.expense.doctormodule.fragment.pastHistory.PastAppointmentsRequest
import com.bics.expense.doctormodule.fragment.pastHistory.PastHistoryModule
import com.bics.expense.doctormodule.fragment.rejected.RejectAppoinmentRequest
import com.bics.expense.doctormodule.fragment.rejected.RejectedModel
import com.bics.expense.doctormodule.fragment.upcoming.UpcomingModule
import com.bics.expense.doctormodule.fragment.patientDetail.PatientResponse
import com.bics.expense.doctormodule.fragment.patientUpcomingPast.CustomAppointment
import com.bics.expense.doctormodule.fragment.profile.ProfileRequest
import com.bics.expense.doctormodule.fragment.profile.ProfileUpdateRequest
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

        @POST("/Authentication/DoctorLogin")
        fun login(@Body request: LoginRequest): Call<LoginResponse>

        @GET("/Patient/GetPatientDetailsBasedOnDoctorID")
        fun getPatients(): Call<PatientResponse>


        @POST("/Appointment/GetPastAppointmentDetailsBasedOnDoctorID")
        fun getPastAppointments(@Body requestBody: PastAppointmentsRequest): Call<PastHistoryModule>


        @PUT("/Appointment/CancelAppointment")
        fun cancelAppointment(@Query("AppointmentID") appointmentID: String): Call<CancelAppointmentResponse>

        @POST("/Appointment/GetRejectedAppointmentDetailsBasedOnDoctorID")
        fun getRejectedAppointments(@Body requestBody: RejectAppoinmentRequest): Call<RejectedModel>

        @GET("/Appointment/GetUpcomingAppointmentDetailsBasedOnDoctorID")
        fun getUpcomingAppointments(): Call<UpcomingModule>

        @GET("/Appointment/GetNewAppointmentRequestBasedOnDoctorID")
        fun getNewRequest(): Call<NewRequestModule>

        @GET("/Appointment/GetAppointmentDetailsBasedOnAppointmentID")
        fun getAppointmentDetails(@Query("AppointmentID") appointmentID: String): Call<AppointmentDetails>

        @PUT("/Appointment/UpdateAppointmentRequestByDoctorID")
        fun updateAppointment(@Body request: UpdateAppointmentRequest): Call<UpdateAppointmentResponse>

        @GET("/Authentication/GetProfileDetails")
        fun getProfileDetails(): Call<ProfileRequest>

        @PUT("/Authentication/UpdateProfileDetails")
        fun updateProfileDetails(@Body profileUpdateRequest: ProfileUpdateRequest): Call<ProfileUpdateRequest>

        @HTTP(method = "DELETE", path = "/Document/DeleteDocumentBasedOnAppointmentNotesID", hasBody = true)
        fun deleteDocumentBasedOnAppointmentNotesID(@Body requestBody: RequestBody): Call<Void>

        @GET("Document/GetDocumentsBasedOnAppointmentID")
        fun getDocuments(@Query("AppointmentID") appointmentID: String): Call<DocumentResponse>

        @GET("/Patient/GetPatientDetailsBasedOnPatientID")
        fun getPatientDetails(@Query("PatientID") patientID: String): Call<PatientDetailsModel>

        @GET("/Appointment/GetAppointmentDetailsBasedOnPatientID")
        fun getUpcomingAppointmentDetails(@Query("PatientID") patientID: String): Call<CustomAppointment>

}