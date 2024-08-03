package com.bics.expense.doctormodule.fragment.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.Login.MainActivity
import com.bics.expense.doctormodule.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {


    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var userIdTextView: TextView
    private lateinit var button: Button
    private lateinit var roleProfile: TextView


    private var accountID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        // Retrieve required data from SharedPreferences
        val token = sharedPreferences.getString("token", "")
        // Set the token to RetrofitClient
        RetrofitClient.setAuthToken(token)

        firstNameTextView = view.findViewById(R.id.nameEditText)
        lastNameTextView = view.findViewById(R.id.lastNameEditText)
        emailTextView = view.findViewById(R.id.emailEditText)
        phoneTextView = view.findViewById(R.id.phoneEditText)
        userIdTextView = view.findViewById(R.id.userIdEditText)
        roleProfile = view.findViewById(R.id.roleTextProfile)
        button = view.findViewById(R.id.buttonUpdate)

        // Make API call to retrieve profile details
        getProfileDetails()

        button.setOnClickListener {
            updateProfileDetails()
        }

        return view
    }


    private fun getProfileDetails() {
        val apiService = RetrofitClient.apiService
        val call = apiService.getProfileDetails()

        call.enqueue(object : Callback<ProfileRequest> {
            override fun onResponse(
                call: Call<ProfileRequest>,
                response: Response<ProfileRequest>
            ) {
                if (response.isSuccessful) {
                    val profileResponse = response.body()?.data
                    profileResponse?.let {
                        // Update UI with profile details
                        accountID = it.accountID
                        firstNameTextView.text = it.firstName
                        lastNameTextView.text = it.lastName
                        emailTextView.text = it.email
                        phoneTextView.text = it.phone
                        userIdTextView.text = it.userID
                        roleProfile.text = it.role

                    } ?: run {
                        // Handle null response
                        showToast("Failed to get profile details")
                    }
                } else {
                    // Handle unsuccessful response
                    showToast("Failed to get profile details")
                }
            }

            override fun onFailure(call: Call<ProfileRequest>, t: Throwable) {
                // Handle failure
                showToast("Failed to get profile details: ${t.message}")
            }
        })
    }

    private fun updateProfileDetails() {
        val newFirstName = firstNameTextView.text.toString()
        val newLastName = lastNameTextView.text.toString()

        accountID?.let { accountId ->
            val apiService = RetrofitClient.apiService
            val call = apiService.updateProfileDetails(
                ProfileUpdateRequest(
                    accountID = accountId,
                    firstName = newFirstName,
                    lastName = newLastName
                )
            )

            call.enqueue(object : Callback<ProfileUpdateRequest> {
                override fun onResponse(
                    call: Call<ProfileUpdateRequest>,
                    response: Response<ProfileUpdateRequest>
                ) {
                    if (response.isSuccessful) {
                        // Update successful, handle response
                        val updateResponse = response.body()
                        showToast("Profile updated successfully")
                        logoutUser()

                    } else {
                        // Handle unsuccessful response
                        showToast("Failed to update profile")
                    }
                }

                override fun onFailure(call: Call<ProfileUpdateRequest>, t: Throwable) {
                    // Handle failure
                    showToast("Failed to update profile: ${t.message}")
                }
            })
        }
    }
    private fun logoutUser() {
        val sharedPreferences = requireActivity().getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all session data
        editor.apply()

        // Navigate to the login screen
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // Close the current activity
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}