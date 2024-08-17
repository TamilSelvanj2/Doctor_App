package com.bics.expense.doctormodule.fragment.pastHistory

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.appointment.CancelAppointmentResponse
import com.bics.expense.doctormodule.dashboard.AppointmentsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class PastHistoryAdapter(private var appointments: List<PastHistoryResponse>,
                         private val progressBars: ProgressBar,
                         private val context: Context,
                         private val refreshCallback: () -> Unit
) : RecyclerView.Adapter<PastHistoryAdapter.ViewHolder>() {


    fun submitList(appointments: List<PastHistoryResponse>) {
        this.appointments = appointments
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentId: TextView = itemView.findViewById(R.id.textViewAppointmentIdPastHistory)
        private val patientName: TextView = itemView.findViewById(R.id.textViewPatientNamePastHistory)
        private val specliaity: TextView = itemView.findViewById(R.id.textViewSpecialitysPast)
        private val status: TextView = itemView.findViewById(R.id.textViewStatusPastHistory)
        private val date : TextView = itemView.findViewById(R.id.datePastHistory)
        private val time : TextView = itemView.findViewById(R.id.timePastHistory)
        private val cancelBtnPastHistory: TextView = itemView.findViewById(R.id.cancelBtnPastHistory)
        private val linearLayoutPastHistory: LinearLayout = itemView.findViewById(R.id.linearLayoutPastHistory)
        private val wait: TextView = itemView.findViewById(R.id.textViewWaitingNamePast)
        private val linear: LinearLayout = itemView.findViewById(R.id.linearLayout1past)
        private val payment: TextView = itemView.findViewById(R.id.payment)

        init {
            cancelBtnPastHistory.setOnClickListener {
                val appointment = appointments[adapterPosition]
                cancelAppointment(itemView, appointment.appointmentID)
            }
        }



        fun bind(appointment: PastHistoryResponse) {
            val startDateTime = appointment.appointmentStartDateAndTime ?: ""
            val endDateTime = appointment.appointmentEndDateAndTime ?: ""

            val startTime = extractTime(startDateTime)
            val endTime = extractTime(endDateTime)

            // Format the time range
            val timeRange = "$startTime - $endTime"
            val resources = itemView.context.resources



            appointmentId.text = appointment.appointmentID
            specliaity.text = ":  ${appointment.speciality}"
            patientName.text = ":  ${appointment.patientName}"
            date.text = appointment.appointmentDate
            time.text = timeRange

            when (appointment.status) {
                "Accepted" -> {
                    status.text = "Doctor accepted the call but not respond"
                    payment.visibility = View.GONE
                    status.setTextColor(Color.parseColor("#59BBAC"))

                }
                "PreBookAppointment" -> {
                    status.text = "Waiting for doctor acceptance"
                    payment.visibility = View.GONE
                    status.setTextColor(Color.parseColor("#F8C471"))

                }
                "PaymentCompleted" -> {
                    status.text = ": Cash"
                    payment.visibility = View.VISIBLE
                    status.setTextColor(Color.parseColor("#5ABCAD"))
                }
                else -> {
                    status.text = "Unknown status"
                }
            }


            if (appointment.status == "PaymentCompleted") {
                linearLayoutPastHistory.visibility = View.VISIBLE
                cancelBtnPastHistory.visibility = View.GONE
            } else if (appointment.status == "Accepted" || appointment.status == "PreBookAppointment") {
                linearLayoutPastHistory.visibility = View.GONE
                cancelBtnPastHistory.visibility = View.VISIBLE
            }

            if (appointment.patientName.isBlank()) {
                linear.visibility = View.GONE
                wait.visibility = View.VISIBLE // Show the TextView
            } else {
                linear.visibility = View.VISIBLE
                wait.visibility = View.GONE // Hide the TextView
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pasthistory_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position])
    }


    override fun getItemCount(): Int {
        return appointments.size
    }
    private fun cancelAppointment(itemView: View, appointmentID: String) {

        progressBars.visibility = View.VISIBLE

        val apiService = RetrofitClient.apiService
        val call = apiService.cancelAppointment(appointmentID)
        call.enqueue(object : Callback<CancelAppointmentResponse> {
            override fun onResponse(call: Call<CancelAppointmentResponse>, response: Response<CancelAppointmentResponse>
            ) {

                if (response.isSuccessful) {
                    Toast.makeText(itemView.context, "Appointment canceled successfully", Toast.LENGTH_SHORT).show()
                    refreshCallback() // Notify the fragment to refresh the list

                    // Optionally, you can notify the adapter if needed
                    // notifyDataSetChanged()
                } else {
                    Toast.makeText(itemView.context, "Failed to cancel appointment", Toast.LENGTH_SHORT).show()
                }
                progressBars.visibility = View.GONE // Hide progress bar after response
            }
            override fun onFailure(call: Call<CancelAppointmentResponse>, t: Throwable) {
                Toast.makeText(itemView.context, "Failed to cancel appointment: ${t.message}", Toast.LENGTH_SHORT).show()
                progressBars.visibility = View.GONE // Hide progress bar after response

            }
        })
    }

}