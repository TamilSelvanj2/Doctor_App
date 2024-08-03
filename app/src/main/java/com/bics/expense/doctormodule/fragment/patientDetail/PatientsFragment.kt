package com.bics.expense.doctormodule.fragment.patientDetail




import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientAdapter
    private lateinit var count: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_patients, container, false)
        progressBar = view.findViewById(R.id.progressBarPatientDetails)


        recyclerView = view.findViewById(R.id.recyclerViewPatientDetails)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PatientAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = adapter
        count = view.findViewById(R.id.count)


        loadPatientDetail()
        return view
    }

    private fun loadPatientDetail() {

        progressBar.visibility = View.VISIBLE
        if (!isAdded) {
            return
        }

        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

        val apiServices = RetrofitClient.apiService

        // Set the obtained token as the authentication token
        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }

        apiServices.getPatients().enqueue(object : Callback<PatientResponse> {
            override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData?.success == true) {
                        val patients = responseData.data

                        patients?.let {
                            adapter.updatePatients(it)
                            count.text = "No.of Patient Count: ${it.size}"

                        }
                    } else {
                        // Handle the case where responseData.success is false
                        Toast.makeText(requireContext(), "Failed to get patient details", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle the unsuccessful response (e.g., show a Toast or log the error)
                    Toast.makeText(requireContext(), "Failed to get response", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                t.printStackTrace()
                // Handle the failure (e.g., show a Toast or log the error)
                Toast.makeText(requireContext(), "Failed: " + t.message, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE

            }
        })
    }
}