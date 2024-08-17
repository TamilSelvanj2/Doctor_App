package com.bics.expense.doctormodule.otp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bics.expense.doctormodule.Api.RetrofitClient
import com.bics.expense.doctormodule.Login.LoginRequest
import com.bics.expense.doctormodule.Login.LoginResponse
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.dashboard.DashboardActivity
import com.bics.expense.doctormodule.databinding.ActivityOtpBinding
import com.bics.expense.doctormodule.videoCall.DEFAULT_USER_PASSWORD
import com.bics.expense.doctormodule.videoCall.EXTRA_LOGIN_RESULT_CODE
import com.bics.expense.doctormodule.videoCall.LoginService
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
const val ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422

class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding
    private var userId: String? = null


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

            if (passwordError == null) {
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

                        binding.errorMessage.visibility = View.GONE

                        if (loginResponse?.success == true) {

                            val userData = loginResponse.data
                            val token = userData?.token

                            val sharedPreferences =
                                getSharedPreferences("your_preference_name", MODE_PRIVATE)
                            sharedPreferences.edit().putString("token", token).apply()

                            Toast.makeText(
                                this@OtpActivity,
                                "You have successfully logged in",
                                Toast.LENGTH_LONG
                            ).show()

                            binding.progressBar.visibility = View.GONE
                            val quickBlox_docId = userData?.quickBlox_UserID.toString()
                            val quickBlox_pass = userData?.quickBlox_Password.toString()
                            loginQuickBloxUser(quickBlox_docId, quickBlox_pass)

                            val dashboardIntent =
                                Intent(this@OtpActivity, DashboardActivity::class.java)

                            dashboardIntent.putExtra("firstName", userData?.firstName)
                            dashboardIntent.putExtra("lastName", userData?.lastName)
                            dashboardIntent.putExtra("role", userData?.role)
                            dashboardIntent.putExtra("profileImage", userData?.profileImage)

                            startActivity(dashboardIntent)

                        } else {
                            // Handle unsuccessful login response (show error message)
                            val errorMessage = loginResponse?.error ?: "Unknown error"
                            binding.errorMessage.visibility = View.GONE

                            if (errorMessage.isNotEmpty()) {
                                when {
                                    errorMessage.contains("Account with above user id is not found") -> {
                                        binding.errorMessage.text = "Account user not found"
                                        Toast.makeText(
                                            this@OtpActivity,
                                            errorMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    errorMessage.contains("Invalid credentials") -> {
                                        binding.errorMessage.text = "Invalid credentials"
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

    fun loginQuickBloxUser(quickBlox_docID: String, quickBlox_pass: String) {
        val userLogin = QBUser().apply {
            login = userId
            password = quickBlox_pass
        }

        Log.d("Quickblox", "Starting login with credentials: $quickBlox_docID")

        // Try signing up the user first
        signUp(userLogin)
    }

    private fun signUp(user: QBUser) {
        Log.d("Quickblox", "Attempting to sign up user with login: ${user.login}")

        QBUsers.signUp(user).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(result: QBUser, params: Bundle) {
                Log.d("Quickblox", "Sign up successful for user: ${result.login}")
                loginToChat(result)
            }

            override fun onError(e: QBResponseException) {
                Log.e("Quickblox", "Sign up error: ${e.message}")

                if (e.httpStatusCode == ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                    Log.d("Quickblox", "User already exists. Attempting to log in.")
                    loginToRest(user)
                } else {
                    Log.e("Quickblox", "Unhandled sign up error: ${e.httpStatusCode}")
                    // You might want to add additional error handling here
                }
            }
        })
    }

    private fun loginToRest(user: QBUser) {
        Log.d("Quickblox", "Attempting to log in existing user with login: ${user.login}")

        QBUsers.signIn(user).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(user: QBUser?, bundle: Bundle?) {
                Log.d("Quickblox", "Login successful: ${user?.id}")

                if (user != null) {
                    loginToChat(user)
                } else {
                    Log.e("Quickblox", "Login failed: user is null")
                }
            }

            override fun onError(exception: QBResponseException?) {
                Log.e("Quickblox", "Login error: ${exception?.message}")
                // Handle error case, maybe retry or notify the user
            }
        })
    }

    private fun loginToChat(user: QBUser) {
        Log.d("Quickblox", "Logging in to chat with user: ${user.login}")

        user.password = DEFAULT_USER_PASSWORD
        startLoginService(user)
    }

    private fun startLoginService(qbUser: QBUser) {
        Log.d("Quickblox", "Starting LoginService for user: ${qbUser.login}")

        val tempIntent = Intent(this, LoginService::class.java)
        val pendingIntent = createPendingResult(EXTRA_LOGIN_RESULT_CODE, tempIntent, 0)
        LoginService.loginToChatAndInitRTCClient(this, qbUser, pendingIntent)
    }

    private inner class LoginEditTextWatcher internal constructor(private val editText: EditText) :
        TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // No action needed
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            editText.error = null
        }

        override fun afterTextChanged(s: Editable) {
            // No action needed
        }
    }
}
