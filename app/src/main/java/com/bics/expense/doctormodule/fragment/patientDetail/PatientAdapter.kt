package com.bics.expense.doctormodule.fragment.patientDetail
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.dashboard.PatientUpcomingPastActivity


class PatientAdapter(private var patients: List<PatientModel>) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.patient_card, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.bind(patient)
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    fun updatePatients(newPatients: List<PatientModel>) {
        patients = newPatients
        notifyDataSetChanged()
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_doctor_nameCardView)
        private val emailTextView: TextView = itemView.findViewById(R.id.text_EmailIdCardView)
        private val ageTextView: TextView = itemView.findViewById(R.id.text_ageCardView)
        private val genderTextView: TextView = itemView.findViewById(R.id.text_genderCardView)

        fun bind(patient: PatientModel) {
            nameTextView.text = patient.name
            emailTextView.text = patient.email
            ageTextView.text = patient.age
            genderTextView.text = patient.gender

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, PatientUpcomingPastActivity::class.java)
                intent.putExtra("patientId", patient.patientID)
                intent.putExtra("userId", patient.userID)
                itemView.context.startActivity(intent)

            }
        }
    }
}