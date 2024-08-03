package com.bics.expense.doctormodule.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.fragment.appointment.AppointmentFragment
import com.bics.expense.doctormodule.fragment.doctorAppoinment.DoctorAppointmentFragment
import com.bics.expense.doctormodule.fragment.patientAppoinment.PatientAppoinmentFragment
import com.bics.expense.doctormodule.fragment.prescription.PrescriptionFragment
import com.google.android.material.tabs.TabLayout


class SecondFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: SecondAdapter
    private lateinit var appointmentBooking: AppointmentFragment
    private lateinit var patientAppointment: PatientAppoinmentFragment
    private lateinit var doctorDetails: DoctorAppointmentFragment
    private lateinit var prescription: PrescriptionFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        tabLayout = view.findViewById(R.id.tabLayoutNewRequests)
        viewPager = view.findViewById(R.id.viewPagerNewRequests)

        adapter = SecondAdapter(childFragmentManager)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        // Create and pass the appointment ID to each fragment
        appointmentBooking = AppointmentFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        patientAppointment = PatientAppoinmentFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        doctorDetails = DoctorAppointmentFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }
        prescription = PrescriptionFragment().apply {
            arguments = Bundle().apply {
                putString("APPOINTMENT_ID", appointmentID)
            }
        }

        adapter.addFragments(appointmentBooking,"Appointment")
        adapter.addFragments(patientAppointment,"Patient")
        adapter.addFragments(doctorDetails,"Document")
        adapter.addFragments(prescription,"Prescription")



        viewPager.adapter = adapter

        // Connect viewPager with tabLayout
        tabLayout.setupWithViewPager(viewPager)


        return view
    }
}