package com.bics.expense.doctormodule.fragment.rejected

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.R


class RejectedAdapter(private var appointments: List<RejectedResponse>) : RecyclerView.Adapter<RejectedAdapter.ViewHolder>() {

    fun submitList(appointments: List<RejectedResponse>) {
        this.appointments = appointments
        notifyDataSetChanged()

    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appointmentId: TextView =
            itemView.findViewById(R.id.textViewAppointmentIdRejected)
        private val patientName: TextView = itemView.findViewById(R.id.textViewPatientNameRejected)
        private val status: TextView = itemView.findViewById(R.id.textViewStatusRejected)
        private val speciality: TextView = itemView.findViewById(R.id.textViewSpeciality)
        private val reason: TextView = itemView.findViewById(R.id.textViewReason)
        private val linear: LinearLayout = itemView.findViewById(R.id.linearLayout5Rejected)
        private val wait: TextView = itemView.findViewById(R.id.textViewWaitingNameRejected)
        private val linearName: LinearLayout = itemView.findViewById(R.id.linearLayout2Rejected)


        fun bind(appointment: RejectedResponse) {
            appointmentId.text = appointment.appointmentID
            patientName.text = ":  ${appointment.patientName}"
            speciality.text = ":  ${appointment.speciality}"
            status.text = appointment.status

            if (appointment.reason.isNullOrEmpty()) {
                reason.visibility = View.GONE
                linear.visibility = View.GONE
            } else {
                reason.visibility = View.VISIBLE
                reason.text = ":  ${appointment.reason}"
                linear.visibility = View.VISIBLE

            }
            if (appointment.doctorName.isBlank()) {
                linearName.visibility = View.GONE
                wait.visibility = View.VISIBLE // Show the TextView
            } else {
                linearName.visibility = View.VISIBLE
                wait.visibility = View.GONE // Hide the TextView
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rejected_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

}
