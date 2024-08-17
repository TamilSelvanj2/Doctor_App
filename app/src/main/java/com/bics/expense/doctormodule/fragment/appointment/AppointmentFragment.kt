package com.bics.expense.doctormodule.fragment.appointment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.appointment.appointments.UpdateAppointmentRequest
import com.bics.expense.doctormodule.appointment.appointments.UpdateAppointmentResponse
import com.bics.expense.doctormodule.fragment.AppointmentDetails
import com.bics.expense.doctormodule.videoCall.VideoChatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AppointmentFragment : Fragment() {

    private var selectedTime: Int = 0
    private var utcDateTime: String? = null
    private lateinit var accountID: String


    private lateinit var textViewAccountId: TextView
    private lateinit var textViewSpeciailty: TextView
    private lateinit var textViewAppointmentDate: TextView
    private lateinit var textViewAppointmentTime: TextView
    private lateinit var textViewDuration: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var textViewNote: TextView
    private lateinit var customDateTimeBtn: Button
    private lateinit var acceptBtn: Button
    private lateinit var rejectedBtn: Button
    private lateinit var fifteenBtn: Button
    private lateinit var thirtyBtn: Button
    private lateinit var fourtyfiveBtn: Button
    private lateinit var dateTimeTextView: EditText
    private lateinit var cardView: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var videoCall: Button


    private  var qucikBloxPatient: String =""
    private  var  qucikBloxPassword: String = ""
    private  var  qucikBloxDoctor: String = ""
    private  var  userId: String = ""
    private  var  appointmentID: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        // Initialize views
        textViewAccountId = view.findViewById(R.id.textViewAppointmentId)
        textViewSpeciailty = view.findViewById(R.id.textViewSpeciailty)
        textViewAppointmentDate = view.findViewById(R.id.textViewAppointmentDate)
        textViewAppointmentTime = view.findViewById(R.id.textViewAppointment)
        textViewDuration = view.findViewById(R.id.textViewDuration)
        textViewStatus = view.findViewById(R.id.textViewStatus)
        textViewNote = view.findViewById(R.id.textViewNote)
        customDateTimeBtn = view.findViewById(R.id.customDateTimeBtn)
        acceptBtn = view.findViewById(R.id.acceptBtn)
        rejectedBtn = view.findViewById(R.id.rejectedBtn)
        fifteenBtn = view.findViewById(R.id.fifteenBtn)
        thirtyBtn = view.findViewById(R.id.thirtyBtn)
        fourtyfiveBtn = view.findViewById(R.id.fourtyfiveBtn)
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView)
        cardView = view.findViewById(R.id.cardViewSelect)
        progressBar = view.findViewById(R.id.progressBarAppointmentBook)
        videoCall = view.findViewById(R.id.videoCall)



        accountID = arguments?.getString("APPOINTMENT_ID") ?: run {
            Toast.makeText(requireContext(), "Invalid account ID", Toast.LENGTH_SHORT).show()
            return view
        }

        fetchAccountDetails(accountID)

        setupButtonClicks()


        return view
    }

    private fun fetchAccountDetails(accountID: String?) {
        progressBar.visibility = View.VISIBLE // Show progress bar

        if (!isAdded) {
            return
        }

        if (accountID == null) {
            Log.e("AppointmentFragment", "Account ID is null")
            return
        }

        val call = RetrofitClient.apiService.getAppointmentDetails(accountID)
        call.enqueue(object : Callback<AppointmentDetails> {
            override fun onResponse(
                call: Call<AppointmentDetails>,
                response: Response<AppointmentDetails>
            ) {
                if (!isAdded) {
                    return
                }

                progressBar.visibility = View.GONE // Hide progress bar

                if (response.isSuccessful) {
                    val appointmentModel = response.body()

                    appointmentModel?.let {

                        val startTime = extractTime(
                            it.data?.appointmentDetail?. appointmentStartTime?: ""
                        )
                        val endTime =
                            extractTime(it.data?.appointmentDetail?.appointmentEndTime ?: "")

                        // Format the time range
                        val timeRange = if (!startTime.isNullOrBlank() && !endTime.isNullOrBlank()) {
                            "$startTime - $endTime"
                        } else {
                            "-- : --"
                        }

                        textViewAccountId.text = ": ${it.data?.appointmentDetail?.appointmentID}"
                        textViewSpeciailty.text = ": ${it.data?.appointmentDetail?.speciality}"
                        textViewAppointmentDate.text = ": ${it.data?.appointmentDetail?.appointmentDate}"
                        textViewAppointmentTime.text =": ${timeRange}"
                        textViewStatus.text = ": ${it.data?.appointmentDetail?.status}"



                        qucikBloxDoctor = it.data?.doctorDetail?.quickBlox_Id ?: return
                         qucikBloxPassword = it.data?.doctorDetail?.quickBlox_Password ?: return
                        qucikBloxPatient= (it.data?.patientDetail?.quickblox_Id ?: return).toString()
                        appointmentID = it.data?.appointmentDetail?.appointmentID.toString()


                        when {
                            it.data?.appointmentDetail?.status.equals("Accepted", ignoreCase = true) -> {
                                val green = ContextCompat.getColor(requireContext(), R.color.green)
                                textViewStatus.setTextColor(green)
                                cardView.visibility = View.GONE

                            }

                            it.data?.appointmentDetail?.status.equals("PreBookAppointment", ignoreCase = true) -> {
                                val orange =
                                    ContextCompat.getColor(requireContext(), R.color.orange)
                                textViewStatus.text = "Waiting for doctor acceptance "
                                textViewStatus.setTextColor(orange)


                            }
                            it.data?.appointmentDetail?.status.equals("PaymentCompleted", ignoreCase = true) -> {
                                val greenColor =
                                    ContextCompat.getColor(requireContext(), R.color.green)
                                textViewStatus.setTextColor(greenColor)
                                cardView.visibility = View.GONE
                                videoCall.visibility = View.VISIBLE

                                videoCall.setOnClickListener {
                                    val intent = Intent(requireContext(), VideoChatActivity::class.java).apply {
                                         putExtra("APPOINTMENT_ID", appointmentID)
                                        putExtra("QUICKBLOX_DOCTOR_ID", qucikBloxDoctor)
                                        putExtra("QUICKBLOX_PATIENT_ID", qucikBloxPatient)
                                    }
                                    requireContext().startActivity(intent)
                                }
                            }
                            it.data?.appointmentDetail?.status.equals("Rejected", ignoreCase = true) -> {
                                val redColor =
                                    ContextCompat.getColor(requireContext(), R.color.rejectedColor)
                                textViewStatus.setTextColor(redColor)
                            }

                            it.data?.appointmentDetail?.status.equals("Cancelled", ignoreCase = true) -> {
                                val redColor =
                                    ContextCompat.getColor(requireContext(), R.color.rejectedColor)
                                textViewStatus.setTextColor(redColor)

                            }

                            else -> {
                                // Handle other statuses i    intent.putExtra(VideoChatActivity.EXTRA_APPOINTMENT_ID, "APPOINMENT_ID")f necessary

                            }
                        }

                        val notesList = it.data?.appointmentDetail?.notes
                        val notesString = notesList?.joinToString(separator = "\n") { note ->
                            " ${note.notes}"
                        } ?: "No notes available"
                        textViewNote.text = ": ${notesString}"

                        Log.d(
                            "AppointmentFragment",
                            "Appointment Details: ${it.data?.appointmentDetail}"
                        )
                    }
                } else {
                    Log.e(
                        "AppointmentFragment",
                        "Failed to get appointment details: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<AppointmentDetails>, t: Throwable) {
                if (!isAdded) {
                    return
                }

                progressBar.visibility = View.GONE // Hide progress bar
                Log.e("AppointmentFragment", "Error fetching appointment details", t)
            }
        })
    }

    private fun setupButtonClicks() {
        customDateTimeBtn.setOnClickListener {
            updateButtonBackground(customDateTimeBtn)
            dateTimeTextView.visibility = View.VISIBLE
            dateTimeTextView.setOnClickListener{
                showDateTimePicker()
            }

        }

        acceptBtn.setOnClickListener {
            sendAppointmentStatus(2)
        }

        rejectedBtn.setOnClickListener {
            rejcteAppointmentStatus(3)
        }

        fifteenBtn.setOnClickListener {
            selectedTime = 15
            updateButtonBackground(fifteenBtn)
            dateTimeTextView.visibility = View.GONE
            convertAndSendTime()
        }

        thirtyBtn.setOnClickListener {
            selectedTime = 30
            updateButtonBackground(thirtyBtn)
            dateTimeTextView.visibility = View.GONE
            convertAndSendTime()
        }

        fourtyfiveBtn.setOnClickListener {
            selectedTime = 45
            updateButtonBackground(fourtyfiveBtn)
            dateTimeTextView.visibility = View.GONE
            convertAndSendTime()


        }
    }

    private fun updateButtonBackground(selectedButton: Button) {
        selectedButton.setBackgroundResource(R.drawable.button_click_focus)

        val buttons = listOf(fifteenBtn, thirtyBtn, fourtyfiveBtn, customDateTimeBtn)
        buttons.forEach { button ->
            if (button != selectedButton) {
                button.setBackgroundResource(R.drawable.timebuttonbackground)
            }
        }
    }
    private fun convertAndSendTime(): String {
        val currentDateTime = Calendar.getInstance()
        currentDateTime.add(Calendar.MINUTE, selectedTime)

        val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = istTimeZone

        val formattedDateTime = dateFormat.format(currentDateTime.time)
        return formattedDateTime
    }

    private fun showDateTimePicker() {
        val currentDateTime = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        val selectedDate =
                            String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                        val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                        val selectedDateTime = "$selectedDate $selectedTime"
                        dateTimeTextView.setText(selectedDateTime)

                        val utcDateFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        utcDateTime = utcDateFormat.format(
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm",
                                Locale.getDefault()
                            ).parse(selectedDateTime)!!
                        )

                        dateTimeTextView.setText(selectedDateTime)
                        dateTimeTextView.visibility = View.VISIBLE
                    },
                    currentDateTime.get(Calendar.HOUR_OF_DAY),
                    currentDateTime.get(Calendar.MINUTE),
                    true
                ).show()
            },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = currentDateTime.timeInMillis
        val maxDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 7)
        }.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }


    private fun sendAppointmentStatus(status: Int) {
        val dateTimeToSend: String

        if (dateTimeTextView.visibility == View.VISIBLE && utcDateTime != null) {
            dateTimeToSend = utcDateTime!!
        } else {
            dateTimeToSend = convertAndSendTime()
        }

        val appointmentID = arguments?.getString("APPOINTMENT_ID") ?: run {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
            return
        }

        val appointmentRequest = UpdateAppointmentRequest(
            appointmentID = appointmentID,
            statusID = status,
            appointmentDate = dateTimeToSend,
            notes = "string"
        )

        RetrofitClient.apiService.updateAppointment(appointmentRequest)
            .enqueue(object : Callback<UpdateAppointmentResponse> {
                override fun onResponse(
                    call: Call<UpdateAppointmentResponse>,
                    response: Response<UpdateAppointmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.success == true) {

                            Toast.makeText(requireContext(), "Appointment updated successfully", Toast.LENGTH_SHORT).show()
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "${responseBody?.error ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to update appointment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UpdateAppointmentResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun rejcteAppointmentStatus(status: Int) {

        val appointmentID = arguments?.getString("APPOINTMENT_ID") ?: run {
            Toast.makeText(requireContext(), "Invalid appointment ID", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.RoundedTabLayoutStyle)
        val inflater = LayoutInflater.from(requireContext())

        val rejectedView = inflater.inflate(R.layout.appoinmentrejectedcard, null)
        val radioGroup = rejectedView.findViewById<RadioGroup>(R.id.rejected_radioGroup)
        val rejected = rejectedView.findViewById<Button>(R.id.rejectedAccepted)
        val cancel = rejectedView.findViewById<Button>(R.id.btnCancel)

// Add radio buttons from your layout (assuming IDs are set in rejected_card_view.xml)
        val notAvailableRadio = rejectedView.findViewById<RadioButton>(R.id.notAvailable_Radio)
        val emergencyRadio = rejectedView.findViewById<RadioButton>(R.id.Emergency_radio)
        val visitingRadio = rejectedView.findViewById<RadioButton>(R.id.visiting_radio)
        val otherRadio = rejectedView.findViewById<RadioButton>(R.id.other_radio) // Assuming an "Other" radio button exists
        val notes = rejectedView.findViewById<EditText>(R.id.othersNotes) // Assuming an "Other" radio button exists


        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == otherRadio.id) {
                notes.visibility = View.VISIBLE
            } else {
                notes.visibility = View.GONE
            }
        }

        builder.setView(rejectedView)
        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.show()


        cancel.setOnClickListener {
            dialog.dismiss()
        }
        rejected.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            val selectedReason = when (selectedId) {
                notAvailableRadio.id -> notAvailableRadio.text.toString()
                emergencyRadio.id -> emergencyRadio.text.toString()
                visitingRadio.id -> visitingRadio.text.toString()
                otherRadio.id -> notes.text.toString()
                else -> "No reason selected"
            }

                val appointmentRequest = UpdateAppointmentRequest(
                    appointmentID = appointmentID,
                    statusID = status,
                    appointmentDate = null,
                    notes = selectedReason
                )

                RetrofitClient.apiService.updateAppointment(appointmentRequest)
                    .enqueue(object : Callback<UpdateAppointmentResponse> {
                        override fun onResponse(
                            call: Call<UpdateAppointmentResponse>,
                            response: Response<UpdateAppointmentResponse>
                        ) {

                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody?.success == true) {

                                    Toast.makeText(requireContext(), "Appointment updated successfully", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    requireActivity().finish()

                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "${responseBody?.error ?: "Unknown error"}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to update appointment",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<UpdateAppointmentResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
            }
    }

    fun extractTime(timeString: String): String {
        if (timeString.isBlank()) return ""

        return try {
            // Parse the time string
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val parsedTime = inputFormat.parse(timeString)

            // Format the parsed time to "h:mm aa"
            val outputFormat = SimpleDateFormat("h:mm aa", Locale.getDefault())
            outputFormat.format(parsedTime)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
