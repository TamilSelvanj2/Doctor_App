package com.bics.expense.doctormodule.fragment.patientUpcomingPast

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PastPatientFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PastPatientAdapter
    private lateinit var progressBar: ProgressBar
    private var patientID: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view = inflater.inflate(R.layout.fragment_past_patient, container, false)
        patientID = arguments?.getString("PATIENT_ID")

        recyclerView = view.findViewById(R.id.PastPatientRecyclerView)
        adapter = PastPatientAdapter(emptyList()) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        progressBar = view.findViewById(R.id.progressBarPastPatient)
        fetchUpcomingAppointments(patientID)
        return view

    }

    fun fetchUpcomingAppointments(
        patientID: String?,
    ) {
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }

        if (patientID != null) {
            apiService.getUpcomingAppointmentDetails(patientID)
                .enqueue(object : Callback<CustomAppointment> {
                    override fun onResponse(
                        call: Call<CustomAppointment>,
                        response: Response<CustomAppointment>
                    ) {
                        progressBar.visibility = View.GONE // Hide progress bar

                        if (response.isSuccessful) {
                            val upcomingAppointments =
                                response.body()?.data?.appointmentDetails?.pastAppointment
                                    ?: emptyList()
                            adapter.submitList(upcomingAppointments)
                        } else {
                            // Handle unsuccessful response
                        }
                    }

                    override fun onFailure(call: Call<CustomAppointment>, t: Throwable) {
                        progressBar.visibility = View.GONE // Hide progress bar

                        // Handle failure
                    }
                })
        }
    }

}
