package com.bics.expense.doctormodule.fragment.doctorAppoinment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.fragment.AppointmentDetails
import com.bics.expense.doctormodule.fragment.DoctorDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorAppointmentFragment : Fragment() {

    private lateinit var viewMore: TextView
    private lateinit var documentAdapter: DocumentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var line: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var contract: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_doctor_appointment, container, false)

        val appointmentID = arguments?.getString("APPOINTMENT_ID")


        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressBar = view.findViewById(R.id.progressBarDocument)

        documentAdapter = DocumentAdapter(requireContext(), emptyList(), object : DocumentAdapter.ImageClickListener {
            override fun onImageClicked(imageUrl: String) {
                Toast.makeText(requireContext(), "Image clicked: $imageUrl", Toast.LENGTH_SHORT).show()
            }
        }, this)

        recyclerView.adapter = documentAdapter


        if (appointmentID != null) {
            fetchDocuments(appointmentID) // Fetch documents for the given appointment ID
        } else {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
        }
        return view
    }


        private fun fetchDocuments(appointmentID: String) {

            progressBar.visibility = View.VISIBLE
            if (!isAdded) {
                return
            }

            val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            val apiService = RetrofitClient.apiService

            token?.let { authToken ->
                RetrofitClient.setAuthToken(authToken)
            }
            apiService.getDocuments(appointmentID)
                .enqueue(object : Callback<DocumentResponse> {
                    override fun onResponse(
                        call: Call<DocumentResponse>,
                        response: Response<DocumentResponse>
                    ) {
                        progressBar.visibility = View.GONE

                        if (response.isSuccessful) {
                            val documentResponse = response.body()
                            documentResponse?.let { response ->
                                if (response.success) {
                                    documentAdapter.updateDocuments(response.data)


                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to fetch documents: ${response.error}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } ?: run {
                                Toast.makeText(
                                    requireContext(),
                                    "Empty response body",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch documents: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<DocumentResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE

                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch documents: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
        fun onDocumentDeleted() {
            // Fetch the documents again after a document is deleted
            val appointmentID = arguments?.getString("APPOINTMENT_ID")
            if (appointmentID != null) {
                fetchDocuments(appointmentID)
            }
        }
        // Function to delete document
    }