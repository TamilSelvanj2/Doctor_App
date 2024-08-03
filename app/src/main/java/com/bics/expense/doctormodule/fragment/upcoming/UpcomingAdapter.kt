package com.bics.expense.doctormodule.fragment.upcoming

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.dashboard.AppointmentsActivity
import java.text.SimpleDateFormat
import java.util.Locale


class UpcomingAdapter (private var appointments: List<UpcomingResponse>): RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder>() {


    fun submitList(upcomingList: List<UpcomingResponse>) {
        this.appointments = upcomingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.upcoming_cardview, parent, false)
        return UpcomingViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingViewHolder, position: Int) {
        val upcoming = appointments[position]
        holder.bind(upcoming)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class UpcomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentId: TextView = itemView.findViewById(R.id.textViewAppointmentIdUpcoming)
        private val patientName: TextView = itemView.findViewById(R.id.textViewPatientNamesUpcoming)
        private val speciality: TextView = itemView.findViewById(R.id.textViewSpecialitysUpcoming)
        private val status: TextView = itemView.findViewById(R.id.textViewStatusUpcoming)
        private val date: TextView = itemView.findViewById(R.id.dateUpcoming)
        private val time: TextView = itemView.findViewById(R.id.timeUpcoming)

        fun bind(appointment: UpcomingResponse) {

            val startDateTime = appointment.appointmentStartDateAndTime ?: ""
            val endDateTime = appointment.appointmentEndDateAndTime ?: ""

            val startTime = extractTime(startDateTime)
            val endTime = extractTime(endDateTime)

            // Format the time range
            val timeRange = "$startTime - $endTime"
            val resources = itemView.context.resources



            appointmentId.text = appointment.appointmentID
            patientName.text = ":  ${appointment.patientName}"
            speciality.text = ":  ${appointment.speciality}"
            status.text = appointment.status
            date.text = appointment.appointmentDate
            time.text = timeRange

            if (appointment.status.equals("paymentCompleted", ignoreCase = true)) {
                status.text = ": CASH"
            } else {
                status.text = appointment.status
            }

            itemView.setOnClickListener {

                val intent = Intent(itemView.context, AppointmentsActivity::class.java)
                intent.putExtra("APPOINTMENT_ID", appointment.appointmentID)

                itemView.context.startActivity(intent)
            }
        }
    }

    fun extractTime(dateTime: String): String {
        if (dateTime.isBlank()) return ""

        try {
            // Parse the complete date and time string
            val inputFormat = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.getDefault())
            val parsedDate = inputFormat.parse(dateTime)

            // Format the parsed time to "h:mm a"
            val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            return outputFormat.format(parsedDate)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}

