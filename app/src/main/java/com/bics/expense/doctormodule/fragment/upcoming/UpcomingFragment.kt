package com.bics.expense.doctormodule.fragment.upcoming

import android.content.Context
import android.os.Bundle
import android.util.Log
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

class UpcomingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UpcomingAdapter // You'll need to create this adapter
    private lateinit var progressBars: ProgressBar


    fun refreshData() {
        fetchUpcomingAppointments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_upcoming, container, false)
        Log.d("_AC", "onCreateView: "+"UPCOMING")
        recyclerView = view.findViewById(R.id.upcomingRecyclerView)
        adapter = UpcomingAdapter(emptyList()) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        progressBars = view.findViewById(R.id.progressBarUpcoming)
        recyclerView.adapter = adapter
        fetchUpcomingAppointments()
        return view
    }

    fun fetchUpcomingAppointments() {
        progressBars.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }
        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService

        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }
        apiService.getUpcomingAppointments().enqueue(object : Callback<UpcomingModule> {
            override fun onResponse(
                call: Call<UpcomingModule>,
                response: Response<UpcomingModule>
            ) {
                progressBars.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val upcomingAppointments = response.body()?.data ?: emptyList()
                    adapter.submitList(upcomingAppointments)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<UpcomingModule>, t: Throwable) {
                progressBars.visibility = View.GONE // Hide progress bar
            }
        })
    }
}
