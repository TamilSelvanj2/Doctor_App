package com.bics.expense.doctormodule.fragment.newRequest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.fragment.upcoming.UpcomingAdapter
import com.bics.expense.doctormodule.fragment.upcoming.UpcomingModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewRequestFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewRequestAdapter // You'll need to create this adapter
    private lateinit var textViewNoDateFound: TextView
    private lateinit var progressBar: ProgressBar

    fun refreshData() {

        fetchNewRequest()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_request, container, false)

        recyclerView = view.findViewById(R.id.newRequestRecyclerView)
        textViewNoDateFound = view.findViewById(R.id.textViewNoDateFound)
        adapter = NewRequestAdapter(emptyList()) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        progressBar = view.findViewById(R.id.progressBarNew)
        recyclerView.adapter = adapter
        fetchNewRequest()
        return view
    }

    fun fetchNewRequest(){

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

        apiService.getNewRequest().enqueue(object : Callback<NewRequestModule> {
            override fun onResponse(
                call: Call<NewRequestModule>,
                response: Response<NewRequestModule>
            ) {
                progressBar.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val upcomingAppointments = response.body()?.data ?: emptyList()
                    adapter.submitList(upcomingAppointments)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<NewRequestModule>, t: Throwable) {
                // Handle failure
                progressBar.visibility = View.GONE // Hide progress bar

            }
        })

    }
}