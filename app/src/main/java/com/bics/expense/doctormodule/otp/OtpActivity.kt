package com.bics.expense.doctormodule.otp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.Login.LoginRequest
import com.bics.expense.doctormodule.Login.LoginResponse
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.dashboard.DashboardActivity
import com.bics.expense.doctormodule.databinding.ActivityOtpBinding
import com.quickblox.chat.QBChatService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding
    private  var userId: String? = null
    private lateinit var users: ArrayList<QBUser>
    private var opponents: ArrayList<QBUser>? = null
    private val qbChatService: QBChatService = QBChatService.getInstance()
    val isLoggedIn: Boolean
        get() = QBChatService.getInstance().isLoggedIn


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        userId = intent.getStringExtra("User_Id")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbars.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.buttonLogin.setOnClickListener {
            val passwordError = validatePassword()

            binding.textInputLayoutPassword.helperText = passwordError

            if ( passwordError == null) {
                performLogin()
            }
        }
    }



        private fun validatePassword(): String? {
            val userIdPassword = binding.editTextLoginOtp.text.toString()
            return if (userIdPassword.isEmpty()) {
                "Please enter a password"
            } else {
                null
            }
        }

    private fun performLogin() {

        binding.progressBar.visibility = View.VISIBLE

        val apiClient = RetrofitClient.initialize()
        val otp = binding.editTextLoginOtp.text.toString().trim()
        val loginRequest = LoginRequest(userId, otp)

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val call: Response<LoginResponse> =
                    apiClient.apiService.login(loginRequest).execute()

                withContext(Dispatchers.Main) {

                    if (call.isSuccessful) {

                        val loginResponse = call.body()


                        if (loginResponse?.success == true) {

                            val userData = loginResponse.data
                            val token = userData?.token

                            val sharedPreferences = getSharedPreferences("your_preference_name", MODE_PRIVATE)
                            sharedPreferences.edit().putString("token", token).apply()

                            Toast.makeText(this@OtpActivity, "You have successfully logged in", Toast.LENGTH_LONG).show()
                            binding.errorMessage.visibility = View.GONE

                            loginQuickBloxUser()
                            finish()

                            val dashboardIntent = Intent(this@OtpActivity, DashboardActivity::class.java)

                            dashboardIntent.putExtra("firstName", userData?.firstName)
                            dashboardIntent.putExtra("lastName", userData?.lastName)
                            dashboardIntent.putExtra("role", userData?.role)
                            dashboardIntent.putExtra("profileImage", userData?.profileImage)

                            startActivity(dashboardIntent)


                        } else {
                            // Handle unsuccessful login response (show error message)
                            val errorMessage = loginResponse?.error ?: "Unknown error"


                            if (errorMessage.isNotEmpty()) {
                                when {
                                    errorMessage.contains("Account with above user id is not found") -> {
                                        binding.errorMessage.text = "Account user not found"
                                        binding.errorMessage.visibility = View.VISIBLE
                                        Toast.makeText(
                                            this@OtpActivity,
                                            errorMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    errorMessage.contains("Invalid credentials") -> {
                                        binding.errorMessage.text = "Invalid credentials"
                                        binding.errorMessage.visibility = View.VISIBLE
                                        Toast.makeText(
                                            this@OtpActivity,
                                            errorMessage,
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }

                                    else -> {
                                        binding.errorMessage.text = errorMessage
                                        binding.errorMessage.visibility = View.VISIBLE
                                        Toast.makeText(
                                            this@OtpActivity,
                                            errorMessage,
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }
                                }
                            } else {
                                binding.errorMessage.visibility = View.GONE
                            }

                            binding.progressBar.visibility = View.GONE

                        }
                    } else {
                        Toast.makeText(
                            this@OtpActivity, "Error: ${call.code()}", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@OtpActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }

        }
    }

    fun loginQuickBloxUser() {

        val userLogin = QBUser().apply {
            login = userId  // Set login field correctly
            password = "Bics@123"
        }
        QBUsers.signIn(userLogin).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(user: QBUser?, bundle: Bundle?) {
                // Handle the success case here
                // For example, you can retrieve user details or navigate to another screen
                user?.let {
                    Log.d("QuickBlox", "Login successful: ${it.id}")


                }
            }
            override fun onError(exception: QBResponseException?) {
                // Handle the error case here
                exception?.let {
                    Log.e("QuickBlox", "Login error: ${it.message}")
                }
            }
        })
    }


}

