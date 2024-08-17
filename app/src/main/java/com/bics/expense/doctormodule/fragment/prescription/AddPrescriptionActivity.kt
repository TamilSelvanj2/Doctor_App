package com.bics.expense.doctormodule.fragment.prescription

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.databinding.ActivityAddPrescriptionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPrescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPrescriptionBinding
    private val medicine = ArrayList<Medicine>()
    private val dosageList = listOf("01", "02", "03", "04", "05")
    private val  durationList = listOf("1 Day", "2 Days", "3 Days", "4 Days", "5 Days", "6 Days", "1 Week", "2 Weeks", "3 Weeks", "1 Month")
    private lateinit var appointmentID: String
    private var medicineID: String? = null // Changed to nullable type
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_prescription)

         appointmentID = intent.getStringExtra("APPOINTMENT_ID").toString()


        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Medicine Info"
        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        getMedicine()
        setDay()
        setDays()
        binding.addDrugbtn.setOnClickListener {
            addPrescription()
        }
        val dosageAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, dosageList)
        binding.editTextDosageName.setAdapter(dosageAdapter)

        val durationAdapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,durationList)
        binding.editTextDuration.setAdapter(durationAdapter)

    }

    private fun getMedicine() {

        val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        RetrofitClient.setAuthToken(token)

        RetrofitClient.apiService.getAllMedicine("Bearer $token").enqueue(object :
            retrofit2.Callback<AddPrecriptionRequest> {
            override fun onResponse(
                call: Call<AddPrecriptionRequest>,
                response: Response<AddPrecriptionRequest>
            ) {
                if (response.isSuccessful) {
                    val Medicine = response.body()?.data
                    Medicine?.let {
                        medicine.addAll(it)
                        val medicineName = it.map { Medicine -> Medicine.medicineName }
                         medicineID = it.map { Medicine -> Medicine.medicineID }.toString()

                        val adapter = ArrayAdapter(
                            this@AddPrescriptionActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            medicineName
                        )
                        binding.editTextMedicationName.setAdapter(adapter)
                    }
                } else {
                    Toast.makeText(
                        this@AddPrescriptionActivity,
                        "Failed to fetch specialities",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<AddPrecriptionRequest>, t: Throwable) {
                Toast.makeText(
                    this@AddPrescriptionActivity,
                    "Failed to fetch specialities",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun setDay() {
        binding.everyBtn.setOnClickListener { updateButtonBackground(binding.everyBtn) }
        binding.alternativeBtn.setOnClickListener { updateButtonBackground(binding.alternativeBtn) }
        binding.sepcificBtn.setOnClickListener { updateButtonBackground(binding.sepcificBtn) }

    }
    private fun  setDays(){
        binding.morningBtn.setOnClickListener { updateButtonBackground(binding.morningBtn) }
        binding.noonBtn.setOnClickListener { updateButtonBackground(binding.noonBtn) }
        binding.nightBtn.setOnClickListener { updateButtonBackground(binding.nightBtn) }
    }

    private fun updateButtonBackground(selectedButton: Button) {
        selectedButton.setBackgroundResource(R.drawable.button_click_focus)

        val buttons = listOf(binding.everyBtn, binding.alternativeBtn, binding.sepcificBtn,binding.morningBtn,binding.noonBtn,binding.nightBtn)
        buttons.forEach { button ->
            if (button != selectedButton) {
                button.setBackgroundResource(R.drawable.timebuttonbackground)
            }
        }
    }

    private fun addPrescription() {
        if (medicineID == null) {
            Toast.makeText(this, "Medicine ID is not initialized.", Toast.LENGTH_LONG).show()
            return
        }

        val selectedMedicine = binding.editTextMedicationName.text.toString()
        val selectedDosage = binding.editTextDosageName.text.toString()
        val selectedDuration = binding.editTextDuration.text.toString()
        val repeat = when {
            binding.everyBtn.isSelected -> "Every"
            binding.alternativeBtn.isSelected -> "Alternative"
            else -> "Specific"
        }

        if (selectedDuration == null) {
            Toast.makeText(this, "Please enter a valid duration.", Toast.LENGTH_LONG).show()
            return
        }

        val request = AddPrecriptionRequests(
            medicineId = medicineID!!, // You might want to fetch and assign the actual ID
            medicineName = selectedMedicine,
            dosage = selectedDosage,
            duration = selectedDuration.toInt(),
            repeat = repeat,
            morning = binding.morningBtn.isSelected,
            afterNoon = binding.noonBtn.isSelected,
            evening = binding.nightBtn.isSelected,
            beforeFood = binding.beforeFood.isSelected,
            afterFood = binding.afterFood.isSelected,
            appointmentID = appointmentID
        )

        val token = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE).getString("token", "")
        RetrofitClient.apiService.addPrescription("Bearer $token", request).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddPrescriptionActivity, "Prescription added successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@AddPrescriptionActivity, "Failed to add prescription", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddPrescriptionActivity, "Failed to add prescription", Toast.LENGTH_LONG).show()
            }
        })
    }
}