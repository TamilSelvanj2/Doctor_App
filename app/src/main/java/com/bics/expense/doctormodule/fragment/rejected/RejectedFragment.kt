package com.bics.expense.doctormodule.fragment.rejected

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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


class RejectedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RejectedAdapter // You'll need to create this adapter
    private lateinit var progressBarRejected: ProgressBar

    fun refreshData() {
        val startDate: TextView? = view?.findViewById(R.id.fromDateTextViewRejected)
        val endDate: TextView? = view?.findViewById(R.id.endDateTextViewRejected)
        fetchRejectedAppointments(startDate?.text.toString(), endDate?.text.toString())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rejected, container, false)
        Log.d("_AC", "onCreateView: " + "REJECT")
        progressBarRejected = view.findViewById(R.id.progressBarRejected)


        recyclerView = view.findViewById(R.id.rejectedRecyclerView)
        adapter = RejectedAdapter(emptyList()) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val startDate: TextView = view.findViewById(R.id.fromDateTextViewRejected)
        val endDate: TextView = view.findViewById(R.id.endDateTextViewRejected)
        val searchBtn: Button = view.findViewById(R.id.fetchButtonRejected)

        val defaultStartDate = getOneWeekBeforeDate()
        val defaultEndDate = getTodayDate()
        startDate.text = defaultStartDate
        endDate.text = defaultEndDate

        fetchRejectedAppointments(defaultStartDate, defaultEndDate)

        startDate.setOnClickListener {
            showDatePickerDialog(startDate)
        }
        endDate.setOnClickListener {
            showDatePickerDialog(endDate)
        }

        searchBtn.setOnClickListener {
            val startDateText = startDate.text.toString()
            val endDateText = endDate.text.toString()

            if (startDateText.isNotEmpty() && endDateText.isNotEmpty()) {
                fetchRejectedAppointments(startDateText, endDateText)
            } else {
                // Handle case when start or end date is not selected
            }
        }

        // fetchRejectedAppointments() // Remove this line to prevent auto-fetching on fragment creation
        return view
    }

    fun fetchRejectedAppointments(startDate: String, endDate: String) {

        if (::progressBarRejected.isInitialized) {
            progressBarRejected.visibility = View.VISIBLE
            // Rest of your code
        }
        if (!isAdded) {
            return
        }

        val apiService = RetrofitClient.apiService
        val requestBody = RejectAppoinmentRequest(startDate, endDate)
        apiService.getRejectedAppointments(requestBody).enqueue(object : Callback<RejectedModel> {
            override fun onResponse(call: Call<RejectedModel>, response: Response<RejectedModel>) {

                progressBarRejected.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val rejectedAppointments = response.body()?.data ?: emptyList()
                    adapter.submitList(rejectedAppointments)
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<RejectedModel>, t: Throwable) {
                // Handle failure
                progressBarRejected.visibility = View.GONE // Hide progress bar

            }
        })
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

    private fun showDatePickerDialog(dateEditText: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                // Handle selected date
                val selectedDate =
                    String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                dateEditText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}

