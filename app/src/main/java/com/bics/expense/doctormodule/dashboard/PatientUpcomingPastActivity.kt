package com.bics.expense.doctormodule.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.databinding.ActivityPatientUpcomingPastBinding

class PatientUpcomingPastActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPatientUpcomingPastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_upcoming_past)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "PATIENT DETAILS"

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (savedInstanceState == null) {
            val appointmentID = intent.getStringExtra("APPOINTMENT_ID")
            val patientID = intent.getStringExtra("patientId") ?: ""

            val fragment = PatientUpcomingPastFragment().apply {
                arguments = Bundle().apply {
                    putString("APPOINTMENT_ID", appointmentID)
                    putString("PATIENT_ID", patientID)
                }
            }
            setFragment(fragment)
        }
    }


    // Function to set the fragment
    private fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragments_containerPatient, fragment)
        fragmentTransaction.commit()
    }
}

