package com.bics.expense.doctormodule.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.databinding.ActivityAppointmentsBinding

class AppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointments)


        setSupportActionBar(binding.toolbarheads)

        supportActionBar?.title = "APPOINTMENT DETAILS"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarheads.setNavigationOnClickListener {
            onBackPressed()
        }

        // Enable the back button


        if (savedInstanceState == null) {
            val appointmentID = intent.getStringExtra("APPOINTMENT_ID")
            val fragment = SecondFragment().apply {
                arguments = Bundle().apply { putString("APPOINTMENT_ID", appointmentID)
                }
            }
            setFragment(fragment)
        }
    }

    // Function to set the fragment
    private fun setFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragments_containers, fragment)
        fragmentTransaction.commit()
    }
}