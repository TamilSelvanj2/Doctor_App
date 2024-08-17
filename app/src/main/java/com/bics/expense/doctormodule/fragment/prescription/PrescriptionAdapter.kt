package com.bics.expense.doctormodule.fragment.prescription

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.R

class PrescriptionAdapter  (private var appointments: List<PrescriptionResponse>): RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>() {


    fun submitList(upcomingList: List<PrescriptionResponse>) {
        this.appointments = upcomingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.prescription_card_view, parent, false)
        return PrescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
        val upcoming = appointments[position]
        holder.bind(upcoming)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class PrescriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tabletName: TextView = itemView.findViewById(R.id.textViewTabletName)
        private val timeOfTablet: TextView = itemView.findViewById(R.id.textViewTimeOfTablet)
        private val daysOfTablet: TextView = itemView.findViewById(R.id.textViewDaysOfTablet)
        private val dosage: TextView = itemView.findViewById(R.id.textViewDosage)
        private val night: TextView = itemView.findViewById(R.id.night)
        private val afternoon: TextView = itemView.findViewById(R.id.afternoon)
        private val morning: TextView = itemView.findViewById(R.id.morning)

        fun bind(appointment: PrescriptionResponse) {

            val medicineNames = "${appointment.medicineName} ${appointment.medicineType}"

            tabletName.text = medicineNames.toString()
            daysOfTablet.text = appointment.instruction
            dosage.text = appointment.dosage


            val times = mutableListOf<String>()
            if (appointment.afterFood == true) times.add("afterFood")
            if (appointment.beforeFood == true) times.add("beforeFood")
            timeOfTablet.text = times.joinToString(", ")


            val morningList = mutableListOf<String>()
            val afternoonList = mutableListOf<String>()
            val nightList = mutableListOf<String>()

            if (appointment.morning == true) morningList.add("1")
            if (appointment.morning == false) morningList.add("0")
            if (appointment.afternoon == true) afternoonList.add("1")
            if (appointment.afternoon == false) afternoonList.add("0")
            if (appointment.evening == true) nightList.add("1")
            if (appointment.evening == false) nightList.add("0")

            morning.text = morningList.joinToString(", ")
            afternoon.text = afternoonList.joinToString(", ")
            night.text = nightList.joinToString(", ")


        }
    }
}