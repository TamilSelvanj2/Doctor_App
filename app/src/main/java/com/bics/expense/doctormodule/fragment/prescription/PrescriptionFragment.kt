package com.bics.expense.doctormodule.fragment.prescription

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrescriptionFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrescriptionAdapter // You'll need to create this adapter
    private lateinit var progressBars: ProgressBar
    private lateinit var addPrescription: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prescription, container, false)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")

        Log.d("_AC", "onCreateView: " + "UPCOMING")

        recyclerView = view.findViewById(R.id.PrescriptionRecyclerView)
        adapter = PrescriptionAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        progressBars = view.findViewById(R.id.progressBarPrescription)
        addPrescription = view.findViewById(R.id.addPrescription)
        recyclerView.adapter = adapter

        addPrescription.setOnClickListener {
            val intent = Intent(requireActivity(),AddPrescriptionActivity::class.java)
            intent.putExtra("APPOINTMENT_ID", appointmentID)
            requireActivity().startActivity(intent)
        }



        if (appointmentID != null) {
            fetchPrescription(appointmentID) // Fetch documents for the given appointment ID
        } else {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
        }
        return view
    }


    private fun fetchPrescription(appointmentID: String) {

        progressBars.visibility = View.VISIBLE
        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }
        apiService.getPrescription(appointmentID).enqueue(object : Callback<PrescriptionRequest> {
            override fun onResponse(
                call: Call<PrescriptionRequest>,
                response: Response<PrescriptionRequest>
            ) {
                progressBars.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val upcomingAppointments = response.body()?.data ?: emptyList()
                    adapter.submitList(upcomingAppointments)
                } else {
                    // Handle unsuccessful response
                }
            }
            override fun onFailure(call: Call<PrescriptionRequest>, t: Throwable) {
                progressBars.visibility = View.GONE // Hide progress bar
            }
        })
    }
}

