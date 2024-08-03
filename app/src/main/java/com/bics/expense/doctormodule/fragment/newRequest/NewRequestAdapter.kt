package com.bics.expense.doctormodule.fragment.newRequest

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.dashboard.AppointmentsActivity


class NewRequestAdapter (private var appointments: List<NewRequestResponse>): RecyclerView.Adapter<NewRequestAdapter.NewRequestViewHolder>() {


    fun submitList(upcomingList: List<NewRequestResponse>) {
        this.appointments = upcomingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.newrequest_cardview, parent, false)
        return NewRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewRequestViewHolder, position: Int) {
        val upcoming = appointments[position]
        holder.bind(upcoming)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class NewRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentId: TextView = itemView.findViewById(R.id.textViewAppointmentIdNewRequest)
        private val patientName: TextView = itemView.findViewById(R.id.textViewPatientNameNewRequest)
        private val speciality: TextView = itemView.findViewById(R.id.textViewSpecialityNewRequest)
        private val status: TextView = itemView.findViewById(R.id.textViewStatusNewRequest)
        private val linear: LinearLayout = itemView.findViewById(R.id.linearLayout1NewRequest)
        private val wait: TextView = itemView.findViewById(R.id.textViewWaitingNameNewRequest)




        fun bind(appointment: NewRequestResponse) {

            appointmentId.text = appointment.appointmentID
            patientName.text = ":  ${appointment.patientName}"
            speciality.text = ":  ${appointment.speciality}"
            status.text = appointment.status

            when (appointment.status) {
                "Accepted" -> {
                    status.text = " Accepted the call.."
                    status.setTextColor(Color.parseColor("#44D044"))

                }

                "PreBookAppointment" -> {
                    status.text = "Book in Advance"
                    status.setTextColor(Color.parseColor("#FF5F1F"))

                }

                "PaymentCompleted" -> {
                    status.text = "Cash"
                    status.setTextColor(Color.parseColor("#5ABCAD"))

                }
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
    }


