package com.bics.expense.doctormodule.Login


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.databinding.ActivityMainBinding
import com.bics.expense.doctormodule.otp.OtpActivity


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications
        } else {
            // Inform user that your app will not show notifications
            Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        checkNotificationPermission()


        binding.buttonLogin.setOnClickListener {
            val userIDError = validateUserId()
            binding.usernametextinputlayout.helperText = userIDError
            if (userIDError == null) {

                val userId = binding.editTextLoginId.text.toString()
                val intent = Intent(this@MainActivity, OtpActivity::class.java)
                intent.putExtra("User_Id", userId)
                startActivity(intent)


            }

        }

    }

    private fun validateUserId(): String? {
        val userIdLogin = binding.editTextLoginId.text.toString()
        return when {
            userIdLogin.isEmpty() -> "Please enter a userID"
            userIdLogin.length < 10 -> "Minimum 10 characters required for userID"
            else -> null
        }
    }

}
















//    private fun checkNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            when {
//                ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED -> {
//                    // FCM SDK (and your app) can post notifications
//                }
//
//                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
//                    // Display an educational UI explaining to the user the features that will be enabled
//                    // by granting the POST_NOTIFICATION permission, then request the permission
//                    // once the user understands why your app needs it
//                    Toast.makeText(
//                        this,
//                        "Notification permission is required for updates",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//
//                else -> {
//                    // Directly ask for the permission
//                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                }
//            }
//        }
//    }


//    private fun performLogin() {
//
//        val apiClient = RetrofitClient.initialize()
//
//        val userId = binding.editTextLoginId.text.toString().trim()
//        val otp = binding.editTextLoginOtp.text.toString().trim()
//        val loginRequest = LoginRequest(userId, otp)
//        val errorMessage = "Invalid ID and Password"
//
//        CoroutineScope(Dispatchers.IO).launch {
//
//            try {
//                val call: Response<LoginResponse> =
//                    apiClient.apiService.login(loginRequest).execute()
//
//                withContext(Dispatchers.Main) {
//
//                    if (call.isSuccessful) {
//
//                        val loginResponse = call.body()
//
//
//                        if (loginResponse?.success == true) {
//
//                            val userData = loginResponse.data
//                            val token = userData?.token
//
//                            val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
//                            sharedPreferences.edit().putString("token", token).apply()
//
//                            Toast.makeText(this@MainActivity, "You have successfully logged in", Toast.LENGTH_LONG).show()
//                            binding.errorMessage.visibility = View.GONE
//
//                            val dashboardIntent = Intent(this@MainActivity,DashboardActivity::class.java)
//
//                            dashboardIntent.putExtra("firstName", userData?.firstName)
//                            dashboardIntent.putExtra("role", userData?.role)
//                            dashboardIntent.putExtra("profileImage",userData?.profileImage)
//
//                            startActivity(dashboardIntent)
//
//
//
//                        }
//                        else{
//                            binding.errorMessage.text = errorMessage
//                            binding.errorMessage.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//        }
//    }
//}