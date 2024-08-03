package com.bics.expense.doctormodule.fragment.pastHistory

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PastHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PastHistoryAdapter // You'll need to create this adapter
    private lateinit var progressBars: ProgressBar

    fun refreshData() {
        val startDate: TextView? = view?.findViewById(R.id.fromDateTextViewPastHistory)
        val endDate: TextView? = view?.findViewById(R.id.endDateTextViewPastHistory)
        fetchPastAppointments(startDate?.text.toString(), endDate?.text.toString())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_past_history, container, false)
        Log.d("_AC", "onCreateView: "+"PAST")
        progressBars = view.findViewById(R.id.progressBarPast)

        recyclerView = view.findViewById(R.id.pastRecyclerView)
        adapter = PastHistoryAdapter(emptyList(), progressBars, requireContext()) {
            refreshAppointments() // Callback to refresh the list
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        val startDate: TextView = view.findViewById(R.id.fromDateTextViewPastHistory)
        val endDate: TextView = view.findViewById(R.id.endDateTextViewPastHistory   )
        val searchBtn : Button = view.findViewById(R.id.fetchButton)

        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }
        endDate.setOnClickListener {
            showDatePickerDialog(endDate)
        }

        val defaultStartDate = getOneWeekBeforeDate()
        val defaultEndDate = getTodayDate()
        startDate.text = defaultStartDate
        endDate.text = defaultEndDate

        fetchPastAppointments(defaultStartDate, defaultEndDate)


        searchBtn.setOnClickListener {
            val startDateText = startDate.text.toString()
            val endDateText = endDate.text.toString()

            if (startDateText.isNotEmpty() && endDateText.isNotEmpty()) {
                fetchPastAppointments(startDateText, endDateText)
            } else {
                // Handle case when start or end date is not selected
            }
        }
        return view
    }

    private fun getOneWeekBeforeDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun fetchPastAppointments(startDate: String, endDate: String) {

        progressBars.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }


        val sharedPreferences = requireContext().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val apiService = RetrofitClient.apiService
        val requestBody = PastAppointmentsRequest(startDate, endDate)

        // Set the obtained token as the authentication token
        token?.let { authToken ->
            RetrofitClient.setAuthToken(authToken)
        }

        apiService.getPastAppointments(requestBody).enqueue(object : Callback<PastHistoryModule> {
            override fun onResponse(
                call: Call<PastHistoryModule>,
                response: Response<PastHistoryModule>
            ) {
                progressBars.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val pastAppointments = response.body()?.data ?: emptyList()
                    // Update the adapter with past appointments (you might need to adjust your adapter for this)
                    adapter.submitList(pastAppointments)
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(requireContext(), "Failed to get past appointments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PastHistoryModule>, t: Throwable) {
                t.printStackTrace()
                // Handle failure
                progressBars.visibility = View.GONE // Hide progress bar
                Toast.makeText(requireContext(), "Failed: " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showDatePickerDialog(dateEditText: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                // Handle selected date
                val selectedDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                dateEditText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    private fun refreshAppointments() {
        val startDate: TextView? = view?.findViewById(R.id.fromDateTextViewPastHistory)
        val endDate: TextView? = view?.findViewById(R.id.endDateTextViewPastHistory)

        fetchPastAppointments(startDate?.text.toString(), endDate?.text.toString())
    }
}