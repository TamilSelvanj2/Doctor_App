package com.bics.expense.doctormodule.dashboard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.fragment.patientUpcomingPast.PastPatientFragment
import com.bics.expense.doctormodule.fragment.patientUpcomingPast.UpcomingPatientFragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PatientUpcomingPastFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private lateinit var adapter: PatientUpcomingPastAdapter
    private lateinit var appointmentBooking: UpcomingPatientFragment
    private lateinit var newRequestPatient: PastPatientFragment
    private lateinit var patientname: TextView
    private lateinit var Patientdate: TextView
    private lateinit var Patientage: TextView
    private lateinit var patientId: TextView
    private lateinit var patientGender: TextView
    private lateinit var patientNumber: TextView
    private lateinit var patientEmail: TextView
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_patient_upcoming_past, container, false)

        patientname = view.findViewById(R.id.textViewPatientName)
        Patientdate = view.findViewById(R.id.textViewDate)
        Patientage = view.findViewById(R.id.textViewAge)
        patientId = view.findViewById(R.id.textViewPatientId)
        patientGender = view.findViewById(R.id.textViewGender)
        patientNumber = view.findViewById(R.id.textViewMobilNumber)
        patientEmail = view.findViewById(R.id.textViewEmailID)
        progressBar = view.findViewById(R.id.progressBarPatientUpcomingPast)
        tabLayout = view.findViewById(R.id.tabLayoutUpcomingPast)
        viewPager = view.findViewById(R.id.viewPagerUpcomingPast)
        adapter = PatientUpcomingPastAdapter(childFragmentManager)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")
        val patientID = arguments?.getString("PATIENT_ID") ?: ""


        // Create and pass the appointment ID to each fragment
        appointmentBooking = UpcomingPatientFragment().apply {
            arguments = Bundle().apply {
                putString("PATIENT_ID", patientID)
            }
        }

        newRequestPatient = PastPatientFragment().apply {
            arguments = Bundle().apply {
                putString("PATIENT_ID", patientID)
            }
        }

        adapter.addFragments(appointmentBooking, "Upcoming")
        adapter.addFragments(newRequestPatient, "Patient")
        viewPager.adapter = adapter

        // Connect viewPager with tabLayout
        tabLayout.setupWithViewPager(viewPager)

        fetchPatientDetails(patientID)

        return view
    }


    private fun fetchPatientDetails(patientID: String?) {
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }
        if (patientID != null) {
            apiService.getPatientDetails(patientID).enqueue(object :
                Callback<PatientDetailsModel> {
                override fun onResponse(
                    call: Call<PatientDetailsModel>,
                    response: Response<PatientDetailsModel>
                ) {
                    progressBar.visibility = View.GONE // Hide progress bar

                    if (response.isSuccessful) {
                        val patientDetailsResponse = response.body()
                        if (patientDetailsResponse?.success == true) {
                            val patientDetails = patientDetailsResponse.data
                            patientname.text = patientDetails.name

                            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // adjust based on server date format
                            val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            val date: Date? = inputDateFormat.parse(patientDetails.dateOfBirth)
                            val formattedDate = date?.let { outputDateFormat.format(it) } ?: patientDetails.dateOfBirth
                            Patientdate.text = formattedDate

                            Patientage.text = patientDetails.age
                            patientId.text = patientDetails.userID
                            patientGender.text = patientDetails.gender
                            patientNumber.text = patientDetails.phone
                            patientEmail.text = patientDetails.email
                        } else {
                            // Handle error response
                            // You can show an error message using patientDetailsResponse?.error
                        }
                    }
                }



                override fun onFailure(call: Call<PatientDetailsModel>, t: Throwable) {
                    // Handle failure
                    progressBar.visibility = View.GONE // Hide progress bar

                }
            })
        }
    }
}
