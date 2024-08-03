package com.bics.expense.doctormodule.fragment.patientUpcomingPast

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.R
import java.text.SimpleDateFormat
import java.util.Locale

class PastPatientAdapter (private var appointments: List<CustomAppointmentItem>) : RecyclerView.Adapter<PastPatientAdapter.ViewHolder>() {
    fun submitList(upcomingList: List<CustomAppointmentItem>) {
        this.appointments = upcomingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.past_patient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int = appointments.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentID: TextView = itemView.findViewById(R.id.textViewAppointmentIdPastPatient)
        private val doctorName: TextView = itemView.findViewById(R.id.textViewDoctorNamePastPatient)
        private val speciality: TextView = itemView.findViewById(R.id.textViewSpecialityPastPatient)
        private val status: TextView = itemView.findViewById(R.id.textViewStatusPastPatient)
        private val date: TextView = itemView.findViewById(R.id.datePatientPast)
        private val time: TextView = itemView.findViewById(R.id.timePastHistory)
        private val payment: TextView = itemView.findViewById(R.id.paymentPatient)
        private val patientName: TextView = itemView.findViewById(R.id.textViewPatientNamePastPatient)

        fun bind(appointment: CustomAppointmentItem) {
            val startDateTime = appointment?.appointmentStartDateandTime ?: ""
            val endDateTime = appointment?.appointmentEndDateandTime ?: ""

            val startTime = extractTime(startDateTime)
            val endTime = extractTime(endDateTime)

            // Format the time range
            val timeRange = "$startTime - $endTime"

            appointmentID.text = appointment.appointmentID
            doctorName.text = ":  ${appointment.doctorName}"
            patientName.text =":  ${ appointment.patientName}"
            speciality.text =":  ${ appointment.speciality}"
            status.text =":  ${appointment.status}"
            date.text = appointment.appointmentDate
            time.text = timeRange

            when (appointment.status) {
                "Accepted" -> {
                    status.text = "Doctor accepted the call but not respond"
                    status.setTextColor(Color.parseColor("#59BBAC"))

                }
                "PreBookAppointment" -> {
                    status.text = "Waiting for doctor acceptance"

                    status.setTextColor(Color.parseColor("#F8C471"))

                }
                "PaymentCompleted" -> {
                    status.text = ": Cash"
                    status.setTextColor(Color.parseColor("#5ABCAD"))
                }
                else -> {
                    status.text = "Unknown status"
                }
            }
        }

        fun  extractTime(dateTime: String): String {
            if (dateTime.isBlank()) return ""

            return try {
                // Parse the input datetime string
                val parsedDate =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).parse(dateTime)

                // Format the parsed date to the desired time format
                SimpleDateFormat("h:mm aa", Locale.getDefault()).format(parsedDate)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}